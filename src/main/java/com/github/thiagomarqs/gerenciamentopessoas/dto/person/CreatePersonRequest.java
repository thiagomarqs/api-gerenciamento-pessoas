package com.github.thiagomarqs.gerenciamentopessoas.dto.person;

import com.github.thiagomarqs.gerenciamentopessoas.dto.address.CreatePersonAddressRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;
import java.util.List;

public record CreatePersonRequest(

        @NotBlank
        String fullName,

        @PastOrPresent
        LocalDate birthDate,

        @NotEmpty
        List<CreatePersonAddressRequest> addresses
) {}
