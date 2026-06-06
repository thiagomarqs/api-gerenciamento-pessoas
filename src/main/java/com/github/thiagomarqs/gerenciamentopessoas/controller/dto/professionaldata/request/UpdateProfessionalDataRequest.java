package com.github.thiagomarqs.gerenciamentopessoas.controller.dto.professionaldata.request;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.ContractType;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record UpdateProfessionalDataRequest(

        String companyName,

        ContractType contractType,

        @PastOrPresent
        LocalDate employmentStartDate
) {}
