package com.github.thiagomarqs.gerenciamentopessoas.controller.dto.address.request;

import jakarta.validation.constraints.NotNull;

public record NewMainAddressRequest(
        @NotNull
        Long id
) {
}
