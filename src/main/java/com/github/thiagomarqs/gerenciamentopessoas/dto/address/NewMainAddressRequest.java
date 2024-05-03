package com.github.thiagomarqs.gerenciamentopessoas.dto.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NewMainAddressRequest(
        @NotNull
        Long id
) {
}
