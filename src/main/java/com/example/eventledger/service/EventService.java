package com.example.eventledger.service;

import com.example.eventledger.dto.BalanceResponse;
import com.example.eventledger.dto.EventRequest;
import com.example.eventledger.dto.EventResponse;
import com.example.eventledger.entity.EventEntity;
import com.example.eventledger.repository.EventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository repository;
    private final ObjectMapper objectMapper;

    @Transactional
    public EventResponse createEvent(EventRequest request) {

        // Idempotency check
        var existing = repository.findByEventId(
                request.getEventId()
        );

        if (existing.isPresent()) {
            return mapToResponse(existing.get());
        }

        try {

            EventEntity entity =
                    EventEntity.builder()
                            .eventId(request.getEventId())
                            .accountId(request.getAccountId())
                            .type(request.getType())
                            .amount(request.getAmount())
                            .currency(request.getCurrency())
                            .eventTimestamp(request.getEventTimestamp())
                            .metadata(
                                    convertMetadataToString(
                                            request.getMetadata()
                                    )
                            )
                            .build();

            EventEntity saved =
                    repository.save(entity);

            return mapToResponse(saved);

        } catch (DataIntegrityViolationException ex) {

            // concurrency-safe duplicate handling
            EventEntity existingEntity =
                    repository.findByEventId(
                            request.getEventId()
                    ).orElseThrow();

            return mapToResponse(existingEntity);
        }
    }

    public EventResponse getByEventId(
            String eventId
    ) {

        EventEntity entity =
                repository.findByEventId(eventId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Event not found"
                                ));

        return mapToResponse(entity);
    }

    public List<EventResponse> getEventsByAccount(
            String accountId
    ) {

        return repository
                .findByAccountIdOrderByEventTimestampAsc(
                        accountId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public BalanceResponse getBalance(
            String accountId
    ) {

        BigDecimal balance =
                repository.calculateBalance(accountId);

        return BalanceResponse.builder()
                .accountId(accountId)
                .balance(balance)
                .build();
    }

    private EventResponse mapToResponse(
            EventEntity entity
    ) {

        return EventResponse.builder()
                .eventId(entity.getEventId())
                .accountId(entity.getAccountId())
                .type(entity.getType())
                .amount(entity.getAmount())
                .currency(entity.getCurrency())
                .eventTimestamp(
                        entity.getEventTimestamp()
                )
                .metadata(
                        convertMetadataToMap(
                                entity.getMetadata()
                        )
                )
                .build();
    }

    private String convertMetadataToString(
            Map<String, Object> metadata
    ) {

        if (metadata == null) {
            return null;
        }

        try {
            return objectMapper
                    .writeValueAsString(metadata);

        } catch (JsonProcessingException e) {

            throw new RuntimeException(
                    "Invalid metadata"
            );
        }
    }

    private Map<String, Object> convertMetadataToMap(
            String metadata
    ) {

        if (metadata == null) {
            return null;
        }

        try {

            return objectMapper.readValue(
                    metadata,
                    new TypeReference<>() {}
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Metadata parse failed"
            );
        }
    }
}