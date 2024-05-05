package com.github.thiagomarqs.gerenciamentopessoas.controller.dto.address.request;

import jakarta.validation.constraints.Pattern;

public record EditAddressRequest(
        String address,

        @Pattern(regexp = "\\d{5}-\\d{3}")
        String cep,

        String number,
        String city,
        String state,
        boolean active
) {
}
