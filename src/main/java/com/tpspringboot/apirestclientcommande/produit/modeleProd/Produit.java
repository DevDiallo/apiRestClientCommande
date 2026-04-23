package com.tpspringboot.apirestclientcommande.produit.modeleProd;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String description;
    private Double prix;

    // Stored as image_path in DB, exposed as imageUrl to frontend
    @Column(name = "image_path")
    @JsonAlias("imagePath")   // accept both imageUrl (default) and imagePath from frontend
    private String imageUrl;

    private Long categorieId;
    private String ligneStockId;

    @JsonProperty("imagePath")
    public String getImagePath() {
        return imageUrl;
    }
}