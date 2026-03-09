package com.tpspringboot.apirestclientcommande.User.repositoryCL;

import com.tpspringboot.apirestclientcommande.User.modeleCL.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
