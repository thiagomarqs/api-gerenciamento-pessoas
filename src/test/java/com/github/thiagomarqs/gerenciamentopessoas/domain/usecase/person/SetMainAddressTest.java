package com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.person;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.BusinessRuleException;
import com.github.thiagomarqs.gerenciamentopessoas.domain.repository.PersonRepository;
import com.github.thiagomarqs.gerenciamentopessoas.domain.validator.ValidationResult;
import com.github.thiagomarqs.gerenciamentopessoas.domain.validator.usecase.person.SetMainAddressBusinessRuleValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SetMainAddressTest {

    private final String city = "Cidade Teste";
    private final String state = "Estado Teste";

    @Mock
    private PersonRepository personRepository;

    @Mock
    private FindPeople findPeople;

    @Mock
    private SetMainAddressBusinessRuleValidator businessRuleValidator;

    @InjectMocks
    private SetMainAddress setMainAddress;

    @Test
    void shouldSetMainAddressWhenValidationPasses() {
        var address = Address.builder()
                .id(1L)
                .address("Rua Teste")
                .cep("12345678")
                .number("999")
                .city(city)
                .state(state)
                .isMain(true)
                .active(true)
                .build();

        var newMainAddress = Address.builder()
                .id(2L)
                .address("Avenida Teste")
                .cep("12345678")
                .number("999")
                .city(city)
                .state(state)
                .isMain(false)
                .active(true)
                .build();

        var person = Person.builder()
                .id(1L)
                .fullName("Fulano de Tal")
                .birthDate(LocalDate.of(1990, 1, 1))
                .active(true)
                .address(address)
                .address(newMainAddress)
                .mainAddress(address)
                .build();

        when(findPeople.findOne(1L)).thenReturn(person);
        when(businessRuleValidator.validate(person, newMainAddress)).thenReturn(new ValidationResult());
        when(personRepository.save(person)).thenReturn(person);

        setMainAddress.set(1L, 2L);

        verify(personRepository).save(person);
    }

    @Test
    void shouldNotSetMainAddressWhenValidationFails() {
        var address = Address.builder()
                .id(1L)
                .address("Rua Teste")
                .cep("12345678")
                .number("999")
                .city(city)
                .state(state)
                .isMain(true)
                .active(true)
                .build();

        var newMainAddress = Address.builder()
                .id(2L)
                .address("Avenida Teste")
                .cep("12345678")
                .number("999")
                .city(city)
                .state(state)
                .isMain(false)
                .active(true)
                .build();

        var person = Person.builder()
                .id(1L)
                .fullName("Fulano de Tal")
                .birthDate(LocalDate.of(1990, 1, 1))
                .active(true)
                .address(address)
                .address(newMainAddress)
                .mainAddress(address)
                .build();

        ValidationResult validationResult = new ValidationResult();
        validationResult.addError("error");

        when(findPeople.findOne(1L)).thenReturn(person);
        when(businessRuleValidator.validate(person, newMainAddress)).thenReturn(validationResult);

        assertThrows(BusinessRuleException.class, () -> setMainAddress.set(1L, 2L));

        verifyNoInteractions(personRepository);
    }
}