package com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.person;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;
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
class EditPersonTest {

    @Mock
    PersonRepository personRepository;

    @Mock
    FindPeople findPeople;

    @InjectMocks
    EditPerson editPerson;

    String state = "Estado Teste";
    String city = "Teste";

    @Test
    void shouldEditPerson() {
        var personId = 1L;

        var address = Address.builder()
                .id(1L)
                .address("Rua Teste")
                .cep("12345678")
                .active(true)
                .city(city)
                .state(state)
                .build();

        var newAddress = Address.builder()
                .id(2L)
                .address("Avenida Teste")
                .cep("87654321")
                .active(true)
                .city(city)
                .state(state)
                .build();

        String oldFullName = "Fulano de Tal";
        LocalDate oldBirthDate = LocalDate.of(1990, 1, 1);

        var person = Person.builder()
                .id(personId)
                .fullName(oldFullName)
                .active(true)
                .birthDate(oldBirthDate)
                .address(address)
                .mainAddress(address)
                .build();

        String newFullName = "Fulano Beltrano de Tal";
        LocalDate newBirthDate = LocalDate.of(2000, 2, 2);

        // Address deletion or addition is done in another use case.
        // Here, we are forcing a new address to be sure that the use case rejects the change and keeps the original one.
        var edited = Person.builder()
                .id(personId)
                .fullName(newFullName)
                .active(true)
                .birthDate(newBirthDate)
                .address(newAddress)
                .mainAddress(newAddress)
                .build();

        when(findPeople.findOne(personId)).thenReturn(person);

        assertTrue(person.getAddresses().contains(address));
        assertEquals(person.getMainAddress(), address);
        assertEquals(address.getPerson(), person);
        assertEquals(oldFullName, person.getFullName());
        assertEquals(oldBirthDate, person.getBirthDate());

        editPerson.edit(personId, edited);

        assertTrue(person.getAddresses().contains(address));
        assertEquals(address, person.getMainAddress());
        assertEquals(person, address.getPerson());
        assertEquals(newFullName, person.getFullName());
        assertEquals(newBirthDate, person.getBirthDate());

    }

    @Test
    void shouldInactivateAllAddressesWhenInactivatingPerson() {

        var personId = 1L;

        var address = Address.builder()
                .id(1L)
                .address("Rua Teste 111")
                .cep("12345678")
                .active(true)
                .city(city)
                .state(state)
                .build();

        var address2 = Address.builder()
                .id(2L)
                .address("Avenida Teste 555")
                .cep("87654321")
                .active(true)
                .city(city)
                .state(state)
                .build();

        var person = Person.builder()
                .id(personId)
                .fullName("Fulano de Tal")
                .active(true)
                .birthDate(LocalDate.of(1990, 1, 1))
                .address(address)
                .mainAddress(address)
                .build();

        var edited = Person.builder()
                .id(personId)
                .fullName("Fulano de Tal")
                .active(false)
                .birthDate(LocalDate.of(1990, 1, 1))
                .address(address)
                .mainAddress(address)
                .build();

        when(findPeople.findOne(personId)).thenReturn(person);

        person.getAddresses().forEach(a -> {
            assertTrue(a.isActive());
            assertEquals(person, a.getPerson());
        });

        editPerson.edit(personId, edited);

        person.getAddresses().forEach(a -> {
            assertFalse(a.isActive());
            assertEquals(person, a.getPerson());
        });

    }

}