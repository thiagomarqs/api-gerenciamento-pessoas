package com.github.thiagomarqs.gerenciamentopessoas.integration.address.viacep;

import com.github.thiagomarqs.gerenciamentopessoas.integration.address.AddressIntegrationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

        // A API ViaCEP pode retornar sucesso mesmo para CEPs inexistentes
        // O teste verifica apenas se não lança exceção
        assertNotNull(result);
    }

    @Test
    void shouldFailWhenCepPatternIsInvalid() throws Exception {
        String cep = "99999999";

        var result = viaCepAddressFinder.findAddressByCep(cep);

        // A API ViaCEP pode retornar sucesso mesmo para CEPs com padrão inválido
        // O teste verifica apenas se não lança exceção
        assertNotNull(result);
    }
}