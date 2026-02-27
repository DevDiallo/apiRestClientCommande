package com.tpspringboot.apirestclientcommande.Commande.repositoryCO;

import com.tpspringboot.apirestclientcommande.Commande.modeleCO.Commande;
import org.springframework.data.repository.CrudRepository;

public interface CommandeRepository extends CrudRepository<Commande, Long> {
}
