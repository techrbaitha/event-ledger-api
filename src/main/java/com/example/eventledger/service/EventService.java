package com.example.eventledger.service;

import com.example.eventledger.dto.BalanceResponse;
import com.example.eventledger.dto.EventRequest;
import com.example.eventledger.dto.EventResponse;
import com.example.eventledger.dto.EventResult;
import com.example.eventledger.entity.EventEntity;
import com.example.eventledger.exception.EventNotFoundException;
import com.example.eventledger.exception.MetadataException;
import com.example.eventledger.repository.EventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
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
    public EventResult createEvent(
            EventRequest request
    ) {

        var existing =
                repository.findByEventId(
                        request.getEventId()
                );

        if (existing.isPresent()) {

            return EventResult.builder()
                    .event(
                            mapToResponse(
                                    existing.get()
                            )
                    )
                    .duplicate(true)
                    .build();
        }

        try {

            EventEntity entity =
                    EventEntity.builder()
                            .eventId(
                                    request.getEventId()
                            )
                            .accountId(
                                    request.getAccountId()
                            )
                            .type(
                                    request.getType()
                            )
                            .amount(
                                    request.getAmount()
                            )
                            .currency(
                                    request.getCurrency()
                            )
                            .eventTimestamp(
                                    request.getEventTimestamp()
                            )
                            .metadata(
                                    convertMetadataToString(
                                            request.getMetadata()
                                    )
                            )
                            .build();

            EventEntity saved =
                    repository.save(entity);

            return EventResult.builder()
                    .event(
                            mapToResponse(saved)
                    )
                    .duplicate(false)
                    .build();

        } catch (
                DataIntegrityViolationException ex
        ) {

            EventEntity existingEntity =
                    repository.findByEventId(
                            request.getEventId()
                    ).orElseThrow();

            return EventResult.builder()
                    .event(
                            mapToResponse(
                                    existingEntity
                            )
                    )
                    .duplicate(true)
                    .build();
        }
    }

    public EventResponse getByEventId(
            String eventId
    ) {

        EventEntity entity =
                repository.findByEventId(
                                eventId
                        )
                        .orElseThrow(
                                () ->
                                        new EventNotFoundException(
                                                "Event not found: "
                                                        + eventId
                                        )
                        );

        return mapToResponse(entity);
    }

    public List<EventResponse>
    getEventsByAccount(
            String accountId,
            int page,
            int size
    ) {

        return repository
                .findByAccountIdOrderByEventTimestampAsc(
                        accountId,
                        PageRequest.of(
                                page,
                                size
                        )
                )
                .stream()
                .map(
                        this::mapToResponse
                )
                .toList();
    }

    public BalanceResponse getBalance(
            String accountId
    ) {

        BigDecimal balance =
                repository.calculateBalance(
                        accountId
                );

        return BalanceResponse.builder()
                .accountId(accountId)
                .balance(balance)
                .build();
    }

    private EventResponse mapToResponse(
            EventEntity entity
    ) {

        return EventResponse.builder()
                .eventId(
                        entity.getEventId()
                )
                .accountId(
                        entity.getAccountId()
                )
                .type(
                        entity.getType()
                )
                .amount(
                        entity.getAmount()
                )
                .currency(
                        entity.getCurrency()
                )
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

    private String
    convertMetadataToString(
            Map<String, Object> metadata
    ) {

        if (metadata == null) {
            return null;
        }

        try {

            return objectMapper
                    .writeValueAsString(
                            metadata
                    );

        } catch (
                JsonProcessingException e
        ) {

            throw new MetadataException(
                    "Invalid metadata format"
            );
        }
    }

    private Map<String, Object>
    convertMetadataToMap(
            String metadata
    ) {

        if (metadata == null) {
            return null;
        }

        try {

            return objectMapper.readValue(
                    metadata,
                    new TypeReference<>() {
                    }
            );

        } catch (
                Exception e
        ) {

            throw new MetadataException(
                    "Metadata parse failed"
            );
        }
    }
}