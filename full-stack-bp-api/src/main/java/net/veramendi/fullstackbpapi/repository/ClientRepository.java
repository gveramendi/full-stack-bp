package net.veramendi.fullstackbpapi.repository;

import net.veramendi.fullstackbpapi.domain.Client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {

    boolean existsByIdentification(String identification);
}
