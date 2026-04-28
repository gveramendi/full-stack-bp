package net.veramendi.fullstackbpapi.web.controller;

import net.veramendi.fullstackbpapi.service.AccountService;
import net.veramendi.fullstackbpapi.web.dto.account.AccountCreateRequest;
import net.veramendi.fullstackbpapi.web.dto.account.AccountResponse;
import net.veramendi.fullstackbpapi.web.dto.account.AccountUpdateRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@Tag(name = "Accounts", description = "CRUD for client accounts")
public class AccountController {

    private final AccountService accountService;

    @Operation(summary = "List accounts, optionally filtered by clientId")
    @GetMapping
    public List<AccountResponse> findAll(
            @Parameter(description = "Filter by owning client") @RequestParam(required = false) String clientId) {
        return clientId != null ? accountService.findByClientId(clientId) : accountService.findAll();
    }

    @Operation(summary = "Get an account by id")
    @SuppressWarnings("null")
    @GetMapping("/{id}")
    public AccountResponse findById(@PathVariable Long id) {
        return accountService.findById(id);
    }

    @Operation(summary = "Create a new account")
    @SuppressWarnings("null")
    @PostMapping
    public ResponseEntity<AccountResponse> create(@Valid @RequestBody AccountCreateRequest request) {
        AccountResponse response = accountService.create(request);

        return ResponseEntity
                .created(URI.create("/accounts/" + response.id()))
                .body(response);
    }

    @Operation(summary = "Replace an account")
    @SuppressWarnings("null")
    @PutMapping("/{id}")
    public AccountResponse update(@PathVariable Long id, @Valid @RequestBody AccountUpdateRequest request) {
        return accountService.update(id, request);
    }

    @Operation(summary = "Partially update an account")
    @SuppressWarnings("null")
    @PatchMapping("/{id}")
    public AccountResponse patch(@PathVariable Long id, @Valid @RequestBody AccountUpdateRequest request) {
        return accountService.update(id, request);
    }

    @Operation(summary = "Delete an account")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        accountService.delete(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
