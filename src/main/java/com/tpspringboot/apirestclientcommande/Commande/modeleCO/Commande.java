package com.tpspringboot.apirestclientcommande.Commande.modeleCO;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tpspringboot.apirestclientcommande.User.modeleCL.User;
import com.tpspringboot.apirestclientcommande.produit.modeleProd.Commande_produit;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Data
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Commande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    private java.time.LocalDateTime dateValidation;
    private Double total;

    @ManyToOne
    @JoinColumn(name="user_id" , nullable = false)
    @JsonBackReference
    private User user;

    @OneToMany(mappedBy = "commande" , cascade = CascadeType.ALL)
    @JsonManagedReference(value = "commande-commandeProduit")
    private List<Commande_produit> commandeProduits = new ArrayList<>() ;

    @PrePersist
    public void prePersist() {
        if (dateValidation == null) {
            dateValidation = java.time.LocalDateTime.now();
        }
    }

    @JsonProperty("items")
    public List<Map<String, Object>> getItems() {
        return commandeProduits.stream().map(cp -> {
            Map<String, Object> item = new HashMap<>();
            item.put("produitId", cp.getProduit() != null ? cp.getProduit().getId() : null);
            item.put("nom", cp.getProduit() != null ? cp.getProduit().getNom() : null);
            item.put("prixUnitaire", cp.getPrixUnitaire());
            item.put("quantite", cp.getQuantite());
            item.put("sousTotal", cp.getSousTotal());
            return item;
        }).toList();
    }


}
