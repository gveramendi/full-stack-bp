package net.veramendi.fullstackbpapi.web.controller;

import net.veramendi.fullstackbpapi.service.MovementService;
import net.veramendi.fullstackbpapi.web.dto.movement.MovementRequest;
import net.veramendi.fullstackbpapi.web.dto.movement.MovementResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/movements")
@RequiredArgsConstructor
@Tag(name = "Movements", description = "Deposits and withdrawals against accounts")
public class MovementController {

    private final MovementService movementService;

    @Operation(summary = "List movements, optionally filtered by accountId")
    @GetMapping
    public List<MovementResponse> findAll(
            @Parameter(description = "Filter by account") @RequestParam(required = false) Long accountId) {
        return accountId != null ?
                    movementService.findByAccountId(accountId) :
                    movementService.findAll();
    }

    @Operation(summary = "Get a movement by id")
    @GetMapping("/{id}")
    public MovementResponse findById(@PathVariable Long id) {
        return movementService.findById(id);
    }

    @Operation(
            summary = "Create a deposit or withdrawal",
            description = "Updates the account balance atomically. Rejects with 422 on insufficient balance "
                        + "or when the daily withdrawal limit is exceeded."
    )
    @SuppressWarnings("null")
    @PostMapping
    public ResponseEntity<MovementResponse> create(@Valid @RequestBody MovementRequest request) {
        MovementResponse response = movementService.create(request);

        return ResponseEntity
                .created(URI.create("/movements/" + response.id()))
                .body(response);
    }

    @Operation(summary = "Delete a movement")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        movementService.delete(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
