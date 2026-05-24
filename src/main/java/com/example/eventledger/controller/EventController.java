package com.example.eventledger.controller;

import com.example.eventledger.dto.EventRequest;
import com.example.eventledger.dto.EventResponse;
import com.example.eventledger.dto.EventResult;
import com.example.eventledger.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService service;

    @PostMapping
    public ResponseEntity<EventResponse>
    createEvent(
            @Valid
            @RequestBody
            EventRequest request
    ) {

        EventResult result =
                service.createEvent(
                        request
                );

        HttpStatus status =
                result.isDuplicate()
                        ? HttpStatus.OK
                        : HttpStatus.CREATED;

        return ResponseEntity
                .status(status)
                .body(
                        result.getEvent()
                );
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponse>
    getEvent(
            @PathVariable
            String eventId
    ) {

        return ResponseEntity.ok(
                service.getByEventId(
                        eventId
                )
        );
    }

    @GetMapping
    public ResponseEntity<
            List<EventResponse>>
    getEventsByAccount(
            @RequestParam
            String account
    ) {

        return ResponseEntity.ok(
                service.getEventsByAccount(
                        account
                )
        );
    }
}