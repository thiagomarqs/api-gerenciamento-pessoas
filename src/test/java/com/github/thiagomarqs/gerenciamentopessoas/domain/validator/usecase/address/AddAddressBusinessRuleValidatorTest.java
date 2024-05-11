package com.github.thiagomarqs.gerenciamentopessoas.domain.validator.usecase.address;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;
import com.github.thiagomarqs.gerenciamentopessoas.domain.validator.address.AddressValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddAddressBusinessRuleValidatorTest {

    @Mock
    private AddressValidator addressValidator;

    @InjectMocks
    private AddAddressBusinessRuleValidator businessRuleValidator;

    @Test
    void shouldPassWhenAddingValidAddress() {
        var newAddress = Address.builder()
                .address("Rua Teste")
                .cep("12345678")
                .number("999")
                .city("Cidade Teste")
                .state("Estado Teste")
                .build();

        var person = Person.builder()
                .id(1L)
                .fullName("Fulano de Tal")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        newAddress.setPerson(person);

        when(addressValidator.validateOneByCep(newAddress)).thenReturn(true);

        var validationResult = businessRuleValidator.validate(newAddress);

        assertFalse(validationResult.hasErrors());
    }

    @Test
    void shouldFailWhenAddingDuplicatedAddressToTheSamePerson() {
        var address = Address.builder()
                .id(1L)
                .address("Avenida Teste")
                .cep("87654321")
                .number("111")
                .city("Cidade Teste")
                .state("Estado Teste")
                .build();

        var newAddress = Address.builder()
                .address("Avenida Teste")
                .cep("87654321")
                .number("111")
                .city("Cidade Teste")
                .state("Estado Teste")
                .build();

        var person = Person.builder()
                .id(1L)
                .fullName("Fulano de Tal")
                .address(address)
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        newAddress.setPerson(person);

        when(addressValidator.validateOneByCep(newAddress)).thenReturn(true);

        var validationResult = businessRuleValidator.validate(newAddress);

        assertTrue(validationResult.hasErrors());
    }

    @Test
    void shouldFailWhenAddingAddressWithInvalidCep() {
        var newAddress = Address.builder()
                .address("Avenida Teste")
                .cep("87654321")
                .number("111")
                .city("Cidade Teste")
                .state("Estado Teste")
                .build();

        var person = Person.builder()
                .id(1L)
                .fullName("Fulano de Tal")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        newAddress.setPerson(person);

        when(addressValidator.validateOneByCep(newAddress)).thenReturn(false);

        var validationResult = businessRuleValidator.validate(newAddress);

        assertTrue(validationResult.hasErrors());
    }
}