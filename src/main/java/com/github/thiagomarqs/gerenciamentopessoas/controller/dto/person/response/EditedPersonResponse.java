package com.github.thiagomarqs.gerenciamentopessoas.controller.dto.person.response;

public record EditedPersonResponse(
        Long id,
        String fullName,
        String birthDate,
        boolean active
) {
}
