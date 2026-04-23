package com.tpspringboot.apirestclientcommande.Commande.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CommandeResponseDto(
        Long id,
        LocalDateTime dateValidation,
        Double total,
        Long userId,
        Long clientId,
        Long utilisateurId,
        ClientDetailsDto client,
        List<CommandeItemResponseDto> items,
        List<CommandeItemResponseDto> articles
) {
}
