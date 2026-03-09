package com.tpspringboot.apirestclientcommande.produit.modeleProd;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Produit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;
    private String nom ;
    private Double prix ;
    /*
    @OneToMany(mappedBy = "produit" , cascade = CascadeType.ALL)
    @JsonManagedReference(value = "produit-commandeProduit")
    private List<Commande_produit> commandeProduits = new ArrayList<>();
     */

}
