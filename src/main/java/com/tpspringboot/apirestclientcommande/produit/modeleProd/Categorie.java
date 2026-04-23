package com.tpspringboot.apirestclientcommande.produit.modeleProd;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Data
public class Categorie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id_categorie")
    private Long idCategorie;

    @JsonProperty("categorie_name")
    private String categorieName;

    @Transient
    private List<Produit> produits = new ArrayList<>();
}
