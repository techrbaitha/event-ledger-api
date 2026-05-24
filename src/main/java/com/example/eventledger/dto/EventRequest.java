package com.example.eventledger.dto;

import jakarta.validation.constraints.Pattern;
import com.example.eventledger.enums.EventType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRequest {

    @NotBlank(message = "eventId is required")
    private String eventId;

    @NotBlank(message = "accountId is required")
    private String accountId;

    @NotNull(message = "type is required")
    private EventType type;

    @NotNull(message = "amount is required")
    @DecimalMin(
            value = "0.01",
            message = "amount must be greater than 0"
    )
    private BigDecimal amount;

    @NotBlank(
            message = "currency is required"
    )
    @Pattern(
            regexp = "^[A-Z]{3}$",
            message = "currency must be 3 uppercase letters"
    )

    private String currency;

    @NotNull(message = "eventTimestamp is required")
    private Instant eventTimestamp;

    private Map<String, Object> metadata;
}
