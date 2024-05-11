package com.github.thiagomarqs.gerenciamentopessoas.mapper;

import com.github.thiagomarqs.gerenciamentopessoas.controller.dto.address.request.AddAddressRequest;
import com.github.thiagomarqs.gerenciamentopessoas.controller.dto.address.request.CreatePersonAddressRequest;
import com.github.thiagomarqs.gerenciamentopessoas.controller.dto.address.request.EditAddressRequest;
import com.github.thiagomarqs.gerenciamentopessoas.controller.dto.address.response.AddressResponse;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import com.github.thiagomarqs.gerenciamentopessoas.integration.address.AddressIntegrationResponse;
import com.github.thiagomarqs.gerenciamentopessoas.integration.address.viacep.ViaCepAddressResponse;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper
public interface AddressMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "active", ignore = true)
    Address createAddressRequestToAddress(CreatePersonAddressRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "active", ignore = true)
    Address addAddressRequestToAddress(AddAddressRequest request);

    AddressResponse addressToAddressResponse(Address address);

    List<Address> createAddressRequestListToAddressList(List<CreatePersonAddressRequest> request);

    @Mapping(target = "cep", source = "cep")
    @Mapping(target = "address", source = "logradouro")
    @Mapping(target = "city", source = "localidade")
    @Mapping(target = "state", source = "uf" )
    @Mapping(target = "uf", source = "uf")
    @Mapping(target = "number", ignore = true)
    AddressIntegrationResponse viaCepAddressResponseToAddressIntegrationResponse(ViaCepAddressResponse viaCepAddressResponse);

    List<AddressResponse> addressListToAddressResponseList(List<Address> addresses);

    @Mapping(target = "id", source = "addressId")
    @Mapping(target = "person", ignore = true)
    Address editAddressRequestToAddress(EditAddressRequest request, Long addressId);

    @AfterMapping
    default void setIsMain(@MappingTarget Address address) {
        if(address.getIsMain() == null) {
            address.setIsMain(false);
        }
    }
}
