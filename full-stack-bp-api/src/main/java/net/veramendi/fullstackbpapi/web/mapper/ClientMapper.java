package net.veramendi.fullstackbpapi.web.mapper;

import net.veramendi.fullstackbpapi.domain.Client;
import net.veramendi.fullstackbpapi.domain.enums.Status;
import net.veramendi.fullstackbpapi.web.dto.client.ClientCreateRequest;
import net.veramendi.fullstackbpapi.web.dto.client.ClientResponse;
import net.veramendi.fullstackbpapi.web.dto.client.ClientUpdateRequest;

public final class ClientMapper {

    private ClientMapper() {}

    public static Client toEntity(ClientCreateRequest req) {
        Client client = new Client();
        client.setClientId(req.clientId());
        client.setName(req.name());
        client.setGender(req.gender());
        client.setAge(req.age());
        client.setIdentification(req.identification());
        client.setAddress(req.address());
        client.setPhone(req.phone());
        client.setStatus(req.status() != null ? req.status() : Status.ACTIVE);

        return client;
    }

    public static void applyUpdate(Client client, ClientUpdateRequest req) {
        if (req.name() != null) 
            client.setName(req.name());

        if (req.gender() != null) 
            client.setGender(req.gender());
        
        if (req.age() != null) 
            client.setAge(req.age());
        
        if (req.identification() != null) 
            client.setIdentification(req.identification());
        
        if (req.address() != null) 
            client.setAddress(req.address());
        
        if (req.phone() != null) 
            client.setPhone(req.phone());
        
        if (req.status() != null) 
            client.setStatus(req.status());
    }

    public static ClientResponse toResponse(Client client) {
        return new ClientResponse(
                client.getClientId(),
                client.getName(),
                client.getGender(),
                client.getAge(),
                client.getIdentification(),
                client.getAddress(),
                client.getPhone(),
                client.getStatus(),
                client.getCreatedAt(),
                client.getUpdatedAt()
        );
    }
}
