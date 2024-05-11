package com.github.thiagomarqs.gerenciamentopessoas.integration.address;

public record AddressIntegrationResponse(
        String address,
        String cep,
        String number,
        String city,
        String state,
        String uf
) {
}
