package com.tpspringboot.apirestclientcommande.Commande.serviceCO;

import com.tpspringboot.apirestclientcommande.Commande.dto.CommandeAdminDetailsResponseDto;
import com.tpspringboot.apirestclientcommande.Commande.dto.CommandeResponseDto;
import com.tpspringboot.apirestclientcommande.Commande.modeleCO.Commande;
import com.tpspringboot.apirestclientcommande.Commande.repositoryCO.CommandeRepository;
import com.tpspringboot.apirestclientcommande.User.modeleCL.User;
import com.tpspringboot.apirestclientcommande.User.repositoryCL.CrudUserRepository;
import com.tpspringboot.apirestclientcommande.produit.modeleProd.Commande_produit;
import com.tpspringboot.apirestclientcommande.produit.modeleProd.LigneStock;
import com.tpspringboot.apirestclientcommande.produit.modeleProd.Produit;
import com.tpspringboot.apirestclientcommande.produit.repositoryProd.LigneStockRepository;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommandeServiceTest {

    @Mock
    CommandeRepository commandeRepository ;
    @Mock
    CrudUserRepository crudUserRepository ;
    @Mock
    ProduitRepository produitRepository;
    @Mock
    LigneStockRepository ligneStockRepository;

    @InjectMocks
    CommandeService commandeService ;

    @BeforeEach
    void cleanSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCommandeDetailsForAdmin_returnsClientAndItems() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "admin@exemple.com",
                        "n/a",
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                )
        );

        User user = new User();
        user.setId(3L);
        user.setNom("Diallo");
        user.setPrenom("Amadou");
        user.setEmail("amadou@example.com");
        user.setTelephone("+33611111111");

        Produit produit = new Produit();
        produit.setId(10L);
        produit.setNom("Clavier");

        Commande_produit item = new Commande_produit();
        item.setProduit(produit);
        item.setQuantite(2);
        item.setPrixUnitaire(25.0);
        item.setSousTotal(50.0);

        Commande commande = new Commande();
        commande.setId(99L);
        commande.setUser(user);
        commande.setTotal(50.0);
        commande.setCommandeProduits(new ArrayList<>(Collections.singletonList(item)));

        when(commandeRepository.findById(99L)).thenReturn(Optional.of(commande));

        CommandeAdminDetailsResponseDto result = commandeService.getCommandeDetailsForAdmin(99L);

        assertThat(result.id()).isEqualTo(99L);
        assertThat(result.client()).isNotNull();
        assertThat(result.client().nom()).isEqualTo("Diallo");
        assertThat(result.articles()).hasSize(1);
        assertThat(result.articles().get(0).produitId()).isEqualTo(10L);
    }

    @Test
    void getCommandeDetailsForAdmin_forNonAdmin_throwsAccessDenied() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "user@exemple.com",
                        "n/a",
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                )
        );

        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> commandeService.getCommandeDetailsForAdmin(99L));

        verify(commandeRepository, never()).findById(anyLong());
    }

    @Test
    void saveCommande_containsClientAndItems_andTotalIsConsistent() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "user@exemple.com",
                        "n/a",
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                )
        );

        User user = new User();
        user.setId(1L);
        user.setNom("Diallo");
        user.setPrenom("Amadou");
        user.setEmail("amadou@example.com");
        user.setTelephone("+33611111111");
        user.setCommandes(new ArrayList<>());

        Produit produit = new Produit();
        produit.setId(10L);
        produit.setNom("Clavier");
        produit.setPrix(25.0);
        produit.setLigneStockId("7");

        LigneStock ligneStock = new LigneStock();
        ligneStock.setId(7L);
        ligneStock.setQuantiteStock(12);

        Commande_produit item = new Commande_produit();
        item.setProduit(produit);
        item.setQuantite(2);

        Commande commande = new Commande();
        commande.setCommandeProduits(new ArrayList<>(List.of(item)));

        when(crudUserRepository.findById(1L)).thenReturn(Optional.of(user));
        when(produitRepository.findById(10L)).thenReturn(Optional.of(produit));
        when(ligneStockRepository.findById(7L)).thenReturn(Optional.of(ligneStock));
        when(ligneStockRepository.save(any(LigneStock.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(commandeRepository.save(any(Commande.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CommandeResponseDto result = commandeService.saveCommande(1L, commande);

        assertThat(result.client()).isNotNull();
        assertThat(result.items()).hasSize(1);
        double totalItems = result.items().stream().map(i -> i.sousTotal() == null ? 0.0 : i.sousTotal()).reduce(0.0, Double::sum);
        assertThat(result.total()).isEqualTo(totalItems);
        assertThat(result.total()).isEqualTo(50.0);
    }

    @Test
    void saveCommande_forAdminRole_throwsAccessDenied() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "admin@exemple.com",
                        "n/a",
                        java.util.List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                )
        );

        Commande commande = new Commande();

        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> commandeService.saveCommande(1L, commande));

        verify(crudUserRepository, never()).findById(anyLong());
        verify(commandeRepository, never()).save(any());
    }
}