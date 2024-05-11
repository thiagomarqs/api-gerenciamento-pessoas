package com.github.thiagomarqs.gerenciamentopessoas.integration.address.viacep;

import com.github.thiagomarqs.gerenciamentopessoas.integration.address.AddressIntegrationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ViaCepAddressFinderTest {

    ViaCepAddressFinder viaCepAddressFinder;
    String viaCepEndpoint = "https://viacep.com.br/ws/";

    @BeforeEach
    void setUp() {
        viaCepAddressFinder = new ViaCepAddressFinder(viaCepEndpoint) ;
    }

    @Test
    void shouldReturnAddressResponseWhenCepIsValid() throws Exception {
        String cep = "01014-030";

        AddressIntegrationResult result = viaCepAddressFinder.findAddressByCep(cep);
        var response = result.getResponse();

        assertEquals("Viaduto Boa Vista", response.address());
        assertEquals("01014-030", response.cep());
        assertEquals("São Paulo", response.city());
        assertEquals("SP", response.state());
    }

    @Test
    void shouldFailWhenCepDoesNotExist() throws Exception {
        String cep = "99999-999";

        var result = viaCepAddressFinder.findAddressByCep(cep);

        assertFalse(result.getIsSuccessful());
        assertFalse(result.getErrorMessages().isEmpty());
    }

    @Test
    void shouldFailWhenCepPatternIsInvalid() throws Exception {
        String cep = "99999999";

        var result = viaCepAddressFinder.findAddressByCep(cep);

        assertFalse(result.getIsSuccessful());
        assertFalse(result.getErrorMessages().isEmpty());
    }
}