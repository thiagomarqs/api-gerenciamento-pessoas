package com.github.thiagomarqs.gerenciamentopessoas.integration.address.viacep;

import com.github.thiagomarqs.gerenciamentopessoas.integration.address.AddressResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ViaCepAddressFinderTest {

    ViaCepAddressFinder viaCepAddressFinder;
    String viaCepEndpoint = "https://viacep.com.br/ws/";

    @BeforeEach
    void setUp() {
        viaCepAddressFinder = new ViaCepAddressFinder(viaCepEndpoint) ;
    }

    @Test
    void shouldReturnAddressResultWhenCepIsValid() throws Exception {
        String cep = "01014-030";

        AddressResult result = viaCepAddressFinder.findAddressByCep(cep);

        assertEquals("Viaduto Boa Vista", result.address());
        assertEquals("01014-030", result.cep());
        assertEquals("São Paulo", result.city());
        assertEquals("SP", result.state());
    }

    @Test
    void shouldThrowExceptionWhenCepIsInvalid() throws Exception {
        String cep = "99999-999";

        assertThrows(RuntimeException.class, () -> viaCepAddressFinder.findAddressByCep(cep));
    }
}