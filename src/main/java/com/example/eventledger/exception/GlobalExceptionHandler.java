package com.example.eventledger.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(
            EventNotFoundException.class
    )
    public ResponseEntity<ApiError>
    handleNotFound(
            EventNotFoundException ex
    ) {

        return ResponseEntity
                .status(
                        HttpStatus.NOT_FOUND
                )
                .body(
                        ApiError.builder()
                                .message(
                                        ex.getMessage()
                                )
                                .build()
                );
    }

    @ExceptionHandler(
            MetadataException.class
    )
    public ResponseEntity<ApiError>
    handleMetadata(
            MetadataException ex
    ) {

        return ResponseEntity
                .badRequest()
                .body(
                        ApiError.builder()
                                .message(
                                        ex.getMessage()
                                )
                                .build()
                );
    }

    @ExceptionHandler(
            MethodArgumentNotValidException.class
    )
    public ResponseEntity<ApiError>
    handleValidation(
            MethodArgumentNotValidException ex
    ) {

        String message =
                ex.getBindingResult()
                        .getFieldErrors()
                        .get(0)
                        .getDefaultMessage();

        return ResponseEntity
                .badRequest()
                .body(
                        ApiError.builder()
                                .message(
                                        message
                                )
                                .build()
                );
    }

    @ExceptionHandler(
            HttpMessageNotReadableException.class
    )
    public ResponseEntity<ApiError>
    handleInvalidPayload(
            HttpMessageNotReadableException ex
    ) {

        String message =
                ex.getMessage()
                        .contains(
                                "EventType"
                        )
                        ?
                        "type must be CREDIT or DEBIT"
                        :
                        "Invalid request payload";

        return ResponseEntity
                .badRequest()
                .body(
                        ApiError.builder()
                                .message(
                                        message
                                )
                                .build()
                );
    }

    @ExceptionHandler(
            Exception.class
    )
    public ResponseEntity<ApiError>
    handleGeneric(
            Exception ex
    ) {

        log.error(
                "Unexpected error",
                ex
        );

        return ResponseEntity
                .status(
                        HttpStatus.INTERNAL_SERVER_ERROR
                )
                .body(
                        ApiError.builder()
                                .message(
                                        "Unexpected internal error"
                                )
                                .build()
                );
    }
}