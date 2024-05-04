package com.github.thiagomarqs.gerenciamentopessoas.dto.person.response;

import com.github.thiagomarqs.gerenciamentopessoas.dto.address.response.AddressResponse;

import java.util.List;

public record PersonResponse(
        Long id,
        String fullName,
        String birthDate,
        AddressResponse mainAddress,
        List<AddressResponse> addresses,
        boolean active
) {
}
