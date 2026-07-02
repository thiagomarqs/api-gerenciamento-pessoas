package integration.junit;

import com.github.thiagomarqs.gerenciamentopessoas.Application;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;
import com.github.thiagomarqs.gerenciamentopessoas.domain.repository.AddressRepository;
import com.github.thiagomarqs.gerenciamentopessoas.domain.repository.PersonRepository;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import integration.junit.config.SpringConfiguration;
import integration.junit.config.TestContainersConfiguration;
import integration.junit.config.WireMockConfiguration;
import integration.junit.utils.GsonClient;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathTemplate;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static integration.junit.utils.PayloadUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {
                Application.class,
                TestContainersConfiguration.class,
                WireMockConfiguration.class,
                SpringConfiguration.class
        }
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ManagePeopleTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private WireMock wireMock;

    Gson gson = GsonClient.get();

    ResultActions resultActions;
    Long personId;
    Person person;

    @BeforeEach
    @Transactional
    public void cleanup() {
        if (personId != null) {
            personRepository.deleteById(personId);
        }

        resultActions = null;
        personId = null;
        person = null;
    }

    @BeforeEach
    public void setupDefaultStubs() throws IOException {
        String viaCepDefaultResponse = readWireMockResponsePayload("viacep-default-response-mock.json");
        wireMock.register(get(urlPathTemplate("/{cep}/json"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(viaCepDefaultResponse))
        );
    }

    @Test
    @DisplayName("""
            Quando um payload válido é recebido para criação de pessoa
            Então a pessoa é criada com sucesso
            """)
    void criarPessoaComSucesso() throws Exception {
        enviarRequisicaoParaCriarPessoa("create-person-success.json");
        verificarPessoaFoiCriadaComSucesso();
    }

    @Test
    @DisplayName("""
            Dado que uma pessoa já foi criada pela API
            Quando é requisitada a desativação de uma pessoa
            Então a pessoa e seus endereços são desativados
            """)
    void desativarPessoaETodosSeusEnderecosComSucesso()  throws Exception {
        enviarRequisicaoParaCriarPessoa("create-person-success.json");
        enviarRequisicaoParaEditarPessoa("deactivate-person-success.json");
        verificarPessoaDesativadaComSucesso();
    }

    @Test
    @DisplayName("""
            Dado que a integração de endereços retorna erro na validação do endereço
            Quando é requisitada a criação de uma pessoa pela API
            Então a pessoa não é criada
            """)
    void naoCriaPessoaSeFalharNaValidacaoDeIntegracaoDeEnderecos() throws Exception {
        integracaoDeEnderecosRespondeComPayload("invalid-address-response.json");
        enviarRequisicaoParaCriarPessoa("create-person-fails-address-validation.json");
        verificarPessoaNaoFoiCriada();
    }

    private void integracaoDeEnderecosRespondeComPayload(String responsePayloadFileName) throws IOException {
        String response = readResponsePayload(responsePayloadFileName);

        wireMock
                .register(get(urlPathTemplate("/{cep}/json"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(response))
                );
    }

    private void verificarPessoaNaoFoiCriada() throws Exception {
        resultActions.andExpect(status().is4xxClientError());
    }

    private void enviarRequisicaoParaCriarPessoa(String payloadJsonFileName) throws Exception {
        String request = readRequestPayload(payloadJsonFileName);

        resultActions = mockMvc.perform(
                post("/api/people")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        personId = extractIdFromResponseBody();
    }

    private void enviarRequisicaoParaEditarPessoa(String payloadJsonFileName) throws Exception {
        String request = readRequestPayload(payloadJsonFileName);

        resultActions = mockMvc.perform(
                patch("/api/people/" + personId)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
        );
    }

    private void verificarPessoaDesativadaComSucesso() throws Exception {
        resultActions.andExpect(status().isOk());

        var editedPerson = personRepository.findById(personId);

        assertThat(editedPerson).isPresent();
        person = editedPerson.get();

        assertThat(person.isActive()).isFalse();

        List<Address> addresses = addressRepository.findAllByPersonId(personId);
        addresses.forEach(address -> assertThat(address.getActive()).isFalse());
    }

    private void verificarPessoaFoiCriadaComSucesso() throws Exception {
        resultActions.andExpect(status().isCreated());
        assertThat(personId).isNotNull();
        var createdPerson = personRepository.findById(personId);
        assertThat(createdPerson).isPresent();
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
