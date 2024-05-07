package com.github.thiagomarqs.gerenciamentopessoas.domain.validator.usecase.person;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class SetMainAddressBusinessRuleValidatorTest {

    private final String state = "Estado Teste";
    private final String city = "Cidade Teste";
    SetMainAddressBusinessRuleValidator businessRuleValidator = new SetMainAddressBusinessRuleValidator();

    @Test
    void shouldPassWhenPersonAndAddressAreActive() {
        var address = Address.builder()
                .address("Rua Teste")
                .cep("12345678")
                .number("123")
                .state(state)
                .city(city)
                .isMain(true)
                .active(true)
                .build();

        var person = Person.builder()
                .fullName("Fulano de Tal")
                .birthDate(LocalDate.of(1990, 1, 1))
                .active(true)
                .build();

        var validationResult = businessRuleValidator.validate(person, address);

        assertFalse(validationResult.hasErrors());
    }

    @Test
    void shouldFailWhenPersonIsInactive() {
        var address = Address.builder()
                .address("Rua Teste")
                .cep("12345678")
                .number("123")
                .state(state)
                .city(city)
                .isMain(true)
                .active(true)
                .build();

        var person = Person.builder()
                .fullName("Fulano de Tal")
                .birthDate(LocalDate.of(1990, 1, 1))
                .active(false)
                .build();

        var validationResult = businessRuleValidator.validate(person, address);

        assertTrue(validationResult.hasErrors());
    }

    @Test
    void shouldFailWhenNewMainAddressIsInactive() {
        var address = Address.builder()
                .address("Rua Teste")
                .cep("12345678")
                .number("123")
                .state(state)
                .city(city)
                .isMain(true)
                .active(false)
                .build();

        var person = Person.builder()
                .fullName("Fulano de Tal")
                .birthDate(LocalDate.of(1990, 1, 1))
                .active(true)
                .build();

        var validationResult = businessRuleValidator.validate(person, address);

        assertTrue(validationResult.hasErrors());
    }

    @Test
    void shouldFailWhenNewMainAddressIsInexistent() {
        var person = Person.builder()
                .fullName("Fulano de Tal")
                .birthDate(LocalDate.of(1990, 1, 1))
                .active(true)
                .build();

        Address address = null;

        assertThrows(NullPointerException.class, () -> businessRuleValidator.validate(person, address));
    }
}