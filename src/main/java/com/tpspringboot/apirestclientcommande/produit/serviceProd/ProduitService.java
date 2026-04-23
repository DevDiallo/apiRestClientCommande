package com.tpspringboot.apirestclientcommande.produit.serviceProd;

import com.tpspringboot.apirestclientcommande.Exceptions.RessourceNotFoundException;
import com.tpspringboot.apirestclientcommande.produit.dto.ProduitAdminUpsertRequestDto;
import com.tpspringboot.apirestclientcommande.produit.modeleProd.LigneStock;
import com.tpspringboot.apirestclientcommande.produit.modeleProd.Produit;
import com.tpspringboot.apirestclientcommande.produit.repositoryProd.ComProdRepository;
import com.tpspringboot.apirestclientcommande.produit.repositoryProd.LigneStockRepository;
import com.tpspringboot.apirestclientcommande.produit.repositoryProd.ProduitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProduitService {

    private final ProduitRepository produitRepository;
    private final LigneStockRepository ligneStockRepository;
    private final ComProdRepository comProdRepository;

    public Iterable<Produit> getProduits() {
        return produitRepository.findAll();
    }

    public Optional<Produit> getProduit(Long id) {
        return produitRepository.findById(id);
    }

    @Transactional
    public Produit saveProduit(ProduitAdminUpsertRequestDto payload) {
        Integer stockQuantity = payload.quantiteStock() != null ? payload.quantiteStock() : 0;
        validateStockQuantity(stockQuantity);

        Produit produit = new Produit();
        produit.setNom(payload.nom());
        produit.setDescription(payload.description());
        produit.setPrix(payload.prix());
        produit.setImageUrl(payload.imagePath());
        produit.setCategorieId(payload.categorieId());

        Produit savedProduct = produitRepository.save(produit);

        LigneStock ligneStock;
        if (payload.ligneStockId() != null) {
            ligneStock = ligneStockRepository.findById(payload.ligneStockId())
                    .orElseThrow(() -> new RessourceNotFoundException("LigneStock introuvable : " + payload.ligneStockId()));
            ligneStock.setQuantiteStock(stockQuantity);
        } else {
            ligneStock = new LigneStock();
            ligneStock.setQuantiteStock(stockQuantity);
        }
        ligneStock.setProduit(savedProduct);

        LigneStock savedLigneStock = ligneStockRepository.save(ligneStock);
        savedProduct.setLigneStockId(String.valueOf(savedLigneStock.getId()));
        Produit finalProduct = produitRepository.save(savedProduct);

        log.info("[ADMIN_ACTION] create-product | produitId={} | nom={}", finalProduct.getId(), finalProduct.getNom());
        return finalProduct;
    }

    @Transactional
    public Optional<Produit> updateProduit(Long id, ProduitAdminUpsertRequestDto payload) {
        Optional<Produit> existingProduit = produitRepository.findById(id);
        if (existingProduit.isPresent()) {
            Produit p = existingProduit.get();
            if (payload.nom() != null) p.setNom(payload.nom());
            if (payload.description() != null) p.setDescription(payload.description());
            if (payload.prix() != null) p.setPrix(payload.prix());
            if (payload.imagePath() != null) p.setImageUrl(payload.imagePath());
            if (payload.categorieId() != null) p.setCategorieId(payload.categorieId());

            if (payload.quantiteStock() != null) {
                validateStockQuantity(payload.quantiteStock());
            }

            Long targetLigneStockId = payload.ligneStockId();
            if (targetLigneStockId == null && p.getLigneStockId() != null && !p.getLigneStockId().isBlank()) {
                targetLigneStockId = parseLigneStockId(p.getLigneStockId(), p.getId());
            }

            if (targetLigneStockId != null) {
                Long finalTargetLigneStockId = targetLigneStockId;
                LigneStock ligneStock = ligneStockRepository.findById(finalTargetLigneStockId)
                        .orElseThrow(() -> new RessourceNotFoundException("LigneStock introuvable : " + finalTargetLigneStockId));
                ligneStock.setProduit(p);
                if (payload.quantiteStock() != null) {
                    ligneStock.setQuantiteStock(payload.quantiteStock());
                }
                LigneStock savedStock = ligneStockRepository.save(ligneStock);
                p.setLigneStockId(String.valueOf(savedStock.getId()));
            }

            Produit produitSaved = produitRepository.save(p);
            log.info("[ADMIN_ACTION] update-product | produitId={} | nom={}", produitSaved.getId(), produitSaved.getNom());
            return Optional.of(produitSaved);
        }
        return Optional.empty();
    }

    @Transactional
    public void deleteProduit(Long id) {
        log.info("[ADMIN_ACTION] delete-product | produitId={}", id);

        // 1) Remove commande lines referencing this product (FK commande_produit -> produit)
        comProdRepository.deleteByProduit_Id(id);

        // 2) Clear product -> ligneStock association stored as string id
        Optional<Produit> productOptional = produitRepository.findById(id);
        productOptional.ifPresent(p -> {
            p.setLigneStockId(null);
            produitRepository.save(p);
        });

        // 3) Remove stock lines referencing this product (FK ligne_stocks -> produit)
        List<LigneStock> lignes = ligneStockRepository.findByProduit_Id(id);
        for (LigneStock ligne : lignes) {
            ligne.setProduit(null);
        }
        ligneStockRepository.saveAll(lignes);
        ligneStockRepository.deleteByProduit_Id(id);

        // 4) Finally delete product row
        produitRepository.deleteById(id);
    }

    private void validateStockQuantity(Integer quantity) {
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("quantite_stock doit etre >= 0");
        }
    }

    private Long parseLigneStockId(String rawId, Long produitId) {
        try {
            return Long.parseLong(rawId);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("ligneStockId invalide pour le produit : " + produitId);
        }
    }
}