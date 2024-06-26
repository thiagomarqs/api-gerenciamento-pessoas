package com.github.thiagomarqs.gerenciamentopessoas.controller;

import com.github.thiagomarqs.gerenciamentopessoas.controller.dto.address.request.AddAddressRequest;
import com.github.thiagomarqs.gerenciamentopessoas.controller.dto.address.request.EditAddressRequest;
import com.github.thiagomarqs.gerenciamentopessoas.controller.dto.address.request.NewMainAddressRequest;
import com.github.thiagomarqs.gerenciamentopessoas.controller.dto.address.response.AddressResponse;
import com.github.thiagomarqs.gerenciamentopessoas.controller.hateoas.links.AddressLinks;
import com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.address.AddAddress;
import com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.address.EditAddress;
import com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.address.FindAddresses;
import com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.address.RemoveAddress;
import com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.person.SetMainAddress;
import com.github.thiagomarqs.gerenciamentopessoas.mapper.AddressMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/people")
@Tag(name = "Endereço", description = "Gerenciamento dos endereços das pessoas")
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
            tags = {"Endereço"},
            parameters = {
                    @Parameter(name = "active", description = "Filtrar por endereços ativos ou inativos", example = "true"),
                    @Parameter(name = "personId", description = "ID da pessoa", required = true, example = "1")
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "Endereços encontrados e os hiperlinks.",
            content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = AddressResponse.class))
            )
    )
    @GetMapping("{personId}/addresses")
    public ResponseEntity<?> getPersonAddresses(
            @RequestParam(value = "active", required = false) Boolean active,
            @PathVariable("personId") @NotNull Long personId
    ) {
        var addresses = active != null ? findAddresses.findAllByActive(personId, active) : findAddresses.findAll(personId);
        var response = addressMapper.addressListToAddressResponseList(addresses);
        var model = CollectionModel.of(response, AddressLinks.getAddressCollectionLinks(personId));
        return ResponseEntity.ok(model);
    }



    @Operation(
            summary = "Buscar endereço",
            description = "Busca um endereço específico.",
            tags = {"Endereço"},
            parameters = {
                    @Parameter(name = "personId", description = "ID da pessoa", required = true, example = "1"),
                    @Parameter(name = "addressId", description = "ID do endereço", required = true, example = "1")
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "Endereço encontrado e os hiperlinks.",
            content = @Content(
                    schema = @Schema(implementation = AddressResponse.class)
            )
    )
    @GetMapping("{personId}/addresses/{addressId}")
    public ResponseEntity<?> getAddress(@PathVariable("personId") @NotNull Long personId, @PathVariable("addressId") @NotNull Long addressId) {
        var address = findAddresses.findOne(addressId);
        var response = addressMapper.addressToAddressResponse(address);
        var model = EntityModel.of(response, AddressLinks.getIndividualAddressLinks(personId, addressId));
        return ResponseEntity.ok(model);
    }



    @Operation(
            summary = "Busca por nome",
            description = "Busca todos os endereços de uma pessoa que contenham o endereço informado.",
            tags = {"Endereço"},
            parameters = {
                    @Parameter(name = "personId", description = "ID da pessoa", required = true, example = "1"),
                    @Parameter(
                            name = "address",
                            description = """
                                    Endereço a ser buscado.
                                    É uma operação que busca endereços que CONTENHAM o conteúdo pesquisado.
                                    Assim, se o endereço pesquisado for 'Rua', endereços como 'Rua 1' e 'Rua 2' serão retornados.
                                    """,
                            required = true,
                            example = "Rua"
                    )
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "Endereços encontrados e os hiperlinks.",
            content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = AddressResponse.class))
            )
    )
    @GetMapping("{personId}/addresses/address/{address}")
    public ResponseEntity<?> getAllByAddressLike(@PathVariable("personId") @NotNull Long personId, @PathVariable("address") @NotBlank String address) {
        var addresses = findAddresses.findAllByAddressLike(personId, address);
        var response = addressMapper.addressListToAddressResponseList(addresses);
        var model = CollectionModel.of(response, AddressLinks.getAddressCollectionLinks(personId));
        return ResponseEntity.ok(model);
    }



    @Operation(
            summary = "Editar endereço",
            description = "Edita o endereço especificado. " +
                    "Se o endereço editado for desativado e for o endereço principal, o sistema automaticamente define o endereço ativo restante como principal. " +
                    "Se houver mais de um endereço ativo restante, o sistema não define automaticamente um novo endereço principal e retorna erro. " +
                    "Não é possível alterar o 'isMain' de um endereço. Para alterar o endereço principal, utilize o endpoint PUT /api/people/{personId}/addresses/main.",
            tags = {"Endereço"},
            parameters = {
                    @Parameter(name = "personId", description = "ID da pessoa", required = true, example = "1"),
                    @Parameter(name = "addressId", description = "ID do endereço", required = true, example = "1")
            }
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Endereço editado.",
            content = @Content(
                    schema = @Schema(implementation = EditAddressRequest.class)
            )
    )
    @ApiResponse(
            responseCode = "200",
            description = "Endereço editado e os hiperlinks.",
            content = @Content(
                    schema = @Schema(implementation = AddressResponse.class)
            )
    )
    @PatchMapping("{personId}/addresses/{addressId}")
    public ResponseEntity<?> editAddress(@PathVariable("personId") Long personId, @PathVariable("addressId") Long addressId, @RequestBody @Valid EditAddressRequest request) {
        var edited = addressMapper.editAddressRequestToAddress(request, addressId);
        var address = editAddress.edit(addressId, edited);
        var response = addressMapper.addressToAddressResponse(address);
        var model = EntityModel.of(response, AddressLinks.getIndividualAddressLinks(personId, addressId));
        return ResponseEntity.ok(model);
    }



    @Operation(
            summary = "Remover endereço",
            description = "Remove o endereço especificado.",
            tags = {"Endereço"},
            parameters = {
                    @Parameter(name = "personId", description = "ID da pessoa", required = true, example = "1"),
                    @Parameter(name = "addressId", description = "ID do endereço", required = true, example = "1")
            }
    )
    @ApiResponse(responseCode = "204", description = "Endereço removido com sucesso.")
    @DeleteMapping("{personId}/addresses/{addressId}")
    public ResponseEntity<?> deleteAddress(@PathVariable("personId") @NotNull Long personId, @PathVariable("addressId") @NotNull Long addressId) {
        removeAddress.remove(personId, addressId);
        return ResponseEntity.noContent().build();
    }



    @Operation(
            summary = "Adicionar endereço",
            description = "Adiciona um novo endereço à pessoa especificada",
            tags = {"Endereço"},
            parameters = {
                    @Parameter(name = "personId", description = "ID da pessoa", required = true, example = "1")
            }
    )
    @ApiResponse(
            responseCode = "201",
            description = "Endereço adicionado e os hiperlinks.",
            content = @Content(
                    schema = @Schema(implementation = AddressResponse.class)
            )
    )
    @PostMapping("{personId}/addresses")
    public ResponseEntity<?> addAddress(@PathVariable("personId") @NotNull Long personId, @RequestBody @NotNull @Valid AddAddressRequest request) {
        var address = addressMapper.addAddressRequestToAddress(request);
        var savedAddress = addAddress.add(personId, address);
        var response = addressMapper.addressToAddressResponse(savedAddress);
        var model = EntityModel.of(response, AddressLinks.getIndividualAddressLinks(personId, address.getId()));
        return ResponseEntity.created(model.getRequiredLink("self").toUri()).body(model);
    }



    @Operation(
            summary = "Endereço principal",
            description = "Busca o endereço principal de uma pessoa.",
            tags = {"Endereço"},
            parameters = {
                    @Parameter(name = "personId", description = "ID da pessoa", required = true, example = "1")
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "Endereço principal encontrado e os hiperlinks.",
            content = @Content(
                    schema = @Schema(implementation = AddressResponse.class)
            )
    )
    @GetMapping("{personId}/addresses/main")
    public ResponseEntity<?> getMainAddress(@PathVariable("personId") @NotNull Long personId) {
        var address = findAddresses.findMainAddress(personId);
        var response = addressMapper.addressToAddressResponse(address);
        var addressId = address.getId();
        var model = EntityModel.of(response, AddressLinks.getIndividualAddressLinks(personId, addressId));
        return ResponseEntity.ok(model);
    }



    @Operation(
            summary = "Endereço principal",
            description = "Define o endereço principal de uma pessoa.",
            tags = {"Endereço"},
            parameters = {
                    @Parameter(name = "personId", description = "ID da pessoa", required = true, example = "1")
            }
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Novo endereço.",
            content = @Content(
                    schema = @Schema(implementation = NewMainAddressRequest.class)
            )
    )
    @ApiResponse(responseCode = "204", description = "Endereço principal definido com sucesso.")
    @PutMapping("{personId}/addresses/main")
    public ResponseEntity<?> setMainAddress(@PathVariable("personId") @NotNull Long personId, @RequestBody @NotNull @Valid NewMainAddressRequest request) {
        setMainAddress.set(personId, request.id());
        return ResponseEntity.noContent().build();
    }

}
