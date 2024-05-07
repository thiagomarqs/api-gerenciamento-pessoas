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
class EditAddressBusinessRuleValidatorTest {

    private final String state = "Estado Teste";
    private final String city = "Cidade Teste";
    @Mock
    AddressValidator addressValidator;

    @InjectMocks
    EditAddressBusinessRuleValidator businessRuleValidator;

    @Test
    void shouldAllowDeactivatingMainAddressWhenPersonHasOnlyTwoAddresses() {
        var edited = Address.builder()
                .id(1L)
                .address("Rua Teste")
                .cep("12345678")
                .number("123")
                .state(state)
                .city(city)
                .active(false)
                .isMain(true)
                .build();

        var address2 = Address.builder()
                .id(2L)
                .address("Avenida Teste")
                .cep("87654321")
                .number("321")
                .state(state)
                .city(city)
                .active(true)
                .isMain(false)
                .build();

        var person = Person.builder()
                .id(1L)
                .fullName("Fulano de Tal")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address(edited)
                .address(address2)
                .mainAddress(edited)
                .build();

        when(addressValidator.validateCep(edited)).thenReturn(true);

        var validationResult = businessRuleValidator.validate(edited);

        assertFalse(validationResult.hasErrors());
    }

    @Test
    void shouldFailWhenDeactivatingMainAddressAndPersonHasMoreThanTwoAddresses() {
        var edited = Address.builder()
                .id(1L)
                .address("Rua Teste")
                .cep("12345678")
                .number("123")
                .state(state)
                .city(city)
                .active(false)
                .isMain(true)
                .build();

        var address2 = Address.builder()
                .id(2L)
                .address("Avenida Teste")
                .cep("87654321")
                .number("321")
                .state(state)
                .city(city)
                .active(true)
                .build();

        var address3 = Address.builder()
                .id(3L)
                .address("Alameda Teste")
                .cep("99999999")
                .number("444")
                .state(state)
                .city(city)
                .active(true)
                .build();

        var person = Person.builder()
                .id(1L)
                .fullName("Fulano de Tal")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address(edited)
                .address(address2)
                .address(address3)
                .mainAddress(edited)
                .build();

        when(addressValidator.validateCep(edited)).thenReturn(true);

        var validationResult = businessRuleValidator.validate(edited);

        assertTrue(validationResult.hasErrors());
    }

    @Test
    void shouldAllowDeactivatingNonMainAddressWhenPersonHasMoreThanTwoAddresses() {
        var edited = Address.builder()
                .id(1L)
                .address("Rua Teste")
                .cep("12345678")
                .number("123")
                .state(state)
                .city(city)
                .active(false)
                .build();

        var address2 = Address.builder()
                .id(2L)
                .address("Avenida Teste")
                .cep("87654321")
                .number("321")
                .state(state)
                .city(city)
                .active(true)
                .build();

        var address3 = Address.builder()
                .id(3L)
                .address("Alameda Teste")
                .cep("99999999")
                .number("444")
                .state(state)
                .city(city)
                .active(true)
                .isMain(true)
                .build();

        var person = Person.builder()
                .id(1L)
                .fullName("Fulano de Tal")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address(edited)
                .address(address2)
                .address(address3)
                .mainAddress(address3)
                .build();

        when(addressValidator.validateCep(edited)).thenReturn(true);

        var validationResult = businessRuleValidator.validate(edited);

        assertFalse(validationResult.hasErrors());
    }

    @Test
    void shouldFailWhenDeactivatingAddressButAllOthersAreAlreadyDeactivated() {
        var edited = Address.builder()
                .id(1L)
                .address("Rua Teste")
                .cep("12345678")
                .number("123")
                .state(state)
                .city(city)
                .active(false)
                .isMain(true)
                .build();

        var address2 = Address.builder()
                .id(2L)
                .address("Avenida Teste")
                .cep("87654321")
                .number("321")
                .state(state)
                .city(city)
                .active(false)
                .build();

        var address3 = Address.builder()
                .id(3L)
                .address("Alameda Teste")
                .cep("99999999")
                .number("444")
                .state(state)
                .city(city)
                .active(false)
                .build();

        var person = Person.builder()
                .id(1L)
                .fullName("Fulano de Tal")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address(edited)
                .address(address2)
                .address(address3)
                .mainAddress(edited)
                .build();

        when(addressValidator.validateCep(edited)).thenReturn(true);

        var validationResult = businessRuleValidator.validate(edited);

        assertTrue(validationResult.hasErrors());
    }
}