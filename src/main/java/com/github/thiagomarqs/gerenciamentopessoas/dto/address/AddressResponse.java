package com.github.thiagomarqs.gerenciamentopessoas.dto.address;

public record AddressResponse(
    Long id,
    String address,
    String cep,
    String number,
    String city,
    String state,
    boolean active
) { }
