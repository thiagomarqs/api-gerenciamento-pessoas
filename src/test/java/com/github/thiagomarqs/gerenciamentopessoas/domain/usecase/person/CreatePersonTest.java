package com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.person;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.BusinessRuleException;
import com.github.thiagomarqs.gerenciamentopessoas.domain.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreatePersonTest {

    @Mock
    PersonRepository personRepository;

    @InjectMocks
    CreatePerson createPerson;

    String state = "Estado Teste";
    String city = "Cidade Teste";

    @Test
    void createPerson() {

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
                .mainAddress(address)
                .build();

        when(personRepository.save(person)).thenReturn(person);

        createPerson.create(person);
    }

    @Test
    void shouldThrowExceptionWhenNoMainAddressWasSetAndThereIsMoreThanOneAddress() {

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

        assertThrows(BusinessRuleException.class, () -> createPerson.create(person));

    }

    @Test
    void shouldSetMainAddressAutomaticallyWhenNoMainAddressIsTrySetAndThereIsOnlyOneAddress() {

        var address = Address.builder()
                .address("Rua Teste")
                .cep("12345678")
                .number("123")
                .city(city)
                .state(state)
                .build();

        var person = Person.builder()
                .fullName("Fulano de Tal")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address(address)
                .build();

        assertNull(person.getMainAddress());

        when(personRepository.save(person)).thenReturn(person);

        createPerson.create(person);

        assertNotNull(person.getMainAddress());
    }

    @Test
    void shouldThrowWhenCreatingPersonWithoutAddress() {

        var person = Person.builder()
                .fullName("Fulano de Tal")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        assertThrows(BusinessRuleException.class, () -> createPerson.create(person));

    }
}