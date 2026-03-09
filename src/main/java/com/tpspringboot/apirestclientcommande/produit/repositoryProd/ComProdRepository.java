package com.tpspringboot.apirestclientcommande.produit.repositoryProd;

import com.tpspringboot.apirestclientcommande.produit.modeleProd.Commande_produit;
import org.springframework.data.repository.CrudRepository;

public interface ComProdRepository extends CrudRepository<Commande_produit, Long> {
}
