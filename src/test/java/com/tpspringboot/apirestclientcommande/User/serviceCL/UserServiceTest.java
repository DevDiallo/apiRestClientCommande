package com.tpspringboot.apirestclientcommande.User.serviceCL;

import com.tpspringboot.apirestclientcommande.User.modeleCL.User;
import com.tpspringboot.apirestclientcommande.User.repositoryCL.CrudUserRepository;
import com.tpspringboot.apirestclientcommande.User.repositoryCL.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.*;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.* ;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    CrudUserRepository crudUserRepository ;

    @InjectMocks
    UserService userService ;

    @Test
    void getUsersByRole() {
        // Given
        User u1 = new User() ;
        User u2 = new User() ;
        List<User> users = List.of(u1,u2) ;
        when(crudUserRepository.findByRole("ROLE_USER")).thenReturn(users) ;

        // When
        Iterable<User> result = userService.getUsersByRole();

        // Then
        verify(crudUserRepository).findByRole("ROLE_USER") ;
        assertThat(result).isNotNull()
                .hasSize(2)
                .containsExactly(u1,u2) ;

    }

    @Test
    void getUser() {
        // Given
        User user = new User() ;
        when(crudUserRepository.findById(user.getId())).thenReturn(Optional.of(user)) ;

        // When
        Optional<User> result = userService.getUser(user.getId()) ;

        // Then
        verify(crudUserRepository).findById(user.getId()) ;
        assertThat(result.get()).isNotNull().isEqualTo(user);
    }

    @Test
    void updateUser() {
        // Given
        User existingUser = new User() ;
        User userToUpdate = new User() ;
        userToUpdate.setId(existingUser.getId());
        when(crudUserRepository.findById(userToUpdate.getId())).thenReturn(Optional.of(existingUser)) ;
        when(crudUserRepository.save(userToUpdate)).thenReturn(userToUpdate) ;

        // When
        Optional<User> result = userService.updateUser(userToUpdate.getId(), userToUpdate) ;

        // Then
        verify(crudUserRepository).findById(userToUpdate.getId()) ;
        verify(crudUserRepository).save(userToUpdate) ;
        assertThat(result.get()).isNotNull().isEqualTo(userToUpdate) ;
    }

    @Test
    void deleteUser() {
        // Given
        User user = new User() ;
        doNothing().when(crudUserRepository).deleteById(user.getId());

        // When
        userService.deleteUser(user.getId());

        // Then
        verify(crudUserRepository).deleteById(user.getId());
    }
}