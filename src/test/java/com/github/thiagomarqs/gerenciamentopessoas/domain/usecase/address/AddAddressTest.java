package com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.address;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.BusinessRuleException;
import com.github.thiagomarqs.gerenciamentopessoas.domain.repository.AddressRepository;
import com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.person.FindPeople;
import com.github.thiagomarqs.gerenciamentopessoas.domain.validator.ValidationResult;
import com.github.thiagomarqs.gerenciamentopessoas.domain.validator.usecase.address.AddAddressBusinessRuleValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddAddressTest {

    private final String city = "Cidade Teste";
    private final String state = "Estado Teste";
    @Mock
    private AddressRepository addressRepository;

    @Mock
    private FindPeople findPeople;

    @Mock
    private AddAddressBusinessRuleValidator businessRuleValidator;

    @InjectMocks
    private AddAddress addAddress;

    @Test
    void shouldAddAddressWhenValidationPasses() {
        var address = Address.builder()
                .address("Avenida Teste")
                .cep("12345678")
                .number("999")
                .city(city)
                .state(state)
                .active(true)
                .isMain(true)
                .build();

        var newAddress = Address.builder()
                .address("Rua Teste")
                .cep("12345678")
                .number("999")
                .city(city)
                .state(state)
                .active(true)
                .isMain(false)
                .build();

        var person = Person.builder()
                .id(1L)
                .fullName("Fulano de Tal")
                .address(address)
                .mainAddress(address)
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        when(findPeople.findOne(1L)).thenReturn(person);
        when(businessRuleValidator.validate(newAddress)).thenReturn(new ValidationResult());
        when(addressRepository.save(newAddress)).thenReturn(newAddress);

        addAddress.add(1L, newAddress);

        verify(addressRepository).save(newAddress);
    }

    @Test
    void shouldNotAddAddressWhenValidationFails() {
        var address = Address.builder()
                .address("Avenida Teste")
                .cep("12345678")
                .number("999")
                .city(city)
                .state(state)
                .active(true)
                .isMain(true)
                .build();

        var newAddress = Address.builder()
                .address("Rua Teste")
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
                .address(address)
                .mainAddress(address)
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        ValidationResult validationResult = new ValidationResult();
        validationResult.addError("error");

        when(findPeople.findOne(1L)).thenReturn(person);
        when(businessRuleValidator.validate(newAddress)).thenReturn(validationResult);

        assertThrows(BusinessRuleException.class, () -> addAddress.add(1L, newAddress));

        verifyNoInteractions(addressRepository);
    }
}