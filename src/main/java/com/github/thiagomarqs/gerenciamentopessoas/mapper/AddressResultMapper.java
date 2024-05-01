package com.github.thiagomarqs.gerenciamentopessoas.mapper;

import com.github.thiagomarqs.gerenciamentopessoas.integration.address.AddressResult;
import com.github.thiagomarqs.gerenciamentopessoas.integration.address.viacep.ViaCepAddressResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface AddressResultMapper {

    @Mapping(target = "cep", source = "cep")
    @Mapping(target = "address", source = "logradouro")
    @Mapping(target = "city", source = "localidade")
    @Mapping(target = "state", source = "uf" )
    @Mapping(target = "uf", source = "uf")
    @Mapping(target = "number", ignore = true)
    AddressResult viaCepAddressResultToAddressResult(ViaCepAddressResult viaCepAddressResult);

}
