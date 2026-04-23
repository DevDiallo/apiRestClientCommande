package com.tpspringboot.apirestclientcommande.produit.repositoryProd;

import com.tpspringboot.apirestclientcommande.produit.modeleProd.LigneStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LigneStockRepository extends JpaRepository<LigneStock, Long> {
	List<LigneStock> findByProduit_Id(Long produitId);
	void deleteByProduit_Id(Long produitId);
}
