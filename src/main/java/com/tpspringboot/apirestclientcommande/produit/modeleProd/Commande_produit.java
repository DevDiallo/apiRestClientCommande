package com.tpspringboot.apirestclientcommande.produit.modeleProd;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tpspringboot.apirestclientcommande.Commande.modeleCO.Commande;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Commande_produit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;
    private Integer quantite ;
    private Double prixUnitaire;
    private Double sousTotal;

    @ManyToOne
    @JoinColumn(name = "commande_id" , nullable = false)
    @JsonBackReference(value = "commande-commandeProduit")
    private Commande commande ;

    @ManyToOne
    @JoinColumn(name = "produit_id" , nullable = false)
    //@JsonBackReference(value = "produit-commandeProduit")
    private Produit produit ;

    @PrePersist
    @PreUpdate
    public void computeTotals() {
        if (prixUnitaire == null && produit != null && produit.getPrix() != null) {
            prixUnitaire = produit.getPrix();
        }
        if (prixUnitaire != null && quantite != null) {
            sousTotal = prixUnitaire * quantite;
        }
    }

    @JsonProperty("commandeId")
    public Long getCommandeId() {
        return commande != null ? commande.getId() : null;
    }

    @JsonProperty("produitId")
    public Long getProduitId() {
        return produit != null ? produit.getId() : null;
    }
}
