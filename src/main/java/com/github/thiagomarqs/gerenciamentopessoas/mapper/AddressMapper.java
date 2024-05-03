package com.github.thiagomarqs.gerenciamentopessoas.mapper;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import com.github.thiagomarqs.gerenciamentopessoas.dto.address.AddressResponse;
import com.github.thiagomarqs.gerenciamentopessoas.dto.address.CreateAddressRequest;
import com.github.thiagomarqs.gerenciamentopessoas.dto.address.EditAddressRequest;
import com.github.thiagomarqs.gerenciamentopessoas.integration.address.AddressResult;
import com.github.thiagomarqs.gerenciamentopessoas.integration.address.viacep.ViaCepAddressResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface AddressMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "active", ignore = true)
    Address createAddressRequestToAddress(CreateAddressRequest request);

    AddressResponse addressToAddressResponse(Address address);

    List<Address> createAddressRequestListToAddressList(List<CreateAddressRequest> request);

    @Mapping(target = "cep", source = "cep")
    @Mapping(target = "address", source = "logradouro")
    @Mapping(target = "city", source = "localidade")
    @Mapping(target = "state", source = "uf" )
    @Mapping(target = "uf", source = "uf")
    @Mapping(target = "number", ignore = true)
    AddressResult viaCepAddressResultToAddressResult(ViaCepAddressResult viaCepAddressResult);

    List<AddressResponse> addressListToAddressResponseList(List<Address> addresses);

    @Mapping(target = "id", source = "addressId")
    @Mapping(target = "person", ignore = true)
    Address editAddressRequestToAddress(EditAddressRequest request, Long addressId);
}
