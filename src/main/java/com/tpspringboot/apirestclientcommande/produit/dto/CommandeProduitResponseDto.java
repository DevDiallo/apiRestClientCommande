package com.tpspringboot.apirestclientcommande.produit.dto;

public record CommandeProduitResponseDto(
        Long id,
        Long produitId,
        Long commandeId,
        Integer quantite,
        Double prixUnitaire,
        Double sousTotal
) {
}
