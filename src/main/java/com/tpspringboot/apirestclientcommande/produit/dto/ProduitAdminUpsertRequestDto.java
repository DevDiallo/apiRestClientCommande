package com.tpspringboot.apirestclientcommande.produit.dto;

public record ProduitAdminUpsertRequestDto(
        String nom,
        String description,
        Double prix,
        String imagePath,
        Long categorieId,
        Long ligneStockId,
        Integer quantiteStock
) {
}
