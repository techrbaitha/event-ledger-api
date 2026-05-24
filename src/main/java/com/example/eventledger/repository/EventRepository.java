package com.example.eventledger.repository;

import com.example.eventledger.entity.EventEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Optional;

public interface EventRepository
        extends JpaRepository<EventEntity, Long> {

    Optional<EventEntity> findByEventId(
            String eventId
    );

    Page<EventEntity>
    findByAccountIdOrderByEventTimestampAsc(
            String accountId,
            Pageable pageable
    );

    @Query("""
        SELECT COALESCE(
            SUM(
                CASE
                    WHEN e.type='CREDIT'
                    THEN e.amount
                    ELSE -e.amount
                END
            ),0
        )
        FROM EventEntity e
        WHERE e.accountId=:accountId
    """)
    BigDecimal calculateBalance(
            String accountId
    );
}