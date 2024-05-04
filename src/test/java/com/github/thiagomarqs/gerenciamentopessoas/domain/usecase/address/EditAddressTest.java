package com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.address;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.BusinessRuleException;
import com.github.thiagomarqs.gerenciamentopessoas.domain.repository.AddressRepository;
import com.github.thiagomarqs.gerenciamentopessoas.validation.AddressValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EditAddressTest {

    @Mock
    AddressRepository addressRepository;

    @Mock
    FindAddresses findAddresses;

    @Mock
    AddressValidator addressValidator;

    @InjectMocks
    EditAddress editAddress;

    String state = "Estado Teste";
    String city = "Cidade Teste";

    @Test
    void editAddress() {

        long addressId = 1L;

        var address = Address.builder()
                .id(addressId)
                .address("Rua Teste")
                .cep("12345678")
                .number("123")
                .state(state)
                .city(city)
                .build();

        var person = Person.builder()
                .id(1L)
                .fullName("Fulano de Tal")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address(address)
                .mainAddress(address)
                .build();

        var edited = Address.builder()
                .address("Avenida Teste")
                .cep("87654321")
                .number("321")
                .active(true)
                .city(city)
                .state(state)
                .build();

        assertEquals(person, address.getPerson());
        assertTrue(person.getAddresses().contains(address));
        assertEquals(address, person.getMainAddress());

        when(findAddresses.findOne(addressId)).thenReturn(address);

        editAddress.edit(addressId, edited);

        verify(addressRepository).save(address);

        assertTrue(
                address.getAddress() == edited.getAddress() &&
                address.getCep() == edited.getCep() &&
                address.getNumber() == edited.getNumber() &&
                address.getState() == edited.getState() &&
                address.getCity() == edited.getCity() &&
                address.isActive() == edited.isActive() &&
                address.getPerson() == person
        );

    }

    @Test
    void shouldDeactivateMainAddressAndAutomaticallySetTheRemainingAddressAsTheMainAddressWhenPersonHasOnlyTwoAddresses() {

        long addressId = 1L;
        long address2Id = 2L;

        var address = Address.builder()
                .id(addressId)
                .address("Rua Teste")
                .cep("12345678")
                .number("123")
                .state(state)
                .city(city)
                .active(true)
                .build();

        var address2 = Address.builder()
                .id(address2Id)
                .address("Avenida Teste")
                .cep("87654321")
                .number("321")
                .state(state)
                .city(city)
                .active(true)
                .build();

        var person = Person.builder()
                .id(1L)
                .fullName("Fulano de Tal")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address(address)
                .address(address2)
                .mainAddress(address)
                .build();

        var edited = Address.builder()
                .id(addressId)
                .address("Rua Teste")
                .cep("12345678")
                .number("123")
                .state(state)
                .city(city)
                .active(false)
                .build();

        assertEquals(person, address.getPerson());
        assertTrue(person.getAddresses().contains(address));
        assertEquals(address, person.getMainAddress());
        assertTrue(address.isActive());
        assertTrue(address2.isActive());

        when(findAddresses.findOne(addressId)).thenReturn(address);

        editAddress.edit(addressId, edited);

        verify(addressRepository).save(address);

        assertFalse(address.isActive());
        assertTrue(
        address.getAddress() == edited.getAddress() &&
                address.getCep() == edited.getCep() &&
                address.getNumber() == edited.getNumber() &&
                address.getState() == edited.getState() &&
                address.getCity() == edited.getCity() &&
                address.isActive() == edited.isActive() &&
                address.getPerson() == person
        );
        assertTrue(address2.isActive());
        assertEquals(address2, person.getMainAddress());

    }

    @Test
    void shouldThrowWhenDeactivatingMainAddressAndPersonHasMoreThanTwoAddresses() {

        long addressId = 1L;
        long address2Id = 2L;
        long address3Id = 3L;

        var address = Address.builder()
                .id(addressId)
                .address("Rua Teste")
                .cep("12345678")
                .number("123")
                .state(state)
                .city(city)
                .active(true)
                .build();

        var address2 = Address.builder()
                .id(address2Id)
                .address("Avenida Teste")
                .cep("87654321")
                .number("321")
                .state(state)
                .city(city)
                .active(true)
                .build();

        var address3 = Address.builder()
                .id(address3Id)
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
                .address(address)
                .address(address2)
                .address(address3)
                .mainAddress(address)
                .build();

        var edited = Address.builder()
                .id(addressId)
                .address("Rua Teste")
                .cep("12345678")
                .number("123")
                .state(state)
                .city(city)
                .active(false)
                .build();

        when(findAddresses.findOne(addressId)).thenReturn(address);

        assertEquals(3, person.getAddresses().size());

        person.getAddresses().forEach(a -> {
            assertEquals(person, a.getPerson());
            assertTrue(a.isActive());
        });

        assertThrows(BusinessRuleException.class, () -> editAddress.edit(addressId, edited));

        verifyNoInteractions(addressRepository);

        assertTrue(address.isActive());
        assertEquals(address, person.getMainAddress());

    }

    @Test
    void shouldAllowDeactivatingNonMainAddressWhenPersonHasMoreThanTwoAddresses() {

        long addressId = 1L;
        long address2Id = 2L;
        long address3Id = 3L;

        var address = Address.builder()
                .id(addressId)
                .address("Rua Teste")
                .cep("12345678")
                .number("123")
                .state(state)
                .city(city)
                .active(true)
                .build();

        var address2 = Address.builder()
                .id(address2Id)
                .address("Avenida Teste")
                .cep("87654321")
                .number("321")
                .state(state)
                .city(city)
                .active(true)
                .build();

        var address3 = Address.builder()
                .id(address3Id)
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
                .address(address)
                .address(address2)
                .address(address3)
                .mainAddress(address3)
                .build();

        var edited = Address.builder()
                .id(addressId)
                .address("Rua Teste")
                .cep("12345678")
                .number("123")
                .state(state)
                .city(city)
                .active(false)
                .build();

        assertTrue(address.isActive());
        assertEquals(address3, person.getMainAddress());
        assertTrue(address2.isActive());
        assertTrue(address3.isActive());

        when(findAddresses.findOne(addressId)).thenReturn(address);

        editAddress.edit(addressId, edited);

        verify(addressRepository).save(address);

        assertFalse(address.isActive());
        assertEquals(address3, person.getMainAddress());
        assertTrue(address2.isActive());
        assertTrue(address3.isActive());
    }

    @Test
    void shouldThrowWhenDeactivatingAddressButAllOthersAreAlreadyDeactivated() {

        long addressId = 1L;
        long address2Id = 2L;
        long address3Id = 3L;

        var address = Address.builder()
                .id(addressId)
                .address("Rua Teste")
                .cep("12345678")
                .number("123")
                .state(state)
                .city(city)
                .active(true)
                .build();

        var address2 = Address.builder()
                .id(address2Id)
                .address("Avenida Teste")
                .cep("87654321")
                .number("321")
                .state(state)
                .city(city)
                .active(false)
                .build();

        var address3 = Address.builder()
                .id(address3Id)
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
                .address(address)
                .address(address2)
                .address(address3)
                .mainAddress(address)
                .build();

        var edited = Address.builder()
                .id(addressId)
                .address("Rua Teste")
                .cep("12345678")
                .number("123")
                .state(state)
                .city(city)
                .active(false)
                .build();

        assertTrue(address.isActive());
        assertEquals(address, person.getMainAddress());
        assertFalse(address2.isActive());
        assertFalse(address3.isActive());

        when(findAddresses.findOne(addressId)).thenReturn(address);

        assertThrows(BusinessRuleException.class, () -> editAddress.edit(addressId, edited));

        verifyNoInteractions(addressRepository);

        assertTrue(address.isActive());
        assertEquals(address, person.getMainAddress());
        assertFalse(address2.isActive());
        assertFalse(address3.isActive());
    }

}