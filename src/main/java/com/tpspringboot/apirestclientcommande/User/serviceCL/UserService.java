package com.tpspringboot.apirestclientcommande.User.serviceCL;

import com.tpspringboot.apirestclientcommande.Exceptions.RessourceNotFoundException;
import com.tpspringboot.apirestclientcommande.User.modeleCL.User;
import com.tpspringboot.apirestclientcommande.User.repositoryCL.CrudUserRepository;
import com.tpspringboot.apirestclientcommande.User.repositoryCL.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final CrudUserRepository crudUserRepository;

    public Iterable<User> getUsersByRole(){
        return crudUserRepository.findByRole("ROLE_USER") ;
    }

    public Optional<User> getUser(Long id){
        return crudUserRepository.findById(id) ;
    }

    public Optional<User> updateUser(Long id, User userToUpdate){
        Optional<User> existingUser = crudUserRepository.findById(id) ;
        if (existingUser.isPresent()){
            User user = existingUser.get() ;
            user.setUsername(userToUpdate.getUsername());
            user.setCommandes(userToUpdate.getCommandes());
            return Optional.of(crudUserRepository.save(user)) ;
        } else {
            return Optional.empty();
        }
    }

    public void deleteUser(Long id){
        crudUserRepository.deleteById(id);
    }

}
