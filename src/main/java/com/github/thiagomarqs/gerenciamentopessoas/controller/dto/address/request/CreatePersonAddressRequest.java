package com.github.thiagomarqs.gerenciamentopessoas.controller.dto.address.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreatePersonAddressRequest(

        @NotBlank
        String address,

        @NotBlank
        @Pattern(regexp = "\\d{5}-\\d{3}")
        String cep,

        @NotBlank
        String number,

        @NotBlank
        String city,

        @NotBlank
        String state,

        Boolean isMain
) { }
