package com.tpspringboot.apirestclientcommande.produit.serviceProd;

import com.tpspringboot.apirestclientcommande.Exceptions.RessourceNotFoundException;
import com.tpspringboot.apirestclientcommande.produit.modeleProd.LigneStock;
import com.tpspringboot.apirestclientcommande.produit.modeleProd.Produit;
import com.tpspringboot.apirestclientcommande.produit.modeleProd.Stock;
import com.tpspringboot.apirestclientcommande.produit.repositoryProd.LigneStockRepository;
import com.tpspringboot.apirestclientcommande.produit.repositoryProd.ProduitRepository;
import com.tpspringboot.apirestclientcommande.produit.repositoryProd.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LigneStockService {

    private final LigneStockRepository ligneStockRepository;
    private final ProduitRepository produitRepository;
    private final StockRepository stockRepository;

    public Iterable<LigneStock> getLigneStocks() {
        return ligneStockRepository.findAll();
    }

    public LigneStock getLigneStock(Long id) {
        return ligneStockRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("LigneStock introuvable : " + id));
    }

    @Transactional
    public LigneStock saveLigneStock(LigneStock ligneStock) {
        attachRelations(ligneStock);
        if (ligneStock.getQuantiteStock() != null && ligneStock.getQuantiteStock() < 0) {
            throw new IllegalArgumentException("quantite_stock doit etre >= 0");
        }
        LigneStock saved = ligneStockRepository.save(ligneStock);
        syncProduitLigneStockId(saved);
        return saved;
    }

    @Transactional
    public LigneStock updateLigneStock(Long id, LigneStock payload) {
        LigneStock existing = getLigneStock(id);
        if (payload.getQuantiteStock() != null) {
            if (payload.getQuantiteStock() < 0) {
                throw new IllegalArgumentException("quantite_stock doit etre >= 0");
            }
            existing.setQuantiteStock(payload.getQuantiteStock());
        }
        if (payload.getProduit() != null && payload.getProduit().getId() != null) {
            Produit produit = produitRepository.findById(payload.getProduit().getId())
                    .orElseThrow(() -> new RessourceNotFoundException("Produit introuvable : " + payload.getProduit().getId()));
            existing.setProduit(produit);
        }
        if (payload.getStock() != null && payload.getStock().getId() != null) {
            Stock stock = stockRepository.findById(payload.getStock().getId())
                    .orElseThrow(() -> new RessourceNotFoundException("Stock introuvable : " + payload.getStock().getId()));
            existing.setStock(stock);
        }
        LigneStock saved = ligneStockRepository.save(existing);
        syncProduitLigneStockId(saved);
        return saved;
    }

    @Transactional
    public void deleteLigneStock(Long id) {
        if (!ligneStockRepository.existsById(id)) {
            throw new RessourceNotFoundException("LigneStock introuvable : " + id);
        }
        ligneStockRepository.deleteById(id);
    }

    private void attachRelations(LigneStock ligneStock) {
        if (ligneStock.getProduit() != null && ligneStock.getProduit().getId() != null) {
            Produit produit = produitRepository.findById(ligneStock.getProduit().getId())
                    .orElseThrow(() -> new RessourceNotFoundException("Produit introuvable : " + ligneStock.getProduit().getId()));
            ligneStock.setProduit(produit);
        }
        if (ligneStock.getStock() != null && ligneStock.getStock().getId() != null) {
            Stock stock = stockRepository.findById(ligneStock.getStock().getId())
                    .orElseThrow(() -> new RessourceNotFoundException("Stock introuvable : " + ligneStock.getStock().getId()));
            ligneStock.setStock(stock);
        }
    }

    private void syncProduitLigneStockId(LigneStock ligneStock) {
        if (ligneStock.getProduit() != null) {
            Produit produit = ligneStock.getProduit();
            produit.setLigneStockId(String.valueOf(ligneStock.getId()));
            produitRepository.save(produit);
        }
    }
}
