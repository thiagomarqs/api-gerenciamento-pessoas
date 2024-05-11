package com.github.thiagomarqs.gerenciamentopessoas.domain.validator.address;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import com.github.thiagomarqs.gerenciamentopessoas.integration.address.AddressFinder;
import com.github.thiagomarqs.gerenciamentopessoas.integration.address.AddressIntegrationResponse;
import com.github.thiagomarqs.gerenciamentopessoas.integration.address.AddressIntegrationResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressValidatorTest {

    @Mock
    AddressFinder addressFinder;

    @InjectMocks
    AddressValidator addressValidator;

    @Test
    void shouldNotHaveFailuresWhenValidationOfOneAddressIsSuccessful() throws Exception {

        String cep = "12345-678";
        var address = Address.builder().cep(cep).build();
        var response = new AddressIntegrationResponse("Rua", cep, "123", "cidade", "estado", "ES");
        var integrationResult = new AddressIntegrationResult(true, Collections.emptyList(), response);

        when(addressFinder.findAddressByCep(cep)).thenReturn(integrationResult);

        var validationResult = addressValidator.validateOneByCep(address);

        assertTrue(validationResult);
    }

    @Test
    void shouldNotHaveFailuresWhenValidationOfManyAddressesIsSuccessful() throws Exception {

        String cep = "12345-678";
        var address1 = Address.builder().cep(cep).build();
        var address2 = Address.builder().cep(cep).build();
        var addresses = List.of(address1, address2);
        var response = new AddressIntegrationResponse("Rua", cep, "123", "cidade", "estado", "ES");
        var integrationResult = new AddressIntegrationResult(true, Collections.emptyList(), response);

        when(addressFinder.findAddressByCep(cep)).thenReturn(integrationResult);

        var validationResult = addressValidator.validateManyByCep(addresses);
        var hasFailures = validationResult.hasFailures();

        assertFalse(hasFailures);
    }

    @Test
    void shouldFailWhenValidationOfOneAddressIsSuccessful() throws Exception {

        String cep = "12345678";
        var address = Address.builder().cep(cep).build();
        var integrationResult = new AddressIntegrationResult(false, List.of("Error"), null);

        when(addressFinder.findAddressByCep(cep)).thenReturn(integrationResult);

        var validationResult = addressValidator.validateOneByCep(address);

        assertFalse(validationResult);
    }

    @Test
    void shouldFailWhenValidationOfManyAddressesIsSuccessful() throws Exception {

        String cep = "12345678";
        var address1 = Address.builder().cep(cep).build();
        var address2 = Address.builder().cep(cep).build();
        var addresses = List.of(address1, address2);
        var integrationResult = new AddressIntegrationResult(false, List.of("Error"), null);

        when(addressFinder.findAddressByCep(cep)).thenReturn(integrationResult);

        var validationResult = addressValidator.validateManyByCep(addresses);
        var hasFailures = validationResult.hasFailures();
        var invalidCeps = validationResult.getInvalidCeps();

        assertTrue(hasFailures);
        assertFalse(invalidCeps.isEmpty());
    }

}