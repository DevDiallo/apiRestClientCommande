package com.tpspringboot.apirestclientcommande.produit.serviceProd;

import com.tpspringboot.apirestclientcommande.Commande.modeleCO.Commande;
import com.tpspringboot.apirestclientcommande.produit.modeleProd.Produit;
import com.tpspringboot.apirestclientcommande.produit.repositoryProd.ProduitRepository;
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
class ProduitServiceTest {

    @Mock
    ProduitRepository produitRepository ;

    @InjectMocks
    ProduitService produitService ;

    @Test
    void getProduits() {
        // Given
        Produit p1 = new Produit() ;
        Produit p2 = new Produit() ;
        List<Produit> produits = List.of(p1,p2) ;
        when(produitRepository.findAll()).thenReturn(produits) ;

        // When
        Iterable<Produit> result = produitService.getProduits() ;

        // Then
        verify(produitRepository).findAll() ;
        assertThat(result).isNotNull()
                .hasSize(2)
                .containsExactly(p1,p2) ;
    }

    @Test
    void getProduit() {
        // Given
        Produit produit = new Produit() ;
        when(produitRepository.findById(produit.getId())).thenReturn(Optional.of(produit)) ;

        // When
        Optional<Produit> result = produitService.getProduit(produit.getId()) ;

        // Then
        verify(produitRepository).findById(produit.getId()) ;
        assertThat(result).isNotNull() ;
        assertThat(result.get()).isEqualTo(produit) ;
    }

    @Test
    void saveProduit() {
        // Given
        Produit produit = new Produit() ;
        when(produitRepository.save(produit)).thenReturn(produit) ;

        // When
        Produit result = produitService.saveProduit(produit) ;

        // Then
        verify(produitRepository).save(produit) ;
        assertThat(result).isNotNull().isEqualTo(produit) ;
    }

    @Test
    void updateProduit() {
        // Given
        Produit existingProduit = new Produit() ;
        Produit prodToUpdate = new Produit() ;
        prodToUpdate.setId(existingProduit.getId());
        when(produitRepository.findById(prodToUpdate.getId())).thenReturn(Optional.of(existingProduit)) ;
        when(produitRepository.save(prodToUpdate)).thenReturn(prodToUpdate) ;

        // When
        Optional<Produit> result = produitService.updateProduit(prodToUpdate.getId(), prodToUpdate) ;

        // Then
        verify(produitRepository).findById(prodToUpdate.getId()) ;
        verify(produitRepository).save(prodToUpdate) ;
        assertThat(result.get()).isNotNull().isEqualTo(prodToUpdate) ;
    }

    @Test
    void deleteProduit() {
        // Given
        Produit produit = new Produit() ;
        doNothing().when(produitRepository).deleteById(produit.getId()) ;

        // When
        produitService.deleteProduit(produit.getId());

        // Then
        verify(produitRepository).deleteById(produit.getId()) ;

    }
}