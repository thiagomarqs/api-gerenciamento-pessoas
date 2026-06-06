package com.github.thiagomarqs.gerenciamentopessoas.controller.dto.professionaldata.response;

public record ProfessionalDataResponse(
        Long id,
        String companyName,
        String contractType,
        String employmentStartDate
) {}
