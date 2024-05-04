package com.github.thiagomarqs.gerenciamentopessoas.dto.person.request;

import jakarta.validation.constraints.NotEmpty;

public record GetManyPeopleRequest(

        @NotEmpty
        Iterable<Long> ids
) {
}
