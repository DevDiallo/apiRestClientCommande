package com.tpspringboot.apirestclientcommande.produit.dto;

import java.time.LocalDateTime;

public record StockResponseDto(
        Long id,
        LocalDateTime dateStock
) {
}
