package net.veramendi.fullstackbpapi.service;

import net.veramendi.fullstackbpapi.domain.Client;
import net.veramendi.fullstackbpapi.exception.ConflictException;
import net.veramendi.fullstackbpapi.exception.NotFoundException;
import net.veramendi.fullstackbpapi.repository.ClientRepository;
import net.veramendi.fullstackbpapi.web.dto.client.ClientCreateRequest;
import net.veramendi.fullstackbpapi.web.dto.client.ClientResponse;
import net.veramendi.fullstackbpapi.web.dto.client.ClientUpdateRequest;
import net.veramendi.fullstackbpapi.web.mapper.ClientMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientService {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    @SuppressWarnings("null")
    public ClientResponse create(ClientCreateRequest request) {
        if (clientRepository.existsById(request.clientId())) {
            throw new ConflictException("Client with id '%s' already exists".formatted(request.clientId()));
        }
        if (clientRepository.existsByIdentification(request.identification())) {
            throw new ConflictException("Client with identification '%s' already exists".formatted(request.identification()));
        }

        Client client = ClientMapper.toEntity(request);
        client.setPassword(passwordEncoder.encode(request.password()));
        
        return ClientMapper.toResponse(clientRepository.save(client));
    }

    @Transactional(readOnly = true)
    public List<ClientResponse> findAll() {
        return clientRepository.findAll().stream()
                .map(ClientMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClientResponse findById(@NonNull String clientId) {
        return ClientMapper.toResponse(load(clientId));
    }

    @SuppressWarnings("null")
    public ClientResponse update(String clientId, ClientUpdateRequest request) {
        Client client = load(clientId);
        ClientMapper.applyUpdate(client, request);
        
        if (request.password() != null && !request.password().isBlank()) {
            client.setPassword(passwordEncoder.encode(request.password()));
        }

        return ClientMapper.toResponse(clientRepository.save(client));
    }

    @SuppressWarnings("null")
    public void delete(String clientId) {
        Client client = load(clientId);
        clientRepository.delete(client);
    }

    private Client load(@NonNull String clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new NotFoundException("Client", clientId));
    }
}
