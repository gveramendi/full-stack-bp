package net.veramendi.fullstackbpapi.service;

import net.veramendi.fullstackbpapi.domain.Account;
import net.veramendi.fullstackbpapi.domain.Movement;
import net.veramendi.fullstackbpapi.domain.enums.MovementType;
import net.veramendi.fullstackbpapi.exception.DailyLimitExceededException;
import net.veramendi.fullstackbpapi.exception.InsufficientBalanceException;
import net.veramendi.fullstackbpapi.exception.NotFoundException;
import net.veramendi.fullstackbpapi.repository.AccountRepository;
import net.veramendi.fullstackbpapi.repository.MovementRepository;
import net.veramendi.fullstackbpapi.web.dto.movement.MovementRequest;
import net.veramendi.fullstackbpapi.web.dto.movement.MovementResponse;
import net.veramendi.fullstackbpapi.web.mapper.MovementMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MovementService {

    private final MovementRepository movementRepository;
    private final AccountRepository accountRepository;

    @Value("${app.withdrawal.daily-limit-usd}")
    private BigDecimal dailyWithdrawalLimit;

    @SuppressWarnings("null")
    public MovementResponse create(MovementRequest request) {
        Account account = accountRepository.findById(request.accountId())
                .orElseThrow(() -> new NotFoundException("Account", request.accountId()));

        BigDecimal amount = request.amount();
        BigDecimal signedValue = switch (request.movementType()) {
            case DEPOSIT -> amount;
            case WITHDRAWAL -> {
                ensureSufficientBalance(account, amount);
                ensureWithinDailyLimit(account, amount);
                yield amount.negate();
            }
        };

        BigDecimal newBalance = account.getCurrentBalance().add(signedValue);

        Movement movement = new Movement();
        movement.setAccount(account);
        movement.setMovementType(request.movementType());
        movement.setValue(signedValue);
        movement.setBalance(newBalance);
        movement.setDate(LocalDateTime.now());

        account.setCurrentBalance(newBalance);
        accountRepository.save(account);

        return MovementMapper.toResponse(movementRepository.save(movement));
    }

    @Transactional(readOnly = true)
    public List<MovementResponse> findAll() {
        return movementRepository.findAll().stream()
                .map(MovementMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MovementResponse findById(Long id) {
        return MovementMapper.toResponse(load(id));
    }

    @Transactional(readOnly = true)
    public List<MovementResponse> findByAccountId(Long accountId) {
        return movementRepository.findByAccountIdOrderByDateDesc(accountId).stream()
                .map(MovementMapper::toResponse)
                .toList();
    }

    @SuppressWarnings("null")
    public void delete(Long id) {
        Movement movement = load(id);
        movementRepository.delete(movement);
    }

    @SuppressWarnings("null")
    private Movement load(Long id) {
        return movementRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Movement", id));
    }

    private void ensureSufficientBalance(Account account, BigDecimal amount) {
        if (account.getCurrentBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException();
        }
    }

    private void ensureWithinDailyLimit(Account account, BigDecimal amount) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);
        BigDecimal alreadyWithdrawn = movementRepository
                .sumAbsoluteValueByAccountAndTypeAndDateBetween(
                        account.getId(), MovementType.WITHDRAWAL, startOfDay, endOfDay);
                        
        if (alreadyWithdrawn.add(amount).compareTo(dailyWithdrawalLimit) > 0) {
            throw new DailyLimitExceededException();
        }
    }
}
