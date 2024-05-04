package com.github.thiagomarqs.gerenciamentopessoas.dto.person.response;

public record EditedPersonResponse(
        Long id,
        String fullName,
        String birthDate,
        boolean active
) {
}
