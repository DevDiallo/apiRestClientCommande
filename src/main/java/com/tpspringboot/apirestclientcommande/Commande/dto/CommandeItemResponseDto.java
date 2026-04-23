package com.tpspringboot.apirestclientcommande.Commande.dto;

public record CommandeItemResponseDto(
        Long produitId,
        String nom,
        Integer quantite,
        Double prixUnitaire,
        Double sousTotal
) {
}
