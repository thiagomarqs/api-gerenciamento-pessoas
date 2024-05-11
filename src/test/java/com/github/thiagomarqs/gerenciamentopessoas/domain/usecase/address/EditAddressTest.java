package com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.address;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.BusinessRuleException;
import com.github.thiagomarqs.gerenciamentopessoas.domain.repository.AddressRepository;
import com.github.thiagomarqs.gerenciamentopessoas.domain.validator.ValidationResult;
import com.github.thiagomarqs.gerenciamentopessoas.domain.validator.usecase.address.EditAddressBusinessRuleValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EditAddressTest {

    private final String city = "Cidade Teste";
    private final String state = "Estado Teste";

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private FindAddresses findAddresses;

    @Mock
    private EditAddressBusinessRuleValidator businessRuleValidator;

    @InjectMocks
    private EditAddress editAddress;

    @Test
    void shouldEditAddressWhenValidationPasses() {
        var address = Address.builder()
                .id(1L)
                .address("Rua Teste")
                .cep("12345678")
                .number("999")
                .city(city)
                .state(state)
                .build();

        var edited = Address.builder()
                .id(1L)
                .address("Rua Editada")
                .cep("87654321")
                .number("111")
                .city("Cidade Editada")
                .state("Estado Editado")
                .build();

        var person = Person.builder()
                .id(1L)
                .fullName("Fulano de Tal")
                .address(address)
                .address(edited)
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        when(findAddresses.findOne(1L)).thenReturn(address);
        when(businessRuleValidator.validate(edited)).thenReturn(new ValidationResult());
        when(addressRepository.save(edited)).thenReturn(edited);

        editAddress.edit(1L, edited);

        verify(addressRepository).save(edited);
    }

    @Test
    void shouldNotEditAddressWhenValidationFails() {
        var address = Address.builder()
                .id(1L)
                .address("Rua Teste")
                .cep("12345678")
                .number("999")
                .city(city)
                .state(state)
                .build();

        var edited = Address.builder()
                .id(1L)
                .address("Rua Editada")
                .cep("87654321")
                .number("111")
                .city("Cidade Editada")
                .state("Estado Editado")
                .build();

        var person = Person.builder()
                .id(1L)
                .fullName("Fulano de Tal")
                .address(address)
                .address(edited)
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        ValidationResult validationResult = new ValidationResult();
        validationResult.addError("error");

        when(findAddresses.findOne(1L)).thenReturn(address);
        when(businessRuleValidator.validate(edited)).thenReturn(validationResult);

        assertThrows(BusinessRuleException.class, () -> editAddress.edit(1L, edited));

        verifyNoInteractions(addressRepository);
    }

    @Test
    void shouldAutomaticallyChangeMainAddressWhenDeactivatingCurrentMainAddress() {
        var mainAddress = Address.builder()
                .id(1L)
                .address("Rua teste")
                .cep("12345678")
                .number("999")
                .city(city)
                .state(state)
                .active(true)
                .isMain(true)
                .build();

        var secondaryAddress = Address.builder()
                .id(2L)
                .address("Avenida Teste")
                .cep("87654321")
                .number("111")
                .city(city)
                .state(state)
                .active(true)
                .isMain(false)
                .build();

        var person = Person.builder()
                .id(1L)
                .fullName("Fulano de Tal")
                .address(mainAddress)
                .address(secondaryAddress)
                .mainAddress(mainAddress)
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        mainAddress.setPerson(person);
        secondaryAddress.setPerson(person);

        var editedMainAddress = Address.builder()
                .id(1L)
                .address("Main Address")
                .cep("12345678")
                .number("999")
                .city(city)
                .state(state)
                .active(false)
                .isMain(true)
                .build();

        when(findAddresses.findOne(1L)).thenReturn(mainAddress);
        when(businessRuleValidator.validate(editedMainAddress)).thenReturn(new ValidationResult());
        when(addressRepository.save(mainAddress)).thenReturn(mainAddress);

        editAddress.edit(1L, editedMainAddress);

        verify(addressRepository).save(mainAddress);
        assertEquals(secondaryAddress, person.getMainAddress());
    }
}