package com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.person;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;
import com.github.thiagomarqs.gerenciamentopessoas.domain.repository.AddressRepository;
import com.github.thiagomarqs.gerenciamentopessoas.domain.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SetMainAddressTest {

    @Mock
    PersonRepository personRepository;

    @Mock
    FindPeople findPeople;

    @Mock
    AddressRepository addressRepository;

    @InjectMocks
    SetMainAddress setMainAddress;

    String state = "Estado Teste";
    String city = "Teste";

    @Test
    void shouldSetMainAddress() {

        Long personId = 1L;
        Long newMainAddressId = 2L;

        var oldMainAddress = Address.builder()
                .id(1L)
                .address("Rua Teste 1")
                .cep("12345678")
                .number("000")
                .city(city)
                .state(state)
                .build();

        var newMainAddress = Address.builder()
                .id(newMainAddressId)
                .address("Rua Teste")
                .cep("12345678")
                .number("999")
                .city(city)
                .state(state)
                .build();

        var person = Person.builder()
                .id(personId)
                .fullName("Fulano de Tal")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address(oldMainAddress)
                .address(newMainAddress)
                .mainAddress(oldMainAddress)
                .build();

        when(findPeople.findOne(personId)).thenReturn(person);
        when(addressRepository.findById(newMainAddressId)).thenReturn(Optional.of(newMainAddress));

        assertEquals(oldMainAddress.getPerson(), person);
        assertEquals(newMainAddress.getPerson(), person);
        assertTrue(person.getAddresses().contains(oldMainAddress));
        assertTrue(person.getAddresses().contains(newMainAddress));

        setMainAddress.set(personId, newMainAddressId);

        assertEquals(newMainAddress, person.getMainAddress());
    }

    @Test
    void shouldThrowWhenSettingMainAddressToInactiveAddress() {

        Long personId = 1L;
        Long newMainAddressId = 2L;

        var oldMainAddress = Address.builder()
                .id(1L)
                .address("Rua Teste 1")
                .cep("12345678")
                .number("000")
                .city(city)
                .state(state)
                .build();

        var newMainAddress = Address.builder()
                .id(newMainAddressId)
                .address("Rua Teste")
                .cep("12345678")
                .number("999")
                .city(city)
                .state(state)
                .active(false)
                .build();

        var person = Person.builder()
                .id(personId)
                .fullName("Fulano de Tal")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address(oldMainAddress)
                .address(newMainAddress)
                .mainAddress(oldMainAddress)
                .build();

        when(findPeople.findOne(personId)).thenReturn(person);
        when(addressRepository.findById(newMainAddressId)).thenReturn(Optional.of(newMainAddress));

        assertEquals(oldMainAddress.getPerson(), person);
        assertEquals(newMainAddress.getPerson(), person);
        assertEquals(person.getMainAddress(), oldMainAddress);
        assertTrue(person.getAddresses().contains(oldMainAddress));
        assertTrue(person.getAddresses().contains(newMainAddress));
        assertFalse(newMainAddress.isActive());

        assertThrows(RuntimeException.class, () -> setMainAddress.set(personId, newMainAddressId));
    }

    @Test
    void shouldThrowWhenSettingMainAddressToSomeoneElsesAddress() {
        Long personId = 1L;
        Long someoneElseId = 2L;
        Long someoneElsesAddressId = 2L;

        var oldMainAddress = Address.builder()
                .id(1L)
                .address("Rua Teste 1")
                .cep("12345678")
                .number("000")
                .city(city)
                .state(state)
                .build();

        var someoneElsesAddress = Address.builder()
                .id(someoneElsesAddressId)
                .address("Rua Teste")
                .cep("12345678")
                .number("999")
                .city(city)
                .state(state)
                .build();

        var someoneElse = Person.builder()
                .id(someoneElseId)
                .fullName("Ciclano de Tal")
                .birthDate(LocalDate.of(1980, 1, 1))
                .address(someoneElsesAddress)
                .mainAddress(someoneElsesAddress)
                .build();

        var person = Person.builder()
                .id(personId)
                .fullName("Fulano de Tal")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address(oldMainAddress)
                .mainAddress(oldMainAddress)
                .build();

        when(findPeople.findOne(personId)).thenReturn(person);
        when(addressRepository.findById(someoneElsesAddressId)).thenReturn(Optional.of(someoneElsesAddress));

        assertEquals(someoneElsesAddress.getPerson(), someoneElse);
        assertTrue(someoneElse.getAddresses().contains(someoneElsesAddress));
        assertEquals(someoneElse.getMainAddress(), someoneElsesAddress);
        assertEquals(oldMainAddress.getPerson(), person);
        assertTrue(person.getAddresses().contains(oldMainAddress));
        assertEquals(person.getMainAddress(), oldMainAddress);

        assertThrows(RuntimeException.class, () -> setMainAddress.set(personId, someoneElsesAddressId));
    }

    @Test
    void shouldThrowWhenSettingMainAddressOfInactivePerson() {
        Long personId = 1L;
        Long newMainAddressId = 2L;

        var oldMainAddress = Address.builder()
                .id(1L)
                .address("Rua Teste 1")
                .cep("12345678")
                .number("000")
                .city(city)
                .state(state)
                .build();

        var newMainAddress = Address.builder()
                .id(newMainAddressId)
                .address("Rua Teste")
                .cep("12345678")
                .number("999")
                .city(city)
                .state(state)
                .build();

        var person = Person.builder()
                .id(personId)
                .fullName("Fulano de Tal")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address(oldMainAddress)
                .address(newMainAddress)
                .mainAddress(oldMainAddress)
                .active(false)
                .build();

        when(findPeople.findOne(personId)).thenReturn(person);
        when(addressRepository.findById(newMainAddressId)).thenReturn(Optional.of(newMainAddress));

        assertEquals(oldMainAddress.getPerson(), person);
        assertEquals(newMainAddress.getPerson(), person);
        assertTrue(person.getAddresses().contains(oldMainAddress));
        assertTrue(person.getAddresses().contains(newMainAddress));
        assertFalse(person.isActive());

        assertThrows(RuntimeException.class, () -> setMainAddress.set(personId, newMainAddressId));
    }

    @Test
    void shouldThrowWhenSettingMainAddressToInexistentAddress() {

        Long personId = 1L;
        Long newMainAddressId = 2L;

        var oldMainAddress = Address.builder()
                .id(1L)
                .address("Rua Teste 1")
                .cep("12345678")
                .number("000")
                .city(city)
                .state(state)
                .build();

        var person = Person.builder()
                .id(personId)
                .fullName("Fulano de Tal")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address(oldMainAddress)
                .mainAddress(oldMainAddress)
                .build();

        when(findPeople.findOne(personId)).thenReturn(person);
        when(addressRepository.findById(newMainAddressId)).thenReturn(Optional.empty());

        assertEquals(oldMainAddress.getPerson(), person);
        assertTrue(person.getAddresses().contains(oldMainAddress));

        assertThrows(RuntimeException.class, () -> setMainAddress.set(personId, newMainAddressId));
    }

}