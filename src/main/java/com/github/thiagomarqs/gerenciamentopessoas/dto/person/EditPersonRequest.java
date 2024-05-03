package com.github.thiagomarqs.gerenciamentopessoas.dto.person;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record EditPersonRequest(
        @NotBlank
        String fullName,

        @NotNull
        @PastOrPresent
        LocalDate birthDate,

        @NotNull
        boolean active
) {
}
