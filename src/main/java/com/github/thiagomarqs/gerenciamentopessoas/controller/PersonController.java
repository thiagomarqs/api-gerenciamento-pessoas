package com.github.thiagomarqs.gerenciamentopessoas.controller;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;
import com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.person.CreatePerson;
import com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.person.EditPerson;
import com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.person.FindPeople;
import com.github.thiagomarqs.gerenciamentopessoas.dto.person.EditPersonRequest;
import com.github.thiagomarqs.gerenciamentopessoas.dto.person.CreatePersonRequest;
import com.github.thiagomarqs.gerenciamentopessoas.dto.person.GetManyPeopleRequest;
import com.github.thiagomarqs.gerenciamentopessoas.mapper.PersonMapper;
import com.github.thiagomarqs.gerenciamentopessoas.validation.AddressValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/people")
@Tag(name = "Pessoas", description = "Gerenciamento de pessoas")
public class PersonController {

    private CreatePerson createPerson;
    private EditPerson editPerson;
    private FindPeople findPeople;
    private AddressValidator addressValidator;
    private PersonMapper personMapper;

    @Inject
    public PersonController(CreatePerson createPerson, EditPerson editPerson, FindPeople findPeople, AddressValidator addressValidator, PersonMapper personMapper) {
        this.createPerson = createPerson;
        this.editPerson = editPerson;
        this.findPeople = findPeople;
        this.addressValidator = addressValidator;
        this.personMapper = personMapper;
    }

    @Operation(
            summary = "Criar pessoa",
            description = "Cria uma pessoa a partir do corpo da requisição. Esta pessoa já deve possuir endereços cadastrados.",
            tags = { "Pessoa" }
    )
    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid CreatePersonRequest request) {

        var person = personMapper.createPersonRequestToPerson(request);
        var addresses = person.getAddresses();
        var addressValidationResult = addressValidator.validateMany(addresses);

        if(addressValidationResult.hasFailures()) {
            return ResponseEntity.badRequest().body(addressValidationResult);
        }

        var saved = createPerson.create(person);
        var response = personMapper.personToPersonResponse(saved);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Editar pessoa",
            description = "Edita os dados de uma pessoa. Se uma pessoa for desativada, todos seus endereços também serão. Adição/Edição/Remoção de endereços deve ser feita através de endpoints específicos.",
            tags = { "Pessoa" }
    )
    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") @NotNull Long personId, @RequestBody @NotNull @Valid EditPersonRequest request) {

        var person = personMapper.editPersonRequestToPerson(request);
        var edited = editPerson.edit(personId, person);
        var response = personMapper.personToEditedPersonResponse(edited);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Uma pessoa",
            description = "Obtém os dados de uma única pessoa.",
            tags = { "Pessoa" }
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable("id") Long personId) {
        var person = findPeople.findOne(personId);
        var response = personMapper.personToPersonResponse(person);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Todas as pessoas",
            description = "Obtém todas as pessoas. Opcionalmente, pode-se filtrar por pessoas ativas ou inativas OU pesquisar por IDs.",
            tags = { "Pessoa" }
    )
    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(value = "active", required = false) Boolean active,
            @RequestBody(required = false) @Valid GetManyPeopleRequest request
    ){
        List<Person> people;

        if(request != null && request.ids() != null) {
            people = findPeople.findMany(request.ids());
        }
        else if(active != null) {
            people = findPeople.findAllByActive(active);
        }
        else {
            people = findPeople.findAll();
        }

        var response = personMapper.personListToPersonResponseList(people);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Pessoas por nome completo",
            description = "Obtém pessoas pesquisando pelo nome completo.",
            tags = { "Pessoa" }
    )
    @GetMapping("/fullName/{fullName}")
    public ResponseEntity<?> getAllByFullNameLike(@PathVariable("fullName") @NotBlank String fullName){
        var people = findPeople.findAllByFullNameLike(fullName);
        var response = personMapper.personListToPersonResponseList(people);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Pessoas por cidade",
            description = "Obtém pessoas pesquisando pela cidade.",
            tags = { "Pessoa" }
    )
    @GetMapping("/city/{city}")
    public ResponseEntity<?> getAllByCity(@PathVariable("city") @NotBlank String city){
        var people = findPeople.findAllByAddressCity(city);
        var response = personMapper.personListToPersonResponseList(people);
        return ResponseEntity.ok(response);
    }

}