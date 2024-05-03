package com.github.thiagomarqs.gerenciamentopessoas.controller;

import com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.address.EditAddress;
import com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.address.FindAddresses;
import com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.person.AddAddress;
import com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.person.RemoveAddress;
import com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.person.SetMainAddress;
import com.github.thiagomarqs.gerenciamentopessoas.dto.address.CreateAddressRequest;
import com.github.thiagomarqs.gerenciamentopessoas.dto.address.EditAddressRequest;
import com.github.thiagomarqs.gerenciamentopessoas.dto.address.NewMainAddressRequest;
import com.github.thiagomarqs.gerenciamentopessoas.mapper.AddressMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/people")
@Tag(name = "Endereços", description = "Gerenciamento dos endereços das pessoas")
public class AddressController {

    private final SetMainAddress setMainAddress;
    private final RemoveAddress removeAddress;
    private final AddAddress addAddress;
    private final AddressMapper addressMapper;
    private final EditAddress editAddress;
    private final FindAddresses findAddresses;

    @Inject
    public AddressController(SetMainAddress setMainAddress, RemoveAddress removeAddress, AddAddress addAddress, AddressMapper addressMapper, EditAddress editAddress, FindAddresses findAddresses) {
        this.setMainAddress = setMainAddress;
        this.removeAddress = removeAddress;
        this.addAddress = addAddress;
        this.addressMapper = addressMapper;
        this.editAddress = editAddress;
        this.findAddresses = findAddresses;
    }

    @Operation(
            summary = "Listar endereços",
            description = "Lista todos os endereços da pessoa especificada. Opcionalmente, pode-se filtrar pelo atributo 'active'.",
            tags = {"Pessoa", "Endereço"}
    )
    @GetMapping("{personId}/addresses")
    public ResponseEntity<?> getAll(
            @RequestParam(value = "active", required = false) Boolean active,
            @PathVariable("personId") @NotNull Long personId
    ) {
        var addresses = active != null ? findAddresses.findAllByActive(personId, active) : findAddresses.findAll(personId);
        var response = addressMapper.addressListToAddressResponseList(addresses);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Buscar endereço",
            description = "Busca um endereço específico.",
            tags = {"Pessoa", "Endereço"}
    )
    @GetMapping("{personId}/addresses/{addressId}")
    public ResponseEntity<?> getAddress(@PathVariable("personId") @NotNull Long personId, @PathVariable("addressId") @NotNull Long addressId) {
        var address = findAddresses.findOne(addressId);
        var response = addressMapper.addressToAddressResponse(address);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Busca por nome",
            description = "Pesquisa todos os endereços de uma pessoa pelo nome do endereço.",
            tags = {"Pessoa", "Endereço"}
    )
    @GetMapping("{personId}/addresses/address/{address}")
    public ResponseEntity<?> getAllByAddressLike(@PathVariable("personId") @NotNull Long personId, @PathVariable("address") @NotBlank String address) {
        var addresses = findAddresses.findAllByAddressLike(personId, address);
        var response = addressMapper.addressListToAddressResponseList(addresses);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Editar endereço",
            description = "Edita o endereço especificado.",
            tags = {"Pessoa", "Endereço"}
    )
    @PatchMapping("{personId}/addresses/{addressId}")
    public ResponseEntity<?> editAddress(@PathVariable("personId") Long personId, @PathVariable("addressId") Long addressId, @RequestBody @Valid EditAddressRequest request) {
        var edited = addressMapper.editAddressRequestToAddress(request, addressId);
        var address = editAddress.edit(addressId, edited);
        var response = addressMapper.addressToAddressResponse(address);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Remover endereço",
            description = "Remove o endereço especificado.",
            tags = {"Pessoa", "Endereço"}
    )
    @DeleteMapping("{personId}/addresses/{addressId}")
    public ResponseEntity<?> deleteAddress(@PathVariable("personId") @NotNull Long personId, @PathVariable("addressId") @NotNull Long addressId) {
        removeAddress.remove(personId, addressId);
        return ResponseEntity.accepted().build();
    }

    @Operation(
            summary = "Adicionar endereço",
            description = "Adiciona um novo endereço à pessoa especificada",
            tags = {"Pessoa", "Endereço"}
    )
    @PostMapping("{id}/addresses")
    public ResponseEntity<?> addAddress(@PathVariable("id") @NotNull Long personId, @RequestBody @NotNull @Valid CreateAddressRequest request) {
        var address = addressMapper.createAddressRequestToAddress(request);
        var person = addAddress.add(personId, address);
        var addresses = person.getAddresses();
        var response = addressMapper.addressListToAddressResponseList(addresses);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Endereço principal",
            description = "Busca o endereço principal de uma pessoa.",
            tags = {"Pessoa", "Endereço"}
    )
    @GetMapping("{personId}/addresses/main")
    public ResponseEntity<?> getMainAddress(@PathVariable("personId") @NotNull Long personId) {
        var address = findAddresses.findMainAddress(personId);
        var response = addressMapper.addressToAddressResponse(address);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Endereço principal",
            description = "Define o endereço principal de uma pessoa.",
            tags = {"Pessoa", "Endereço"}
    )
    @PutMapping("{personId}/addresses/main")
    public ResponseEntity<?> setMainAddress(@PathVariable("personId") @NotNull Long personId, @RequestBody @NotNull @Valid NewMainAddressRequest request) {
        setMainAddress.set(personId, request.id());
        return ResponseEntity.accepted().build();
    }

}
