package com.github.thiagomarqs.gerenciamentopessoas.controller.dto.address.response;

public record AddressResponse(
    Long id,
    String address,
    String cep,
    String number,
    String city,
    String state,
    boolean active,
    Boolean isMain
) { }
