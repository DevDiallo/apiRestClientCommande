package com.tpspringboot.apirestclientcommande.Client.controllerCL;

import com.tpspringboot.apirestclientcommande.Client.modeleCL.User;
import com.tpspringboot.apirestclientcommande.Client.repositoryCL.CrudUserRepository;
import com.tpspringboot.apirestclientcommande.Exceptions.RessourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/")
public class UserController {

    private final CrudUserRepository crudUserRepository ;

    @GetMapping("/users")
    public ResponseEntity<?> getUsers(){
        // on doit recuperer les utilisateur qui ont un role user
        Iterable<User> users = crudUserRepository.findByRole("ROLE_USER") ;

        return ResponseEntity.ok(users) ;
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId,@RequestBody User user){
        Optional<User> existingUser = crudUserRepository.findById(userId) ;
        if (existingUser.isPresent()){
            User userFind = existingUser.get() ;
            userFind.setUsername(user.getUsername()); // c'est ici qu'on change apres tout ce qu'on voudra

            return ResponseEntity.ok(crudUserRepository.save(userFind)) ;
        } else {
            throw new RessourceNotFoundException("Attention l'utilisateur existe pas") ;
        }
    }

}
