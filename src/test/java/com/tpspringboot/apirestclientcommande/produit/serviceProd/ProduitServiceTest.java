package com.tpspringboot.apirestclientcommande.produit.serviceProd;

import com.tpspringboot.apirestclientcommande.Exceptions.RessourceNotFoundException;
import com.tpspringboot.apirestclientcommande.produit.dto.ProduitAdminUpsertRequestDto;
import com.tpspringboot.apirestclientcommande.produit.modeleProd.LigneStock;
import com.tpspringboot.apirestclientcommande.produit.modeleProd.Produit;
import com.tpspringboot.apirestclientcommande.produit.repositoryProd.ComProdRepository;
import com.tpspringboot.apirestclientcommande.produit.repositoryProd.LigneStockRepository;
import com.tpspringboot.apirestclientcommande.produit.repositoryProd.ProduitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProduitServiceTest {

    @Mock
    ProduitRepository produitRepository ;
    @Mock
    LigneStockRepository ligneStockRepository;
    @Mock
    ComProdRepository comProdRepository;

    @InjectMocks
    ProduitService produitService ;

    @Test
    void updateProduit_updatesProductAndStockQuantity() {
        Produit existing = new Produit();
        existing.setId(5L);
        existing.setNom("Ancien");
        existing.setLigneStockId("9");

        LigneStock ligneStock = new LigneStock();
        ligneStock.setId(9L);
        ligneStock.setQuantiteStock(3);

        ProduitAdminUpsertRequestDto payload = new ProduitAdminUpsertRequestDto(
                "Nouveau",
                "Desc",
                35.0,
                "/api/images/produits/p1.jpeg",
                2L,
                null,
                14
        );

        when(produitRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(ligneStockRepository.findById(9L)).thenReturn(Optional.of(ligneStock));
        when(ligneStockRepository.save(any(LigneStock.class))).thenAnswer(i -> i.getArgument(0));
        when(produitRepository.save(any(Produit.class))).thenAnswer(i -> i.getArgument(0));

        Optional<Produit> result = produitService.updateProduit(5L, payload);

        assertThat(result).isPresent();
        assertThat(result.get().getNom()).isEqualTo("Nouveau");
        assertThat(ligneStock.getQuantiteStock()).isEqualTo(14);
    }

    @Test
    void saveProduit_rejectsNegativeStock() {
        ProduitAdminUpsertRequestDto payload = new ProduitAdminUpsertRequestDto(
                "Produit", "Desc", 10.0, "/img", 1L, null, -1
        );

        assertThrows(IllegalArgumentException.class, () -> produitService.saveProduit(payload));
        verify(produitRepository, never()).save(any());
    }

    @Test
    void updateProduit_withUnknownStock_rollsBackBeforeProductSave() {
        Produit existing = new Produit();
        existing.setId(8L);

        ProduitAdminUpsertRequestDto payload = new ProduitAdminUpsertRequestDto(
                "Nom", "Desc", 22.0, "/img", 1L, 404L, 6
        );

        when(produitRepository.findById(8L)).thenReturn(Optional.of(existing));
        when(ligneStockRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(RessourceNotFoundException.class, () -> produitService.updateProduit(8L, payload));
        verify(produitRepository, never()).save(any());
    }
}