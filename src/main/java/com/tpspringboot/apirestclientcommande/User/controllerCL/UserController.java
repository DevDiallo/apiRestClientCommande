package com.tpspringboot.apirestclientcommande.User.controllerCL;

import com.tpspringboot.apirestclientcommande.User.modeleCL.User;
import com.tpspringboot.apirestclientcommande.User.repositoryCL.CrudUserRepository;
import com.tpspringboot.apirestclientcommande.Exceptions.RessourceNotFoundException;
import com.tpspringboot.apirestclientcommande.User.serviceCL.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/")
public class UserController {

    private final UserService userService ;

    @GetMapping("/users")
    public ResponseEntity<?> getUsers(){
        // on doit recuperer les utilisateur qui ont un role user
        Iterable<User> users = userService.getUsersByRole() ;

        return ResponseEntity.ok(users) ;
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId,@RequestBody User user){
        Optional<User> updatedUser = userService.updateUser(userId,user) ;
        if (updatedUser.isPresent()){

            return ResponseEntity.ok(updatedUser.get()) ;
        } else {
            throw new RessourceNotFoundException("Attention l'utilisateur existe pas") ;
        }
    }

}
