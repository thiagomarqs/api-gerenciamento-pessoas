package com.github.thiagomarqs.gerenciamentopessoas.controller;

import com.github.thiagomarqs.gerenciamentopessoas.config.gson.GsonConfig;
import com.github.thiagomarqs.gerenciamentopessoas.controller.dto.address.request.CreatePersonAddressRequest;
import com.github.thiagomarqs.gerenciamentopessoas.controller.dto.address.response.AddressResponse;
import com.github.thiagomarqs.gerenciamentopessoas.controller.dto.person.request.CreatePersonRequest;
import com.github.thiagomarqs.gerenciamentopessoas.controller.dto.person.request.EditPersonRequest;
import com.github.thiagomarqs.gerenciamentopessoas.controller.dto.person.response.EditedPersonResponse;
import com.github.thiagomarqs.gerenciamentopessoas.controller.dto.person.response.PersonResponse;
import com.github.thiagomarqs.gerenciamentopessoas.controller.dto.professionaldata.request.ProfessionalDataRequest;
import com.github.thiagomarqs.gerenciamentopessoas.controller.dto.professionaldata.request.UpdateProfessionalDataRequest;
import com.github.thiagomarqs.gerenciamentopessoas.controller.dto.professionaldata.response.ProfessionalDataResponse;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.ContractType;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.BusinessRuleException;
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
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;
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
        var request = new CreatePersonRequest(fullName, birthDate, addresses, null);
        var json = gson.toJson(request);

        when(personMapper.createPersonRequestToPerson(request)).thenReturn(person);
        when(createPerson.create(person)).thenReturn(person);
        when(personMapper.personToPersonResponse(person)).thenReturn(new PersonResponse(1L, fullName, birthDate.toString(), mock(AddressResponse.class), Collections.emptyList(), true, null));

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

        var request = new EditPersonRequest(fullName, birthDate, false, null);
        var json = gson.toJson(request);

        when(personMapper.editPersonRequestToPerson(request)).thenReturn(person);
        when(editPerson.edit(personId, person)).thenReturn(person);
        when(personMapper.personToEditedPersonResponse(person)).thenReturn(new EditedPersonResponse(personId, fullName, birthDate.toString(), false));

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

    @Test
    void shouldReturnStatusCode200AndEmptyListWhenGetAllIsCalledWithIdsButNoPersonIsFound() throws Exception {
        var ids = Arrays.asList(1L, 2L, 3L);

        when(findPeople.findMany(ids)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/people?ids=1,2,3"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnStatusCode400WhenBusinessRuleException() throws Exception {

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
        var request = new CreatePersonRequest(fullName, birthDate, addresses, null);
        var json = gson.toJson(request);

        when(personMapper.createPersonRequestToPerson(request)).thenReturn(person);
        when(createPerson.create(person)).thenThrow(BusinessRuleException.class);

        mockMvc.perform(post("/api/people")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(createPerson).create(person);
    }

    @Test
    void shouldReturnStatusCode201WhenPersonIsCreatedWithProfessionalData() throws Exception {
        String street = "Rua Teste";
        String cep = "12345678";
        String number = "999";
        String fullName = "Fulano de Tal";
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        String companyName = "Empresa Teste";
        ContractType contractType = ContractType.CLT;
        LocalDate employmentStartDate = LocalDate.of(2020, 1, 1);

        var address = Address.builder()
                .address(street)
                .cep(cep)
                .number(number)
                .city(city)
                .state(state)
                .isMain(true)
                .active(true)
                .build();

        var person = Person.builder()
                .id(1L)
                .fullName(fullName)
                .birthDate(birthDate)
                .address(address)
                .mainAddress(address)
                .build();

        var addresses = Collections.singletonList(new CreatePersonAddressRequest(street, cep, number, city, state, true));
        var professionalData = new ProfessionalDataRequest(companyName, contractType, employmentStartDate);
        var request = new CreatePersonRequest(fullName, birthDate, addresses, professionalData);
        var json = gson.toJson(request);

        when(personMapper.createPersonRequestToPerson(request)).thenReturn(person);
        when(createPerson.create(person)).thenReturn(person);
        when(personMapper.personToPersonResponse(person)).thenReturn(new PersonResponse(1L, fullName, birthDate.toString(), mock(AddressResponse.class), Collections.emptyList(), true, mock(ProfessionalDataResponse.class)));

        mockMvc.perform(post("/api/people")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnStatusCode201WhenPersonIsCreatedWithoutProfessionalData() throws Exception {
        String street = "Rua Teste";
        String cep = "12345678";
        String number = "999";
        String fullName = "Fulano de Tal";
        LocalDate birthDate = LocalDate.of(1990, 1, 1);

        var address = Address.builder()
                .address(street)
                .cep(cep)
                .number(number)
                .city(city)
                .state(state)
                .isMain(true)
                .active(true)
                .build();

        var person = Person.builder()
                .id(1L)
                .fullName(fullName)
                .birthDate(birthDate)
                .address(address)
                .mainAddress(address)
                .build();

        var addresses = Collections.singletonList(new CreatePersonAddressRequest(street, cep, number, city, state, true));
        var request = new CreatePersonRequest(fullName, birthDate, addresses, null);
        var json = gson.toJson(request);

        when(personMapper.createPersonRequestToPerson(request)).thenReturn(person);
        when(createPerson.create(person)).thenReturn(person);
        when(personMapper.personToPersonResponse(person)).thenReturn(new PersonResponse(1L, fullName, birthDate.toString(), mock(AddressResponse.class), Collections.emptyList(), true, null));

        mockMvc.perform(post("/api/people")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnStatusCode200WhenPersonProfessionalDataIsUpdated() throws Exception {
        String fullName = "Fulano de Tal";
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        Long personId = 1L;
        String companyName = "Nova Empresa";
        ContractType contractType = ContractType.PJ;
        LocalDate employmentStartDate = LocalDate.of(2021, 1, 1);

        var person = Person.builder()
                .id(personId)
                .fullName(fullName)
                .birthDate(birthDate)
                .active(true)
                .build();

        var professionalData = new UpdateProfessionalDataRequest(companyName, contractType, employmentStartDate);
        var request = new EditPersonRequest(fullName, birthDate, true, professionalData);
        var json = gson.toJson(request);

        when(personMapper.editPersonRequestToPerson(request)).thenReturn(person);
        when(editPerson.edit(personId, person)).thenReturn(person);
        when(personMapper.personToEditedPersonResponse(person)).thenReturn(new EditedPersonResponse(personId, fullName, birthDate.toString(), true));

        mockMvc.perform(patch("/api/people/" + personId)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnStatusCode200WhenPersonProfessionalDataIsRemoved() throws Exception {
        String fullName = "Fulano de Tal";
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        Long personId = 1L;

        var person = Person.builder()
                .id(personId)
                .fullName(fullName)
                .birthDate(birthDate)
                .active(true)
                .build();

        var request = new EditPersonRequest(fullName, birthDate, true, null);
        var json = gson.toJson(request);

        when(personMapper.editPersonRequestToPerson(request)).thenReturn(person);
        when(editPerson.edit(personId, person)).thenReturn(person);
        when(personMapper.personToEditedPersonResponse(person)).thenReturn(new EditedPersonResponse(personId, fullName, birthDate.toString(), true));

        mockMvc.perform(patch("/api/people/" + personId)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnStatusCode200WhenPersonProfessionalDataIsMaintained() throws Exception {
        String fullName = "Fulano de Tal";
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        Long personId = 1L;

        var person = Person.builder()
                .id(personId)
                .fullName(fullName)
                .birthDate(birthDate)
                .active(true)
                .build();

        var request = new EditPersonRequest(fullName, birthDate, true, null);
        var json = gson.toJson(request);

        when(personMapper.editPersonRequestToPerson(request)).thenReturn(person);
        when(editPerson.edit(personId, person)).thenReturn(person);
        when(personMapper.personToEditedPersonResponse(person)).thenReturn(new EditedPersonResponse(personId, fullName, birthDate.toString(), true));

        mockMvc.perform(patch("/api/people/" + personId)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnStatusCode200WhenGetPersonByIdWithProfessionalData() throws Exception {
        Long personId = 1L;
        String fullName = "Fulano de Tal";
        LocalDate birthDate = LocalDate.of(1990, 1, 1);

        var person = Person.builder()
                .id(personId)
                .fullName(fullName)
                .birthDate(birthDate)
                .active(true)
                .build();

        when(findPeople.findOne(personId)).thenReturn(person);
        when(personMapper.personToPersonResponse(person)).thenReturn(new PersonResponse(personId, fullName, birthDate.toString(), mock(AddressResponse.class), Collections.emptyList(), true, mock(ProfessionalDataResponse.class)));

        mockMvc.perform(get("/api/people/" + personId))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnStatusCode200WhenGetPersonByIdWithoutProfessionalData() throws Exception {
        Long personId = 1L;
        String fullName = "Fulano de Tal";
        LocalDate birthDate = LocalDate.of(1990, 1, 1);

        var person = Person.builder()
                .id(personId)
                .fullName(fullName)
                .birthDate(birthDate)
                .active(true)
                .build();

        when(findPeople.findOne(personId)).thenReturn(person);
        when(personMapper.personToPersonResponse(person)).thenReturn(new PersonResponse(personId, fullName, birthDate.toString(), mock(AddressResponse.class), Collections.emptyList(), true, null));

        mockMvc.perform(get("/api/people/" + personId))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnStatusCode200WhenGetAllPeopleWithProfessionalData() throws Exception {
        String fullName = "Fulano de Tal";
        LocalDate birthDate = LocalDate.of(1990, 1, 1);

        var person = Person.builder()
                .id(1L)
                .fullName(fullName)
                .birthDate(birthDate)
                .active(true)
                .build();

        when(findPeople.findAll()).thenReturn(Collections.singletonList(person));
        when(personMapper.personToPersonResponse(person)).thenReturn(new PersonResponse(1L, fullName, birthDate.toString(), mock(AddressResponse.class), Collections.emptyList(), true, mock(ProfessionalDataResponse.class)));

        mockMvc.perform(get("/api/people"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnStatusCode400WhenPatchWithInvalidContractType() throws Exception {
        String fullName = "Fulano de Tal";
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        Long personId = 1L;
        String companyName = "Empresa Teste";
        LocalDate employmentStartDate = LocalDate.of(2020, 1, 1);

        var person = Person.builder()
                .id(personId)
                .fullName(fullName)
                .birthDate(birthDate)
                .active(true)
                .build();

        var professionalData = new UpdateProfessionalDataRequest(companyName, null, employmentStartDate);
        var request = new EditPersonRequest(fullName, birthDate, true, professionalData);
        var json = gson.toJson(request);

        when(personMapper.editPersonRequestToPerson(request)).thenReturn(person);
        when(editPerson.edit(personId, person)).thenThrow(BusinessRuleException.class);

        mockMvc.perform(patch("/api/people/" + personId)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnStatusCode400WhenPatchWithFutureDate() throws Exception {
        String fullName = "Fulano de Tal";
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        Long personId = 1L;
        String companyName = "Empresa Teste";
        ContractType contractType = ContractType.CLT;
        LocalDate employmentStartDate = LocalDate.now().plusDays(1);

        var person = Person.builder()
                .id(personId)
                .fullName(fullName)
                .birthDate(birthDate)
                .active(true)
                .build();

        var professionalData = new UpdateProfessionalDataRequest(companyName, contractType, employmentStartDate);
        var request = new EditPersonRequest(fullName, birthDate, true, professionalData);
        var json = gson.toJson(request);

        when(personMapper.editPersonRequestToPerson(request)).thenReturn(person);
        when(editPerson.edit(personId, person)).thenThrow(BusinessRuleException.class);

        mockMvc.perform(patch("/api/people/" + personId)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest());
    }

}