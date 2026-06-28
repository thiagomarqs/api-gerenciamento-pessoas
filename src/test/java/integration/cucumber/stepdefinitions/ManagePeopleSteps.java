package integration.cucumber.stepdefinitions;

import com.github.thiagomarqs.gerenciamentopessoas.controller.dto.person.request.EditPersonRequest;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;
import com.github.thiagomarqs.gerenciamentopessoas.domain.repository.AddressRepository;
import com.github.thiagomarqs.gerenciamentopessoas.domain.repository.PersonRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import integration.cucumber.utils.MappingUtils;
import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static integration.cucumber.fixture.PersonFixture.activePerson;
import static integration.cucumber.fixture.PersonFixture.createPersonRequest;
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

    @Quando("é requisitada a criação de uma pessoa")
    public void requisitadaACriacaoDeUmaPessoa() throws Exception {
        resultActions = mockMvc.perform(
                post("/api/people")
                        .content(createPersonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                );

        resultActions.andExpect(status().isCreated());
        personId = extractIdFromResponseBody();
    }

    private Long extractIdFromResponseBody() throws Exception {
        String responseJson = resultActions.andReturn().getResponse().getContentAsString();
        JsonObject jsonObject = gson.fromJson(responseJson, JsonObject.class);

        if (jsonObject != null && jsonObject.has("id")) {
            return jsonObject.get("id").getAsLong();
        }

        return null;
    }

    @Entao("a pessoa é criada com sucesso")
    @Transactional
    public void pessoaCriadaComSucesso() {
        assertThat(personId).isNotNull();
        
        var createdPerson = personRepository.findById(personId);
        
        assertThat(createdPerson).isPresent();
        
        var person = createdPerson.get();
        assertThat(person.getFullName()).isEqualTo("string");
        assertThat(person.getBirthDate()).isNotNull();
        assertThat(person.getAddresses()).hasSize(1);
        assertThat(person.getProfessionalData()).isNotNull();
        assertThat(person.isActive()).isTrue();
    }

    @Dado("que uma pessoa está com cadastro ativo")
    @Transactional
    public void cadastrarPessoaAtiva() {
        person = activePerson();
        personRepository.save(person);
        personId = person.getId();
    }

    @Quando("é requisitada a desativação de uma pessoa")
    public void requisitarDesativacaoDePessoa() throws Exception {
        Person editedPerson;
        BeanUtils.copyProperties(person, editedPerson = new Person());
        editedPerson.setActive(false);

        EditPersonRequest editPersonRequest = MappingUtils.convertPersonToEditPersonRequest(editedPerson);
        String request = gson.toJson(editPersonRequest);

        mockMvc.perform(
                patch("/api/people/" + personId)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Entao("a pessoa e seus endereços são desativados")
    public void validarDesativacaoPessoaEnderecos() {
        var editedPerson = personRepository.findById(personId);

        assertThat(editedPerson).isPresent();
        person = editedPerson.get();

        assertThat(person.isActive()).isFalse();

        List<Address> addresses = addressRepository.findAllByPersonId(personId);
        addresses.forEach(address -> assertThat(address.getActive()).isFalse());
    }

}
