package com.tpspringboot.apirestclientcommande.Client.repositoryCL;

import com.tpspringboot.apirestclientcommande.Client.modeleCL.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
