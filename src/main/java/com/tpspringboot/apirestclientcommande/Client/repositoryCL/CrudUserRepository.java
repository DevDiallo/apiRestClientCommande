package com.tpspringboot.apirestclientcommande.Client.repositoryCL;

import com.tpspringboot.apirestclientcommande.Client.modeleCL.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CrudUserRepository extends CrudRepository<User, Long> {
    Iterable<User> findByRole(String role) ;
}
