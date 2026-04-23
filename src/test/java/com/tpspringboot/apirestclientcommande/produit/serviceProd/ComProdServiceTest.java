package com.tpspringboot.apirestclientcommande.produit.serviceProd;

import com.tpspringboot.apirestclientcommande.Commande.modeleCO.Commande;
import com.tpspringboot.apirestclientcommande.Commande.repositoryCO.CommandeRepository;
import com.tpspringboot.apirestclientcommande.produit.modeleProd.Produit;
import com.tpspringboot.apirestclientcommande.produit.modeleProd.Commande_produit;
import com.tpspringboot.apirestclientcommande.produit.repositoryProd.ComProdRepository;
import com.tpspringboot.apirestclientcommande.produit.repositoryProd.ProduitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComProdServiceTest {

    @Mock
    ComProdRepository comProdRepository ;
    @Mock
    ProduitRepository produitRepository;
    @Mock
    CommandeRepository commandeRepository;

    @InjectMocks
    ComProdService comProdService ;

    @BeforeEach
    void cleanSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void saveComProd_forAdminRole_throwsAccessDenied() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "admin@exemple.com",
                        "n/a",
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                )
        );

        Commande_produit payload = new Commande_produit();

        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> comProdService.saveComProd(1L, 1L, payload));

        verify(produitRepository, never()).findById(anyLong());
        verify(commandeRepository, never()).findById(anyLong());
        verify(comProdRepository, never()).save(any());
    }

    @Test
    void saveComProd_forUserRole_isAllowed() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "user@exemple.com",
                        "n/a",
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                )
        );

        Produit produit = new Produit();
        produit.setId(1L);
        produit.setPrix(10.0);

        Commande commande = new Commande();
        commande.setId(2L);

        Commande_produit payload = new Commande_produit();
        payload.setQuantite(2);

        Commande_produit saved = new Commande_produit();
        saved.setId(5L);
        saved.setProduit(produit);
        saved.setCommande(commande);
        saved.setQuantite(2);
        saved.setPrixUnitaire(10.0);
        saved.setSousTotal(20.0);

        when(produitRepository.findById(1L)).thenReturn(java.util.Optional.of(produit));
        when(commandeRepository.findById(2L)).thenReturn(java.util.Optional.of(commande));
        when(comProdRepository.save(any(Commande_produit.class))).thenReturn(saved);
        when(comProdRepository.findByCommande_Id(2L)).thenReturn(List.of(saved));

        var result = comProdService.saveComProd(1L, 2L, payload);

        assertThat(result.id()).isEqualTo(5L);
        assertThat(result.quantite()).isEqualTo(2);
        assertThat(result.sousTotal()).isEqualTo(20.0);
    }
}