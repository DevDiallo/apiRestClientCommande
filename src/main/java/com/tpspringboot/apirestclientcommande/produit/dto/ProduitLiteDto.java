package com.tpspringboot.apirestclientcommande.produit.dto;

public record ProduitLiteDto(
        Long id,
        String nom,
        Double prix,
        String imagePath,
        Long categorieId
) {
}
