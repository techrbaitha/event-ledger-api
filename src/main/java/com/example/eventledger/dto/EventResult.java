package com.example.eventledger.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventResult {

    private EventResponse event;
    private boolean duplicate;
}