package com.example.eventledger.controller;

import com.example.eventledger.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository repository;

    @BeforeEach
    void setup() {
        repository.deleteAll();
    }

    @Test
    void shouldCreateEvent() throws Exception {

        String request = """
                {
                  "eventId":"evt-001",
                  "accountId":"acct-1",
                  "type":"CREDIT",
                  "amount":100,
                  "currency":"USD",
                  "eventTimestamp":"2026-05-15T10:00:00Z"
                }
                """;

        mockMvc.perform(
                        post("/events")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(request)
                )
                .andExpect(
                        status().isCreated()
                )
                .andExpect(
                        jsonPath(
                                "$.eventId"
                        ).value(
                                "evt-001"
                        )
                );
    }

    @Test
    void shouldHandleDuplicateEvent()
            throws Exception {

        String request = """
                {
                  "eventId":"evt-001",
                  "accountId":"acct-1",
                  "type":"CREDIT",
                  "amount":100,
                  "currency":"USD",
                  "eventTimestamp":"2026-05-15T10:00:00Z"
                }
                """;

        mockMvc.perform(
                post("/events")
                        .contentType(
                                MediaType.APPLICATION_JSON
                        )
                        .content(request)
        );

        mockMvc.perform(
                        post("/events")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(request)
                )
                .andExpect(
                        status().isOk()
                );

        org.junit.jupiter.api.Assertions
                .assertEquals(
                        1,
                        repository.count()
                );
    }

    @Test
    void shouldReturnEventsOrderedByTimestamp()
            throws Exception {

        String later = """
                {
                  "eventId":"evt-2",
                  "accountId":"acct-1",
                  "type":"DEBIT",
                  "amount":20,
                  "currency":"USD",
                  "eventTimestamp":"2026-05-15T12:00:00Z"
                }
                """;

        String earlier = """
                {
                  "eventId":"evt-1",
                  "accountId":"acct-1",
                  "type":"CREDIT",
                  "amount":100,
                  "currency":"USD",
                  "eventTimestamp":"2026-05-15T09:00:00Z"
                }
                """;

        mockMvc.perform(
                post("/events")
                        .contentType(
                                MediaType.APPLICATION_JSON
                        )
                        .content(later)
        );

        mockMvc.perform(
                post("/events")
                        .contentType(
                                MediaType.APPLICATION_JSON
                        )
                        .content(earlier)
        );

        mockMvc.perform(
                        get("/events")
                                .param(
                                        "account",
                                        "acct-1"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$",
                                hasSize(2)
                        )
                )
                .andExpect(
                        jsonPath(
                                "$[0].eventId"
                        ).value(
                                "evt-1"
                        )
                );
    }

    @Test
    void shouldCalculateBalance()
            throws Exception {

        String credit = """
                {
                  "eventId":"evt-1",
                  "accountId":"acct-1",
                  "type":"CREDIT",
                  "amount":100,
                  "currency":"USD",
                  "eventTimestamp":"2026-05-15T09:00:00Z"
                }
                """;

        String debit = """
                {
                  "eventId":"evt-2",
                  "accountId":"acct-1",
                  "type":"DEBIT",
                  "amount":30,
                  "currency":"USD",
                  "eventTimestamp":"2026-05-15T10:00:00Z"
                }
                """;

        mockMvc.perform(
                post("/events")
                        .contentType(
                                MediaType.APPLICATION_JSON
                        )
                        .content(credit)
        );

        mockMvc.perform(
                post("/events")
                        .contentType(
                                MediaType.APPLICATION_JSON
                        )
                        .content(debit)
        );

        mockMvc.perform(
                        get(
                                "/accounts/acct-1/balance"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$.balance"
                        ).value(
                                70
                        )
                );
    }

    @Test
    void shouldRejectInvalidAmount()
            throws Exception {

        String request = """
                {
                  "eventId":"evt-1",
                  "accountId":"acct-1",
                  "type":"CREDIT",
                  "amount":0,
                  "currency":"USD",
                  "eventTimestamp":"2026-05-15T09:00:00Z"
                }
                """;

        mockMvc.perform(
                        post("/events")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(request)
                )
                .andExpect(
                        status().isBadRequest()
                );
    }

    @Test
    void shouldReturn404WhenMissing()
            throws Exception {

        mockMvc.perform(
                        get("/events/unknown")
                )
                .andExpect(
                        status().isNotFound()
                );
    }
}