package net.veramendi.fullstackbpapi.web.controller;

import net.veramendi.fullstackbpapi.service.ClientService;
import net.veramendi.fullstackbpapi.web.dto.client.ClientCreateRequest;
import net.veramendi.fullstackbpapi.web.dto.client.ClientResponse;
import net.veramendi.fullstackbpapi.web.dto.client.ClientUpdateRequest;

import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
@Tag(name = "Clients", description = "CRUD for bank clients")
public class ClientController {

    private final ClientService clientService;

    @Operation(summary = "List all clients")
    @GetMapping
    public List<ClientResponse> findAll() {
        return clientService.findAll();
    }

    @Operation(summary = "Get a client by clientId")
    @SuppressWarnings("null")
    @GetMapping("/{clientId}")
    public ClientResponse findById(@PathVariable String clientId) {
        return clientService.findById(clientId);
    }

    @Operation(summary = "Create a new client")
    @SuppressWarnings("null")
    @PostMapping
    public ResponseEntity<ClientResponse> create(@Valid @RequestBody ClientCreateRequest request) {
        ClientResponse response = clientService.create(request);

        return ResponseEntity
                .created(URI.create("/clients/" + response.clientId()))
                .body(response);
    }

    @Operation(summary = "Replace a client")
    @PutMapping("/{clientId}")
    public ClientResponse update(@PathVariable String clientId, @Valid @RequestBody ClientUpdateRequest request) {
        return clientService.update(clientId, request);
    }

    @Operation(summary = "Partially update a client")
    @PatchMapping("/{clientId}")
    public ClientResponse patch(@PathVariable String clientId, @Valid @RequestBody ClientUpdateRequest request) {
        return clientService.update(clientId, request);
    }

    @Operation(summary = "Delete a client")
    @DeleteMapping("/{clientId}")
    public ResponseEntity<Void> delete(@PathVariable String clientId) {
        clientService.delete(clientId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
