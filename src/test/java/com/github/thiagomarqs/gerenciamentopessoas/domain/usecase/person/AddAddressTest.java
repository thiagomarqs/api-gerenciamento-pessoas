package com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.person;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.BusinessRuleException;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.EntityNotFoundException;
import com.github.thiagomarqs.gerenciamentopessoas.domain.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddAddressTest {

    @Mock
    PersonRepository personRepository;

    @Mock
    FindPeople findPeople;

    @InjectMocks
    AddAddress addAddress;

    String state = "Estado Teste";
    String city = "Teste";

    @Test
    void shouldAddAddress() {

        var address = Address.builder()
                .address("Avenida Teste")
                .cep("87654321")
                .number("111")
                .city(city)
                .state(state)
                .build();

        var newAddress = Address.builder()
                .address("Rua Teste")
                .cep("12345678")
                .number("999")
                .city(city)
                .state(state)
                .build();

        var personId = 1L;

        var person = Person.builder()
                .id(personId)
                .fullName("Fulano de Tal")
                .address(address)
                .mainAddress(address)
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        when(findPeople.findOne(personId)).thenReturn(person);

        addAddress.add(personId, newAddress);

        verify(personRepository).save(person);

        assertTrue(person.getAddresses().contains(newAddress));
        assertEquals(person, newAddress.getPerson());

    }

    @Test
    void shouldThrowWhenAddingDuplicatedAddressToTheSamePerson() {

        var address = Address.builder()
                .id(1L)
                .address("Avenida Teste")
                .cep("87654321")
                .number("111")
                .city(city)
                .state(state)
                .build();

        var newAddress = Address.builder()
                .address("Avenida Teste")
                .cep("87654321")
                .number("111")
                .city(city)
                .state(state)
                .build();

        var personId = 1L;

        var person = Person.builder()
                .id(personId)
                .fullName("Fulano de Tal")
                .address(address)
                .mainAddress(address)
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        when(findPeople.findOne(personId)).thenReturn(person);

        assertThrows(BusinessRuleException.class, () -> addAddress.add(personId, newAddress));
        verifyNoInteractions(personRepository);
        assertFalse(person.getAddresses().contains(newAddress));

    }

    @Test
    void shouldThrowWhenAddingAddressToNonExistentPerson() {

        var newAddress = Address.builder()
                .address("Avenida Teste")
                .cep("87654321")
                .number("111")
                .city(city)
                .state(state)
                .build();

        var personId = 1L;

        when(findPeople.findOne(personId)).thenThrow(EntityNotFoundException.of(personId, Person.class));

        assertThrows(EntityNotFoundException.class, () -> addAddress.add(personId, newAddress));
        verifyNoInteractions(personRepository);

    }


}