package com.tpspringboot.apirestclientcommande.Commande.dto;

public record ClientDetailsDto(
        String nom,
        String prenom,
        String email,
        String telephone
) {
}
