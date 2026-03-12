package com.tpspringboot.apirestclientcommande.produit.serviceProd;

import com.tpspringboot.apirestclientcommande.produit.modeleProd.Commande_produit;
import com.tpspringboot.apirestclientcommande.produit.repositoryProd.ComProdRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.* ;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComProdServiceTest {

    @Mock
    ComProdRepository comProdRepository ;

    @InjectMocks
    ComProdService comProdService ;

    @Test
    void getcommandeProduits() {
        // Given
        Commande_produit cp_1 = new Commande_produit() ;
        Commande_produit cp_2 = new Commande_produit() ;
        when(comProdRepository.findAll()).thenReturn(List.of(cp_1,cp_2)) ;

        // When
        Iterable<Commande_produit> result = comProdService.getcommandeProduits() ;

        // Then
        verify(comProdRepository).findAll() ;
        assertThat(result).isNotNull()
                .hasSize(2)
                .containsExactly(cp_1,cp_2) ;
    }

    @Test
    void getComProd() {
        // Given
        Commande_produit commandeProduit = new Commande_produit() ;
        when(comProdRepository.findById(commandeProduit.getId())).thenReturn(Optional.of(commandeProduit)) ;

        // When
        Optional<Commande_produit> result = comProdService.getComProd(commandeProduit.getId()) ;

        // Then
        verify(comProdRepository).findById(commandeProduit.getId()) ;
        assertThat(result.get()).isNotNull().isEqualTo(commandeProduit) ;
    }

    @Test
    void saveComProd() {
        //Given
        Commande_produit commandeProduit = new Commande_produit() ;
        when(comProdRepository.save(commandeProduit)).thenReturn(commandeProduit) ;

        // When
        Commande_produit result = comProdService.saveComProd(commandeProduit) ;

        // Then
        verify(comProdRepository).save(commandeProduit) ;
        assertThat(result).isNotNull().isEqualTo(commandeProduit) ;
    }

    @Test
    void updateComProd() {
        //Given
        Commande_produit existingComProduit = new Commande_produit() ;
        Commande_produit comProduitToUpdate = new Commande_produit() ;
        comProduitToUpdate.setId(existingComProduit.getId());
        when(comProdRepository.findById(comProduitToUpdate.getId())).thenReturn(Optional.of(existingComProduit)) ;
        when(comProdRepository.save(comProduitToUpdate)).thenReturn(comProduitToUpdate) ;

        // When
        Optional<Commande_produit> result = comProdService.updateComProd(comProduitToUpdate.getId(), comProduitToUpdate) ;

        // Then
        verify(comProdRepository).findById(comProduitToUpdate.getId()) ;
        verify(comProdRepository).save(comProduitToUpdate) ;
        assertThat(result.get()).isNotNull().isEqualTo(comProduitToUpdate) ;
    }

    @Test
    void deleteComProd() {
        //Given
        Commande_produit commandeProduit = new Commande_produit() ;
        doNothing().when(comProdRepository).deleteById(commandeProduit.getId()) ;

        // When
        comProdService.deleteComProd(commandeProduit.getId());

        // Then
        verify(comProdRepository).deleteById(commandeProduit.getId());
    }
}