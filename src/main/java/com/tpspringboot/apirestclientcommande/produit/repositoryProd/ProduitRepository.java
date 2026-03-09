package com.tpspringboot.apirestclientcommande.produit.repositoryProd;

import com.tpspringboot.apirestclientcommande.produit.modeleProd.Produit;
import org.springframework.data.repository.CrudRepository;

public interface ProduitRepository extends CrudRepository<Produit , Long> {
}
