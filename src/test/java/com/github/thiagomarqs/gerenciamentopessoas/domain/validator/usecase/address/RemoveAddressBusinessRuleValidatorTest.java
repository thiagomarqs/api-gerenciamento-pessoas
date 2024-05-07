package com.github.thiagomarqs.gerenciamentopessoas.domain.validator.usecase.address;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RemoveAddressBusinessRuleValidatorTest {

    private final String city = "Cidade Teste";
    private final String state = "Estado Teste";
    RemoveAddressBusinessRuleValidator businessRuleValidator = new RemoveAddressBusinessRuleValidator();

    @Test
    void shouldPassWhenRemovingNonMainAddress() {
        var address1 = Address.builder()
                .id(1L)
                .address("Avenida Teste")
                .cep("87654321")
                .number("111")
                .city(city)
                .state(state)
                .build();

        var address2 = Address.builder()
                .id(2L)
                .address("Rua Teste")
                .cep("12345678")
                .number("999")
                .city(city)
                .state(state)
                .isMain(true)
                .build();

        var person = Person.builder()
                .id(1L)
                .fullName("Fulano de Tal")
                .address(address1)
                .address(address2)
                .mainAddress(address2)
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        var validationResult = businessRuleValidator.validate(address1.getId(), person);

        assertFalse(validationResult.hasErrors());
    }

    @Test
    void shouldFailWhenRemovingTheOnlyExistentAddress() {
        var address1 = Address.builder()
                .id(1L)
                .address("Avenida Teste")
                .cep("87654321")
                .number("111")
                .city(city)
                .state(state)
                .isMain(true)
                .build();

        var person = Person.builder()
                .id(1L)
                .fullName("Fulano de Tal")
                .address(address1)
                .mainAddress(address1)
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        var validationResult = businessRuleValidator.validate(address1.getId(), person);

        assertTrue(validationResult.hasErrors());
    }

    @Test
    void shouldFailWhenRemovingMainAddressAndPersonHasMoreThanTwoAddresses() {
        var address1 = Address.builder()
                .id(1L)
                .address("Avenida Teste")
                .cep("87654321")
                .number("111")
                .city(city)
                .state(state)
                .isMain(true)
                .build();

        var address2 = Address.builder()
                .id(2L)
                .address("Rua Teste")
                .cep("12345678")
                .number("999")
                .city(city)
                .state(state)
                .build();

        var address3 = Address.builder()
                .id(3L)
                .address("Alameda Teste")
                .cep("99999999")
                .number("444")
                .city(city)
                .state(state)
                .build();

        var person = Person.builder()
                .id(1L)
                .fullName("Fulano de Tal")
                .address(address1)
                .address(address2)
                .address(address3)
                .mainAddress(address1)
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        var validationResult = businessRuleValidator.validate(address1.getId(), person);

        assertTrue(validationResult.hasErrors());
    }

    @Test
    void shouldFailWhenRemovingInexistentAddress() {
        var address1 = Address.builder()
                .id(1L)
                .address("Avenida Teste")
                .cep("87654321")
                .number("111")
                .city(city)
                .state(state)
                .build();

        var person = Person.builder()
                .id(1L)
                .fullName("Fulano de Tal")
                .address(address1)
                .mainAddress(address1)
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        var validationResult = businessRuleValidator.validate(2L, person);

        assertTrue(validationResult.hasErrors());
    }
}