package com.github.thiagomarqs.gerenciamentopessoas.controller.dto.person.response;

import com.github.thiagomarqs.gerenciamentopessoas.controller.dto.address.response.AddressResponse;
import com.github.thiagomarqs.gerenciamentopessoas.controller.dto.professionaldata.response.ProfessionalDataResponse;

import java.util.List;

public record PersonResponse(
        Long id,
        String fullName,
        String birthDate,
        AddressResponse mainAddress,
        List<AddressResponse> addresses,
        boolean active,
        ProfessionalDataResponse professionalData
) {
}
