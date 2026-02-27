package com.tpspringboot.apirestclientcommande.Client.repositoryCL;

import com.tpspringboot.apirestclientcommande.Client.modeleCL.Client;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ClientRepository extends CrudRepository<Client, Long> {
    Optional<Client> findByEmail(String email);

    Optional<Client> findByid(Long id);
}
