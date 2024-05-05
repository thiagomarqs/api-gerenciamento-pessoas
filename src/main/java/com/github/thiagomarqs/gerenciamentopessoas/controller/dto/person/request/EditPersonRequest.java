package com.github.thiagomarqs.gerenciamentopessoas.controller.dto.person.request;

import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record EditPersonRequest(

        @Size(min = 3, max = 100)
        String fullName,

        @PastOrPresent
        LocalDate birthDate,

        boolean active
) {
}
