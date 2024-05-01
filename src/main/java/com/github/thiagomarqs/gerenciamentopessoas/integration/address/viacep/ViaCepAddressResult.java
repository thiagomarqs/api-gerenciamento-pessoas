package com.github.thiagomarqs.gerenciamentopessoas.integration.address.viacep;

public record ViaCepAddressResult(
    String cep,
    String logradouro,
    String complemento,
    String bairro,
    String localidade,
    String uf,
    String ibge,
    String gia,
    String ddd,
    String siafi
) {}