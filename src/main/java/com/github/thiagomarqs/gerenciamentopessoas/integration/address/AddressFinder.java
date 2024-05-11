package com.github.thiagomarqs.gerenciamentopessoas.integration.address;

public interface AddressFinder {

    AddressIntegrationResult findAddressByCep(String cep);

}
