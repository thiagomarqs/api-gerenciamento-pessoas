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
class RemoveAddressTest {

    @Mock
    PersonRepository personRepository;

    @Mock
    FindPeople findPeople;

    @InjectMocks
    RemoveAddress removeAddress;

    String state = "Estado Teste";
    String city = "Teste";

    @Test
    void removeAddress() {

        Long personId = 1L;
        Long address1Id = 1L;
        Long address2Id = 2L;

        Address address = Address.builder()
                .id(address1Id)
                .address("Avenida Teste")
                .cep("87654321")
                .number("111")
                .city(city)
                .state(state)
                .build();

        Address address2 = Address.builder()
                .id(address2Id)
                .address("Rua Teste")
                .cep("12345678")
                .number("999")
                .city(city)
                .state(state)
                .build();

        Person person = Person.builder()
                .id(personId)
                .fullName("Fulano de Tal")
                .address(address)
                .address(address2)
                .mainAddress(address2)
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();


        assertTrue(person.getAddresses().contains(address));
        assertTrue(person.getAddresses().contains(address2));
        assertEquals(address2, person.getMainAddress());
        person.getAddresses().forEach(a -> assertEquals(person, a.getPerson()));

        when(findPeople.findOne(personId)).thenReturn(person);

        removeAddress.remove(personId, address1Id);

        verify(personRepository).save(person);

        assertFalse(person.getAddresses().contains(address));
        assertEquals(address2, person.getMainAddress());
    }

    @Test
    void shouldThrowIfRemovingTheOnlyExistentAddress() {

        Long personId = 1L;
        Long address1Id = 1L;

        Address address = Address.builder()
                .id(address1Id)
                .address("Avenida Teste")
                .cep("87654321")
                .number("111")
                .city(city)
                .state(state)
                .build();

        Person person = Person.builder()
                .id(personId)
                .fullName("Fulano de Tal")
                .address(address)
                .mainAddress(address)
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();


        assertTrue(person.getAddresses().contains(address));
        assertEquals(person, address.getPerson());
        assertEquals(1, person.getAddresses().size());

        when(findPeople.findOne(personId)).thenReturn(person);

        assertThrows(BusinessRuleException.class, () -> removeAddress.remove(personId, address1Id));

        verifyNoInteractions(personRepository);

        assertTrue(person.getAddresses().contains(address));
        assertEquals(person, address.getPerson());
        assertEquals(1, person.getAddresses().size());

    }

    @Test
    void shouldSetRemainingAddressAsMainIfRemovingTheMainAddressAndThereIsOnlyOneAddressRemaining() {

        Long personId = 1L;
        Long address1Id = 1L;
        Long address2Id = 2L;

        Address address = Address.builder()
                .id(address1Id)
                .address("Avenida Teste")
                .cep("87654321")
                .number("111")
                .city(city)
                .state(state)
                .build();

        Address address2 = Address.builder()
                .id(address2Id)
                .address("Rua Teste")
                .cep("12345678")
                .number("999")
                .city(city)
                .state(state)
                .build();

        Person person = Person.builder()
                .id(personId)
                .fullName("Fulano de Tal")
                .address(address)
                .address(address2)
                .mainAddress(address2)
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        when(findPeople.findOne(personId)).thenReturn(person);

        assertTrue(person.getAddresses().contains(address));
        assertTrue(person.getAddresses().contains(address2));
        assertEquals(address2, person.getMainAddress());
        person.getAddresses().forEach(a -> assertEquals(person, a.getPerson()));

        removeAddress.remove(personId, address2Id);

        verify(personRepository).save(person);

        assertFalse(person.getAddresses().contains(address2));
        assertEquals(address, person.getMainAddress());

    }

    @Test
    void shouldThrowIfInexistentAddress () {

        Long personId = 1L;
        Long address1Id = 1L;
        Long inexistentId = 2L;

        Address address = Address.builder()
                .id(address1Id)
                .address("Avenida Teste")
                .cep("87654321")
                .number("111")
                .city(city)
                .state(state)
                .build();

        Person person = Person.builder()
                .id(personId)
                .fullName("Fulano de Tal")
                .address(address)
                .mainAddress(address)
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();


        assertTrue(person.getAddresses().contains(address));
        assertEquals(person, address.getPerson());
        assertEquals(1, person.getAddresses().size());
        person.getAddresses().forEach(a -> assertNotEquals(inexistentId, a.getId()));

        when(findPeople.findOne(personId)).thenReturn(person);

        assertThrows(EntityNotFoundException.class, () -> removeAddress.remove(personId, inexistentId));

        verifyNoInteractions(personRepository);

        assertTrue(person.getAddresses().contains(address));
        assertEquals(person, address.getPerson());
        assertEquals(1, person.getAddresses().size());

    }

}