package com.tpspringboot.apirestclientcommande.User.repositoryCL;

import com.tpspringboot.apirestclientcommande.User.modeleCL.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrudUserRepository extends CrudRepository<User, Long> {
    Iterable<User> findByRole(String role) ;
}
