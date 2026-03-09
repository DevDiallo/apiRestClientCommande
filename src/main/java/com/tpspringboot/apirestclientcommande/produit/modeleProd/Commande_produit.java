package com.tpspringboot.apirestclientcommande.produit.modeleProd;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.tpspringboot.apirestclientcommande.Commande.modeleCO.Commande;
import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Entity
public class Commande_produit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;
    private Integer quantite ;

    @ManyToOne
    @JoinColumn(name = "commande_id" , nullable = false)
    @JsonBackReference(value = "commande-commandeProduit")
    private Commande commande ;

    @ManyToOne
    @JoinColumn(name = "produit_id" , nullable = false)
    //@JsonBackReference(value = "produit-commandeProduit")
    private Produit produit ;
}
