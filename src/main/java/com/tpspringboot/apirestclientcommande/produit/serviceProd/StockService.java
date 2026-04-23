package com.tpspringboot.apirestclientcommande.produit.serviceProd;

import com.tpspringboot.apirestclientcommande.Exceptions.RessourceNotFoundException;
import com.tpspringboot.apirestclientcommande.produit.modeleProd.LigneStock;
import com.tpspringboot.apirestclientcommande.produit.modeleProd.Produit;
import com.tpspringboot.apirestclientcommande.produit.modeleProd.Stock;
import com.tpspringboot.apirestclientcommande.produit.repositoryProd.ProduitRepository;
import com.tpspringboot.apirestclientcommande.produit.repositoryProd.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final ProduitRepository produitRepository;

    public Iterable<Stock> getStocks() {
        return stockRepository.findAll();
    }

    public Stock getStock(Long id) {
        return stockRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Stock introuvable : " + id));
    }

    @Transactional
    public Stock saveStock(Stock stock) {
        if (stock.getLignesStock() != null) {
            for (LigneStock ligne : stock.getLignesStock()) {
                ligne.setStock(stock);
                attachProduit(ligne);
            }
        }
        return stockRepository.save(stock);
    }

    @Transactional
    public Stock updateStock(Long id, Stock payload) {
        Stock existing = getStock(id);
        if (payload.getDateStock() != null) {
            existing.setDateStock(payload.getDateStock());
        }
        if (payload.getLignesStock() != null) {
            existing.getLignesStock().clear();
            for (LigneStock ligne : payload.getLignesStock()) {
                ligne.setStock(existing);
                attachProduit(ligne);
                existing.getLignesStock().add(ligne);
            }
        }
        return stockRepository.save(existing);
    }

    public void deleteStock(Long id) {
        if (!stockRepository.existsById(id)) {
            throw new RessourceNotFoundException("Stock introuvable : " + id);
        }
        stockRepository.deleteById(id);
    }

    private void attachProduit(LigneStock ligne) {
        if (ligne.getProduit() != null && ligne.getProduit().getId() != null) {
            Produit produit = produitRepository.findById(ligne.getProduit().getId())
                    .orElseThrow(() -> new RessourceNotFoundException("Produit introuvable : " + ligne.getProduit().getId()));
            ligne.setProduit(produit);
        }
    }
}
