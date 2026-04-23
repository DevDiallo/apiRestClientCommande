package com.tpspringboot.apirestclientcommande.produit.repositoryProd;

import com.tpspringboot.apirestclientcommande.produit.modeleProd.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
}
