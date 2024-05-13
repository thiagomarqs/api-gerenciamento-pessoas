package com.github.thiagomarqs.gerenciamentopessoas.controller;

import com.github.thiagomarqs.gerenciamentopessoas.controller.dto.person.request.CreatePersonRequest;
import com.github.thiagomarqs.gerenciamentopessoas.controller.dto.person.request.EditPersonRequest;
import com.github.thiagomarqs.gerenciamentopessoas.controller.dto.person.response.EditedPersonResponse;
import com.github.thiagomarqs.gerenciamentopessoas.controller.dto.person.response.PersonResponse;
import com.github.thiagomarqs.gerenciamentopessoas.controller.hateoas.links.PersonLinks;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;
import com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.person.CreatePerson;
import com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.person.EditPerson;
import com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.person.FindPeople;
import com.github.thiagomarqs.gerenciamentopessoas.mapper.PersonMapper;
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

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/people")
@Tag(name = "Pessoa", description = "Gerenciamento de pessoas")
public class PersonController {

    private final CreatePerson createPerson;
    private final EditPerson editPerson;
    private final FindPeople findPeople;
    private final PersonMapper personMapper;

    @Inject
    public PersonController(CreatePerson createPerson, EditPerson editPerson, FindPeople findPeople, PersonMapper personMapper) {
        this.createPerson = createPerson;
        this.editPerson = editPerson;
        this.findPeople = findPeople;
        this.personMapper = personMapper;
    }

    @Operation(
            summary = "Criar pessoa",
            description = "Cria uma pessoa a partir do corpo da requisição. Esta pessoa já deve possuir endereços cadastrados e pode-se definir o endereço principal definindo o atributo 'main' de apenas um dos endereços como true",
            tags = { "Pessoa" }
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                    schema = @Schema(implementation = CreatePersonRequest.class)
            )
    )
    @ApiResponse(
            responseCode = "201",
            description = "Retorna a pessoa criada e seus hiperlinks.",
            content = @Content(
                    schema = @Schema(implementation = PersonResponse.class)
            )
    )
    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid CreatePersonRequest request) {
        var person = personMapper.createPersonRequestToPerson(request);
        var saved = createPerson.create(person);
        var response = personMapper.personToPersonResponse(saved);
        Long personId = response.id();
        var model = EntityModel.of(response, PersonLinks.individualPersonLinks(personId));

        URI newPersonUri = model.getRequiredLink("self").toUri();

        return ResponseEntity.created(newPersonUri).body(model);
    }



    @Operation(
            summary = "Editar pessoa",
            description = "Edita os dados de uma pessoa. Se uma pessoa for desativada, todos seus endereços também serão. Adição/Edição/Remoção de endereços deve ser feita através de endpoints específicos.",
            tags = { "Pessoa" }
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                    schema = @Schema(implementation = EditPersonRequest.class)
            )
    )
    @ApiResponse(
            responseCode = "201",
            description = "Retorna a pessoa editada e seus hiperlinks.",
            content = @Content(
                    schema = @Schema(implementation = EditedPersonResponse.class)
            )
    )
    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") @NotNull Long personId, @RequestBody @NotNull @Valid EditPersonRequest request) {

        var person = personMapper.editPersonRequestToPerson(request);
        var edited = editPerson.edit(personId, person);
        var response = personMapper.personToEditedPersonResponse(edited);
        var model = EntityModel.of(response, PersonLinks.individualPersonLinks(personId));

        return ResponseEntity.ok(model);
    }



    @Operation(
            summary = "Uma pessoa",
            description = "Obtém os dados de uma única pessoa.",
            tags = { "Pessoa" }
    )
    @Parameter(
            name = "id",
            description = "ID da pessoa",
            required = true,
            example = "1"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Retorna a pessoa e seus hiperlinks.",
            content = @Content(
                    schema = @Schema(implementation = PersonResponse.class)
            )
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable("id") Long personId) {
        var person = findPeople.findOne(personId);
        var response = personMapper.personToPersonResponse(person);
        var model = EntityModel.of(response, PersonLinks.individualPersonLinks(personId));

        return ResponseEntity.ok(model);
    }



    @Operation(
            summary = "Todas as pessoas",
            description = "Obtém todas as pessoas. Opcionalmente, pode-se filtrar por pessoas ativas ou inativas OU pesquisar por IDs.",
            tags = { "Pessoa" },
            parameters = {
                    @Parameter(name = "active", description = "Filtrar por pessoas ativas ou inativas", example = "true"),
                    @Parameter(name = "ids", description = "Pesquisar por IDs", example = "1,2,3")
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "Retorna todas as pessoas e seus hiperlinks.",
            content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = PersonResponse.class))
            )
    )
    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(value = "active", required = false) Boolean active,
            @RequestParam(value = "ids", required = false) String ids
    ){
        List<Person> people;
        List<Long> idsList = Arrays
                .stream(ids.trim().split(","))
                .map(Long::valueOf)
                .collect(Collectors.toList());

        if (!idsList.isEmpty()) {
            people = findPeople.findMany(idsList);
        }
        else if (active != null) {
            people = findPeople.findAllByActive(active);
        }
        else {
            people = findPeople.findAll();
        }

        var response = personMapper.personListToPersonResponseList(people);
        var model = CollectionModel.of(response, PersonLinks.personCollectionLinks());

        return ResponseEntity.ok(model);
    }



    @Operation(
            summary = "Pessoas por nome completo",
            description = "Busca de pessoas pelo nome completo desde que ele contenha o nome informado.",
            tags = { "Pessoa" }
    )
    @Parameter(
            name = "fullName",
            description = """
                    Nome da pessoa.
                    A pesquisa busca pessoas que CONTENHAM o nome ou parte do nome informado.
                    Desta forma, caso pesquisemos por 'Abe', os resultados irão incluir 'Abel' e 'Abelardo'.
            """,
            required = true,
            example = "Abelardo"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Retorna as pessoas e os hiperlinks.",
            content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = PersonResponse.class))
            )
    )
    @GetMapping("/fullName/{fullName}")
    public ResponseEntity<?> getAllByFullNameLike(@PathVariable("fullName") @NotBlank String fullName) {
        var people = findPeople.findAllByFullNameLike(fullName);

        if(people.isEmpty()) return ResponseEntity.notFound().build();

        var response = personMapper.personListToPersonResponseList(people);
        var model = CollectionModel.of(response, PersonLinks.personCollectionLinks());
        return ResponseEntity.ok(model);
    }



    @Operation(
            summary = "Pessoas por cidade",
            description = "Obtém pessoas pesquisando pela cidade.",
            tags = { "Pessoa" }
    )
    @Parameter(
            name = "city",
            description = "Cidade a ser buscada",
            required = true,
            example = "Não-Me-Toque"
    )
    @GetMapping("/city/{city}")
    public ResponseEntity<?> getAllByCity(@PathVariable("city") @NotBlank String city) {
        var people = findPeople.findAllByAddressCity(city);
        var response = personMapper.personListToPersonResponseList(people);
        var model = CollectionModel.of(response, PersonLinks.personCollectionLinks());
        return ResponseEntity.ok(model);
    }

}
