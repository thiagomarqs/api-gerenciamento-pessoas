package com.github.thiagomarqs.gerenciamentopessoas.dto.person;

import jakarta.validation.constraints.NotEmpty;

public record GetManyPeopleRequest(

        @NotEmpty
        Iterable<Long> ids
) {
}
