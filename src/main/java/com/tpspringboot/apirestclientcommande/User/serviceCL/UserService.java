package com.tpspringboot.apirestclientcommande.User.serviceCL;

import com.tpspringboot.apirestclientcommande.User.dto.UserResponseDto;
import com.tpspringboot.apirestclientcommande.User.modeleCL.User;
import com.tpspringboot.apirestclientcommande.User.repositoryCL.CrudUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
@Service
public class UserService {

    private final CrudUserRepository crudUserRepository;

    public List<UserResponseDto> getUsersByRole() {
        return StreamSupport.stream(crudUserRepository.findByRole("ROLE_USER").spliterator(), false)
                .map(this::toDto)
                .toList();
    }

    public Optional<UserResponseDto> getUser(Long id) {
        return crudUserRepository.findById(id).map(this::toDto);
    }

    public Optional<UserResponseDto> updateUser(Long id, User userToUpdate) {
        Optional<User> existingUser = crudUserRepository.findById(id);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if (userToUpdate.getNom() != null) user.setNom(userToUpdate.getNom());
            if (userToUpdate.getPrenom() != null) user.setPrenom(userToUpdate.getPrenom());
            if (userToUpdate.getEmail() != null) user.setEmail(userToUpdate.getEmail());
            if (userToUpdate.getTelephone() != null) user.setTelephone(userToUpdate.getTelephone());
            if (userToUpdate.getUsername() != null) user.setUsername(userToUpdate.getUsername());
            if (userToUpdate.getRole() != null) user.setRole(userToUpdate.getRole());
            User saved = crudUserRepository.save(user);
            return Optional.of(toDto(saved));
        }
        return Optional.empty();
    }

    public void deleteUser(Long id) {
        crudUserRepository.deleteById(id);
    }

    private UserResponseDto toDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getNom(),
                user.getPrenom(),
                user.getEmail(),
                user.getTelephone(),
                user.getUsername(),
                user.getRole(),
                user.getRoles()
        );
    }
}