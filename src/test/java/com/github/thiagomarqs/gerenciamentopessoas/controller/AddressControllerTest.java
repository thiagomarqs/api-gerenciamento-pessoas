package com.github.thiagomarqs.gerenciamentopessoas.controller;

import com.github.thiagomarqs.gerenciamentopessoas.config.gson.GsonConfig;
import com.github.thiagomarqs.gerenciamentopessoas.controller.dto.address.request.AddAddressRequest;
import com.github.thiagomarqs.gerenciamentopessoas.controller.dto.address.request.EditAddressRequest;
import com.github.thiagomarqs.gerenciamentopessoas.controller.dto.address.request.NewMainAddressRequest;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.address.AddAddress;
import com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.address.EditAddress;
import com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.address.FindAddresses;
import com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.address.RemoveAddress;
import com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.person.SetMainAddress;
import com.github.thiagomarqs.gerenciamentopessoas.mapper.AddressMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AddressController.class)
class AddressControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AddAddress addAddress;

    @MockBean
    EditAddress editAddress;

    @MockBean
    RemoveAddress removeAddress;

    @MockBean
    FindAddresses findAddresses;

    @MockBean
    SetMainAddress setMainAddress;

    @MockBean
    AddressMapper addressMapper;

    final String city = "Cidade Teste";
    final String state = "Estado Teste";

    Gson gson;

    @BeforeEach
    void setUp() {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, GsonConfig.getLocalDateTypeAdapter())
                .create();
    }

    @Test
    void shouldReturnStatus201WhenAddressIsAddedSuccessfully() throws Exception {
        var request = new AddAddressRequest("Rua Teste", "12345-678", "123", city, state);
        var address = Address.builder()
                .address("Rua Teste")
                .cep("12345-678")
                .number("123")
                .city(city)
                .state(state)
                .isMain(false)
                .build();
        var json = gson.toJson(request);

        when(addressMapper.addAddressRequestToAddress(request)).thenReturn(address);
        when(addAddress.add(1L, address)).thenReturn(address);
        when(addressMapper.addressToAddressResponse(address)).thenReturn(mock());

        mockMvc.perform(post("/api/people/1/addresses")
                .contentType("application/json")
                .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnStatus200WhenAddressIsEditedSuccessfully() throws Exception {
        var request = new EditAddressRequest("Rua Teste", "12345-678", "123", city, state, true);
        var address = Address.builder()
                .address("Rua Teste")
                .cep("12345-678")
                .number("123")
                .city(city)
                .state(state)
                .isMain(false)
                .build();
        var json = gson.toJson(request);

        when(addressMapper.editAddressRequestToAddress(request, 1L)).thenReturn(address);
        when(editAddress.edit(1L, address)).thenReturn(address);
        when(addressMapper.addressToAddressResponse(address)).thenReturn(mock());


        mockMvc.perform(patch("/api/people/1/addresses/1")
                .contentType("application/json")
                .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnStatus204WhenMainAddressIsSetSuccessfully() throws Exception {
        var request = new NewMainAddressRequest(1L);
        var json = gson.toJson(request);

        mockMvc.perform(put("/api/people/1/addresses/main")
                .contentType("application/json")
                .content(json))
                .andExpect(status().isNoContent());
    }

}