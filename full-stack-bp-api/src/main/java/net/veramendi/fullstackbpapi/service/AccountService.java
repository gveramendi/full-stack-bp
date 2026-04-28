package net.veramendi.fullstackbpapi.service;

import net.veramendi.fullstackbpapi.domain.Account;
import net.veramendi.fullstackbpapi.domain.Client;
import net.veramendi.fullstackbpapi.exception.ConflictException;
import net.veramendi.fullstackbpapi.exception.NotFoundException;
import net.veramendi.fullstackbpapi.repository.AccountRepository;
import net.veramendi.fullstackbpapi.repository.ClientRepository;
import net.veramendi.fullstackbpapi.web.dto.account.AccountCreateRequest;
import net.veramendi.fullstackbpapi.web.dto.account.AccountResponse;
import net.veramendi.fullstackbpapi.web.dto.account.AccountUpdateRequest;
import net.veramendi.fullstackbpapi.web.mapper.AccountMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;

    @SuppressWarnings("null")
    public AccountResponse create(AccountCreateRequest request) {
        if (accountRepository.existsByAccountNumber(request.accountNumber())) {
            throw new ConflictException("Account with number '%s' already exists".formatted(request.accountNumber()));
        }

        Client client = clientRepository.findById(request.clientId())
                .orElseThrow(() -> new NotFoundException("Client", request.clientId()));
        Account account = AccountMapper.toEntity(request, client);
        
        return AccountMapper.toResponse(accountRepository.save(account));
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> findAll() {
        return accountRepository.findAll().stream()
                .map(AccountMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AccountResponse findById(@NonNull Long id) {
        return AccountMapper.toResponse(load(id));
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> findByClientId(String clientId) {
        return accountRepository.findByClient_ClientId(clientId).stream()
                .map(AccountMapper::toResponse)
                .toList();
    }

    @SuppressWarnings("null")
    public AccountResponse update(@NonNull Long id, AccountUpdateRequest request) {
        Account account = load(id);
        if (request.accountNumber() != null
                && !request.accountNumber().equals(account.getAccountNumber())
                && accountRepository.existsByAccountNumber(request.accountNumber())) {
            throw new ConflictException("Account with number '%s' already exists".formatted(request.accountNumber()));
        }
        
        AccountMapper.applyUpdate(account, request);
        Account saved = accountRepository.save(account);

        return AccountMapper.toResponse(saved);
    }

    @SuppressWarnings("null")
    public void delete(Long id) {
        Account account = load(id);
        accountRepository.delete(account);
    }

    private Account load(@NonNull Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Account", id));
    }
}
