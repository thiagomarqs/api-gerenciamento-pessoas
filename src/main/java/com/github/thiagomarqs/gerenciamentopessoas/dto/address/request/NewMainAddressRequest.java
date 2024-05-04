package com.github.thiagomarqs.gerenciamentopessoas.dto.address.request;

import jakarta.validation.constraints.NotNull;

public record NewMainAddressRequest(
        @NotNull
        Long id
) {
}
