package com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.person;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.BusinessRuleException;
import com.github.thiagomarqs.gerenciamentopessoas.domain.repository.PersonRepository;
import com.github.thiagomarqs.gerenciamentopessoas.domain.validator.ValidationResult;
import com.github.thiagomarqs.gerenciamentopessoas.domain.validator.usecase.person.CreatePersonBusinessRuleValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreatePersonTest {

    private final String city = "Cidade Teste";
    private final String state = "Estado Teste";
    @Mock
    private PersonRepository personRepository;

    @Mock
    private CreatePersonBusinessRuleValidator businessRuleValidator;

    @InjectMocks
    private CreatePerson createPerson;

    @Test
    void shouldCreatePersonWhenValidationPasses() {

        var address = Address.builder()
                .address("Rua Teste")
                .cep("12345678")
                .number("999")
                .city(city)
                .state(state)
                .isMain(true)
                .active(true)
                .build();

        var person = Person.builder()
                .fullName("Fulano de Tal")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address(address)
                .mainAddress(address)
                .build();

        when(businessRuleValidator.validate(person)).thenReturn(new ValidationResult());
        when(personRepository.save(person)).thenReturn(person);

        createPerson.create(person);

        verify(personRepository).save(person);
    }

    @Test
    void shouldNotCreatePersonWhenValidationFails() {

        var address = Address.builder()
                .address("Rua Teste")
                .cep("12345678")
                .number("999")
                .city(city)
                .state(state)
                .isMain(true)
                .active(true)
                .build();

        var person = Person.builder()
                .fullName("Fulano de Tal")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address(address)
                .mainAddress(address)
                .build();

        ValidationResult validationResult = new ValidationResult();
        validationResult.addError("error");

        when(businessRuleValidator.validate(person)).thenReturn(validationResult);

        assertThrows(BusinessRuleException.class, () -> createPerson.create(person));

        verifyNoInteractions(personRepository);
    }
}