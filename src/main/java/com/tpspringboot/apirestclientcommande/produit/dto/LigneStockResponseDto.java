package com.tpspringboot.apirestclientcommande.produit.dto;

public record LigneStockResponseDto(
        Long id,
        Long stock_id,
        Integer quantite_stock,
        ProduitLiteDto produit
) {
}
