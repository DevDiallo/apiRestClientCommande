package com.tpspringboot.apirestclientcommande.Commande.serviceCO;

import com.tpspringboot.apirestclientcommande.Commande.modeleCO.Commande;
import com.tpspringboot.apirestclientcommande.Commande.repositoryCO.CommandeRepository;
import com.tpspringboot.apirestclientcommande.User.modeleCL.User;
import com.tpspringboot.apirestclientcommande.User.repositoryCL.CrudUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommandeServiceTest {

    @Mock
    CommandeRepository commandeRepository ;
    @Mock
    CrudUserRepository crudUserRepository ;

    @InjectMocks
    CommandeService commandeService ;

    @Test
    void getCommandes() {
        // Given
        Commande c1 = new Commande() ;
        Commande c2 = new Commande() ;
        List<Commande> commandes = List.of(c1,c2) ;
        when(commandeRepository.findAll()).thenReturn(commandes) ;

        // When
        Iterable<Commande> result = commandeService.getCommandes() ;

        // Then
        verify(commandeRepository).findAll() ;
        assertThat(result).isNotNull()
                .hasSize(2)
                .containsExactly(c1,c2) ;
    }

    @Test
    void getCommande() {
        // Given
        Commande commande = new Commande() ;
        when(commandeRepository.findById(commande.getId())).thenReturn(Optional.of(commande)) ;

        // When
        ResponseEntity<Commande> result = commandeService.getCommande(commande.getId()) ;

        // Then
        verify(commandeRepository).findById(commande.getId()) ;
        assertThat(result).isNotNull() ;
        assertThat(result.getBody()).isNotNull() ;
        assertThat(result.getBody()).isEqualTo(commande) ;
    }

    @Test
    void saveCommande() {
        // Given
        User user = new User() ;
        user.setCommandes(new ArrayList<>());
        Commande commande = new Commande() ;
        commande.setUser(user);
        when(crudUserRepository.findById(user.getId())).thenReturn(Optional.of(user)) ;
        when(commandeRepository.save(commande)).thenReturn(commande) ;

        // When
        ResponseEntity<Commande> result = commandeService.saveCommande(user.getId(), commande) ;

        // Then
        verify(crudUserRepository).findById(user.getId()) ;
        verify(commandeRepository).save(commande) ;
        assertThat(result).isNotNull() ;
        assertThat(result.getBody()).isEqualTo(commande) ;
    }

    @Test
    void updateCommande() {
        // Given
        Commande existingCommande = new Commande() ;
        Commande c2 = new Commande() ; // La nouvelle commande qu'on veut updater
        c2.setId(existingCommande.getId()); // les 2 commandes doivent avoir le meme Id
        when(commandeRepository.findById(c2.getId())).thenReturn(Optional.of(existingCommande)) ;
        when(commandeRepository.save(c2)).thenReturn(c2) ;

        // When
        ResponseEntity<Commande> result = commandeService.updateCommande(c2.getId(), c2) ;

        // Then
        verify(commandeRepository).findById(c2.getId()) ;
        verify(commandeRepository).save(c2) ;
        assertThat(result).isNotNull() ;
        assertThat(result.getBody()).isEqualTo(c2) ;
    }

    @Test
    void deleteCommande() {
        // Given
        Commande commande = new Commande() ;
        when(commandeRepository.existsById(commande.getId())).thenReturn(true) ;
        doNothing().when(commandeRepository).deleteById(commande.getId()) ;

        // When
        ResponseEntity<Void> result =  commandeService.deleteCommande(commande.getId()) ;

        // Then
        verify(commandeRepository).existsById(commande.getId()) ;
        verify(commandeRepository).deleteById(commande.getId());
        assertThat(result).isNotNull() ;
        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue() ;
    }
}