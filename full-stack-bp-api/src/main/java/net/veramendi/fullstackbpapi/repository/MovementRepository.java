package net.veramendi.fullstackbpapi.repository;

import net.veramendi.fullstackbpapi.domain.Movement;
import net.veramendi.fullstackbpapi.domain.enums.MovementType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovementRepository extends JpaRepository<Movement, Long> {

    @Query("""
        SELECT COALESCE(SUM(ABS(m.value)), 0)
        FROM Movement m
        WHERE m.account.id = :accountId
          AND m.movementType = :type
          AND m.date BETWEEN :start AND :end
        """)
    BigDecimal sumAbsoluteValueByAccountAndTypeAndDateBetween(
            @Param("accountId") Long accountId,
            @Param("type") MovementType type,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("""
        SELECT m FROM Movement m
        WHERE m.account.client.clientId = :clientId
          AND m.date BETWEEN :start AND :end
        ORDER BY m.account.accountNumber, m.date
        """)
    List<Movement> findByClientAndDateRange(
            @Param("clientId") String clientId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    List<Movement> findByAccountIdOrderByDateDesc(Long accountId);
}
