package integration.cucumber.stepdefinitions;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;
import com.github.thiagomarqs.gerenciamentopessoas.domain.repository.AddressRepository;
import com.github.thiagomarqs.gerenciamentopessoas.domain.repository.PersonRepository;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import integration.cucumber.client.WireMockClient;
import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static integration.cucumber.utils.PayloadUtils.readRequestPayload;
import static integration.cucumber.utils.PayloadUtils.readResponsePayload;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ManagePeopleSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private AddressRepository addressRepository;

    private static final WireMock wireMockClient = WireMockClient.getClient();
    private static final Gson gson = integration.cucumber.utils.Gson.get();

    private ResultActions resultActions;
    private Long personId;
    private Person person;

    @Before
    @Transactional
    public void cleanup() {
        if (personId != null) {
            personRepository.deleteById(personId);
        }

        addressRepository.deleteAll();
        resultActions = null;
        personId = null;
        person = null;
    }

    @Dado("que uma pessoa foi criada via API com o payload {string}")
    @Quando("é requisitada a criação de uma pessoa via API com o payload {string}")
    public void criarPessoa(String requestJsonFileName) throws Exception {
        String request = readRequestPayload(requestJsonFileName);

        resultActions = mockMvc.perform(
                post("/api/people")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                );

        personId = extractIdFromResponseBody();
    }

    @Dado("que a integração de endereços responde com o payload {string}")
    public void integracaoDeEnderecosRespondeComPayload(String responseFileNameJson) throws Exception {
        String response = readResponsePayload(responseFileNameJson);

        wireMockClient
                .register(get(urlPathTemplate("/{cep}/json"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(response))
        );

    }

    @Quando("é requisitada a desativação de uma pessoa com o payload {string}")
    public void requisitarDesativacaoDePessoa(String requestJsonFileName) throws Exception {
        String request = readRequestPayload(requestJsonFileName);

        resultActions = mockMvc.perform(
                patch("/api/people/" + personId)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
        );
    }

    @Entao("a pessoa é criada com sucesso")
    @Transactional
    public void pessoaCriadaComSucesso() throws Exception {
        resultActions.andExpect(status().isCreated());

        assertThat(personId).isNotNull();

        var createdPerson = personRepository.findById(personId);

        assertThat(createdPerson).isPresent();
    }

    @Entao("a pessoa não é criada")
    @Transactional
    public void pessoaNaoCriada() throws Exception {
        resultActions.andExpect(status().is4xxClientError());
    }

    @Entao("a pessoa e seus endereços são desativados")
    public void validarDesativacaoPessoaEnderecos() throws Exception {
        resultActions.andExpect(status().isOk());

        var editedPerson = personRepository.findById(personId);

        assertThat(editedPerson).isPresent();
        person = editedPerson.get();

        assertThat(person.isActive()).isFalse();

        List<Address> addresses = addressRepository.findAllByPersonId(personId);
        addresses.forEach(address -> assertThat(address.getActive()).isFalse());
    }

    private Long extractIdFromResponseBody() throws Exception {
        String responseJson = resultActions.andReturn().getResponse().getContentAsString();
        JsonObject jsonObject = gson.fromJson(responseJson, JsonObject.class);

        if (jsonObject != null && jsonObject.has("id")) {
            return jsonObject.get("id").getAsLong();
        }

        return null;
    }

}
