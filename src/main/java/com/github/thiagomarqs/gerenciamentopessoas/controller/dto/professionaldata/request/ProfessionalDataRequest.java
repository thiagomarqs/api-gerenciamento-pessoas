package com.github.thiagomarqs.gerenciamentopessoas.controller.dto.professionaldata.request;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.ContractType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record ProfessionalDataRequest(

        @NotBlank
        String companyName,

        @NotNull
        ContractType contractType,

        @PastOrPresent
        LocalDate employmentStartDate
) {}
