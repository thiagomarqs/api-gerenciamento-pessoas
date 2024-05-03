package com.github.thiagomarqs.gerenciamentopessoas.dto.person;

public record EditedPersonResponse(
        Long id,
        String fullName,
        String birthDate,
        boolean active
) {
}
