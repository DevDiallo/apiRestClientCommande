package com.tpspringboot.apirestclientcommande.User.dto;

import java.util.List;

public record UserResponseDto(
        Long id,
        String nom,
        String prenom,
        String email,
        String telephone,
        String username,
        String role,
        List<String> roles
) {
}
