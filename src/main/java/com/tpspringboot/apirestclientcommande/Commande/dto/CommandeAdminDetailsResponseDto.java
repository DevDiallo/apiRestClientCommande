package com.tpspringboot.apirestclientcommande.Commande.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CommandeAdminDetailsResponseDto(
        Long id,
        LocalDateTime dateValidation,
        Double total,
        Long userId,
        ClientDetailsDto client,
        List<CommandeItemResponseDto> articles
) {
}
