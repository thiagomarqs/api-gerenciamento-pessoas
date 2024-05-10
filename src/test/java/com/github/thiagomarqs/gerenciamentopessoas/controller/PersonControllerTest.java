package com.github.thiagomarqs.gerenciamentopessoas.controller;

import com.github.thiagomarqs.gerenciamentopessoas.config.gson.GsonConfig;
import com.github.thiagomarqs.gerenciamentopessoas.controller.dto.address.request.CreatePersonAddressRequest;
import com.github.thiagomarqs.gerenciamentopessoas.controller.dto.person.request.CreatePersonRequest;
import com.github.thiagomarqs.gerenciamentopessoas.controller.dto.person.request.EditPersonRequest;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.EntityNotFoundException;
import com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.person.CreatePerson;
import com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.person.EditPerson;
import com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.person.FindPeople;
import com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.person.SetMainAddress;
import com.github.thiagomarqs.gerenciamentopessoas.mapper.PersonMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PersonController.class)
class PersonControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CreatePerson createPerson;

    @MockBean
    EditPerson editPerson;

    @MockBean
    SetMainAddress setMainAddress;

    @MockBean
    FindPeople findPeople;

    @MockBean
    PersonMapper personMapper;

    Gson gson;

    final String city = "Cidade Teste";
    final String state = "Estado Teste";

    @BeforeEach
    void setUp() {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, GsonConfig.getLocalDateTypeAdapter())
                .create();
    }

    @Test
    void shouldReturnStatusCode201WhenPersonIsCreatedSuccessfully() throws Exception {

        String street = "Rua Teste";
        String cep = "12345678";
        String number = "999";

        var address = Address.builder()
                .address(street)
                .cep(cep)
                .number(number)
                .city(city)
                .state(state)
                .isMain(true)
                .active(true)
                .build();

        String fullName = "Fulano de Tal";
        LocalDate birthDate = LocalDate.of(1990, 1, 1);

        var person = Person.builder()
                .id(1L)
                .fullName(fullName)
                .birthDate(birthDate)
                .address(address)
                .mainAddress(address)
                .build();

        var addresses = Collections.singletonList(new CreatePersonAddressRequest(street, cep, number, city, state, true));
        var request = new CreatePersonRequest(fullName, birthDate, addresses);
        var json = gson.toJson(request);

        when(personMapper.createPersonRequestToPerson(request)).thenReturn(person);
        when(createPerson.create(person)).thenReturn(person);
        when(personMapper.personToPersonResponse(person)).thenReturn(mock());

        mockMvc.perform(post("/api/people")
                .contentType("application/json")
                .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnStatusCode200WhenPersonIsEditedSuccessfully() throws Exception {

        String fullName = "Fulano de Tal";
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        Long personId = 1L;

        var person = Person.builder()
                .id(personId)
                .fullName(fullName)
                .birthDate(birthDate)
                .active(false)
                .build();

        var request = new EditPersonRequest(fullName, birthDate, false);
        var json = gson.toJson(request);

        when(personMapper.editPersonRequestToPerson(request)).thenReturn(person);
        when(editPerson.edit(personId, person)).thenReturn(person);
        when(personMapper.personToEditedPersonResponse(person)).thenReturn(mock());

        mockMvc.perform(patch("/api/people/" + personId)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnStatusCode404WhenPersonIsNotFound() throws Exception {
        Long personId = 1L;

        when(findPeople.findOne(personId)).thenThrow(EntityNotFoundException.of(personId, Person.class));

        mockMvc.perform(get("/api/people/" + personId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnStatusCode500WhenGetAllThrowsException() throws Exception {
        when(findPeople.findAll()).thenThrow(new RuntimeException());

        mockMvc.perform(get("/api/people"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldReturnStatusCode500WhenGetAllByFullNameLikeThrowsException() throws Exception {
        String fullName = "Fulano de Tal";

        when(findPeople.findAllByFullNameLike(fullName)).thenThrow(new RuntimeException());

        mockMvc.perform(get("/api/people/fullName/" + fullName))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldReturnStatusCode500WhenGetAllByCityThrowsException() throws Exception {
        String city = "Test City";

        when(findPeople.findAllByAddressCity(city)).thenThrow(new RuntimeException());

        mockMvc.perform(get("/api/people/city/" + city))
                .andExpect(status().isInternalServerError());
    }


}