package com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.address;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.BusinessRuleException;
import com.github.thiagomarqs.gerenciamentopessoas.domain.repository.AddressRepository;
import com.github.thiagomarqs.gerenciamentopessoas.domain.repository.PersonRepository;
import com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.person.FindPeople;
import com.github.thiagomarqs.gerenciamentopessoas.domain.validator.ValidationResult;
import com.github.thiagomarqs.gerenciamentopessoas.domain.validator.usecase.address.RemoveAddressBusinessRuleValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RemoveAddressTest {

    private final String city = "Cidade Teste";
    private final String state = "Estado Teste";

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private FindPeople findPeople;

    @Mock
    private RemoveAddressBusinessRuleValidator businessRuleValidator;

    @InjectMocks
    private RemoveAddress removeAddress;

    @Test
    void shouldRemoveAddressWhenValidationPasses() {
        var address = Address.builder()
                .id(1L)
                .address("Avenida Teste")
                .cep("12345678")
                .number("999")
                .city(city)
                .state(state)
                .isMain(true)
                .build();

        var toRemove = Address.builder()
                .id(2L)
                .address("Rua Teste")
                .cep("12345678")
                .number("999")
                .city(city)
                .state(state)
                .build();

        var person = Person.builder()
                .id(1L)
                .fullName("Fulano de Tal")
                .address(address)
                .address(toRemove)
                .mainAddress(address)
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        when(findPeople.findOne(1L)).thenReturn(person);
        when(businessRuleValidator.validate(toRemove.getId(), person)).thenReturn(new ValidationResult());

        removeAddress.remove(1L, toRemove.getId());

        verify(addressRepository).deleteById(toRemove.getId());
    }

    @Test
    void shouldNotRemoveAddressWhenValidationFails() {

        var address = Address.builder()
                .id(1L)
                .address("Avenida Teste")
                .cep("12345678")
                .number("999")
                .city(city)
                .state(state)
                .isMain(true)
                .build();

        var toRemove = Address.builder()
                .id(2L)
                .address("Rua Teste")
                .cep("12345678")
                .number("999")
                .city(city)
                .state(state)
                .build();

        var person = Person.builder()
                .id(1L)
                .fullName("Fulano de Tal")
                .address(address)
                .address(toRemove)
                .mainAddress(address)
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        ValidationResult validationResult = new ValidationResult();
        validationResult.addError("error");

        when(findPeople.findOne(1L)).thenReturn(person);
        when(businessRuleValidator.validate(address.getId(), person)).thenReturn(validationResult);

        assertThrows(BusinessRuleException.class, () -> removeAddress.remove(1L, address.getId()));

        verifyNoInteractions(addressRepository);
    }
}