package com.tpspringboot.apirestclientcommande.User.controllerCL;

import com.tpspringboot.apirestclientcommande.Exceptions.RessourceNotFoundException;
import com.tpspringboot.apirestclientcommande.User.dto.UserResponseDto;
import com.tpspringboot.apirestclientcommande.User.modeleCL.User;
import com.tpspringboot.apirestclientcommande.User.serviceCL.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping({"/", "/api"})
public class UserController {

    private final UserService userService;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUsers() {
        return ResponseEntity.ok(userService.getUsersByRole());
    }

    @GetMapping("/clients")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getClients() {
        return ResponseEntity.ok(userService.getUsersByRole());
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody User user) {
        Optional<UserResponseDto> updatedUser = userService.updateUser(userId, user);
        if (updatedUser.isPresent()) {
            return ResponseEntity.ok(updatedUser.get());
        }
        throw new RessourceNotFoundException("Attention l'utilisateur existe pas");
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}