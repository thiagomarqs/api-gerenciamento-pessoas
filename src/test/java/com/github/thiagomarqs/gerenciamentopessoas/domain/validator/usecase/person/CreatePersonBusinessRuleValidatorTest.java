package com.github.thiagomarqs.gerenciamentopessoas.domain.validator.usecase.person;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;
import com.github.thiagomarqs.gerenciamentopessoas.domain.validator.address.AddressValidationResult;
import com.github.thiagomarqs.gerenciamentopessoas.domain.validator.address.AddressValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreatePersonBusinessRuleValidatorTest {

    @Mock
    AddressValidator addressValidator;

    @InjectMocks
    CreatePersonBusinessRuleValidator businessRuleValidator;

    String state = "Estado Teste";
    String city = "Cidade Teste";

    @Test
    void shouldFailWhenPersonIsNull() {
        var validationResult = businessRuleValidator.validate(null);
        assertTrue(validationResult.hasErrors());
    }

    @Test
    void shouldFailWhenCreatingWithNoMainAddressAndMoreThanOneAddress() {

        var address = Address.builder()
                .address("Rua Teste")
                .cep("12345678")
                .number("123")
                .state(state)
                .city(city)
                .build();

        var address2 = Address.builder()
                .address("Rua Teste 2")
                .cep("12345670")
                .number("321")
                .city(city)
                .state(state)
                .build();

        var person = Person.builder()
                .fullName("Fulano de Tal")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address(address)
                .address(address2)
                .build();

        assertFalse(address.getIsMain());
        assertFalse(address2.getIsMain());

        when(addressValidator.validateManyByCep(any())).thenReturn(new AddressValidationResult());
        var validationResult = businessRuleValidator.validate(person);

        assertTrue(validationResult.hasErrors());
    }

    @Test
    void successWhenCreatingPersonWithOneAddress() {

        var address = Address.builder()
                .address("Rua Teste")
                .cep("12345678")
                .number("123")
                .state(state)
                .city(city)
                .build();

        var person = Person.builder()
                .fullName("Fulano de Tal")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address(address)
                .build();

        when(addressValidator.validateManyByCep(any())).thenReturn(new AddressValidationResult());
        var validationResult = businessRuleValidator.validate(person);

        assertFalse(validationResult.hasErrors());
    }

    @Test
    void shouldFailWhenCreatingPersonWithoutAddress() {

        var person = Person.builder()
                .fullName("Fulano de Tal")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        when(addressValidator.validateManyByCep(any())).thenReturn(new AddressValidationResult());
        var validationResult = businessRuleValidator.validate(person);

        assertTrue(validationResult.hasErrors());
    }

    @Test
    void shouldFailWhenCreatingPersonWithMoreThanOneMainAddresses() {

        var address = Address.builder()
                .address("Rua Teste")
                .cep("12345678")
                .number("123")
                .state(state)
                .city(city)
                .isMain(true)
                .build();

        var address2 = Address.builder()
                .address("Rua Teste 2")
                .cep("12345670")
                .number("321")
                .city(city)
                .state(state)
                .isMain(true)
                .build();

        var person = Person.builder()
                .fullName("Fulano de Tal")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address(address)
                .address(address2)
                .build();

        when(addressValidator.validateManyByCep(any())).thenReturn(new AddressValidationResult());
        var validationResult = businessRuleValidator.validate(person);

        assertTrue(validationResult.hasErrors());
    }
}