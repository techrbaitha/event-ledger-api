package com.example.eventledger.controller;

import com.example.eventledger.dto.BalanceResponse;
import com.example.eventledger.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final EventService service;

    @GetMapping("/{accountId}/balance")
    public ResponseEntity<BalanceResponse>
    getBalance(
            @PathVariable String accountId
    ) {

        return ResponseEntity.ok(
                service.getBalance(accountId)
        );
    }
}