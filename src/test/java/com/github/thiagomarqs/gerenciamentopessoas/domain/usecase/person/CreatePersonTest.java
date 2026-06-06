package com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.person;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.ContractType;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.ProfessionalData;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.BusinessRuleException;
import com.github.thiagomarqs.gerenciamentopessoas.domain.repository.PersonRepository;
import com.github.thiagomarqs.gerenciamentopessoas.domain.validator.ValidationResult;
import com.github.thiagomarqs.gerenciamentopessoas.domain.validator.usecase.person.CreatePersonBusinessRuleValidator;
import com.github.thiagomarqs.gerenciamentopessoas.domain.validator.usecase.professionaldata.ProfessionalDataBusinessRuleValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreatePersonTest {

    private final String city = "Cidade Teste";
    private final String state = "Estado Teste";

    @Mock
    private PersonRepository personRepository;

    @Mock
    private CreatePersonBusinessRuleValidator businessRuleValidator;

    @Mock
    private ProfessionalDataBusinessRuleValidator professionalDataBusinessRuleValidator;

    @InjectMocks
    private CreatePerson createPerson;

    @Test
    void shouldCreatePersonWhenValidationPasses() {

        var address = Address.builder()
                .address("Rua Teste")
                .cep("12345678")
                .number("999")
                .city(city)
                .state(state)
                .isMain(true)
                .active(true)
                .build();

        var person = Person.builder()
                .fullName("Fulano de Tal")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address(address)
                .mainAddress(address)
                .build();

        when(businessRuleValidator.validate(person)).thenReturn(new ValidationResult());
        when(professionalDataBusinessRuleValidator.validate((ProfessionalData) null)).thenReturn(new ValidationResult());
        when(personRepository.save(person)).thenReturn(person);

        createPerson.create(person);

        verify(personRepository).save(person);
    }

    @Test
    void shouldNotCreatePersonWhenValidationFails() {

        var address = Address.builder()
                .address("Rua Teste")
                .cep("12345678")
                .number("999")
                .city(city)
                .state(state)
                .isMain(true)
                .active(true)
                .build();

        var person = Person.builder()
                .fullName("Fulano de Tal")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address(address)
                .mainAddress(address)
                .build();

        ValidationResult validationResult = new ValidationResult();
        validationResult.addError("error");

        when(businessRuleValidator.validate(person)).thenReturn(validationResult);

        assertThrows(BusinessRuleException.class, () -> createPerson.create(person));

        verifyNoInteractions(personRepository);
    }

    @Test
    void shouldAutomaticallySetMainAddressIfNotSetAlready() {

            var address = Address.builder()
                    .address("Rua Teste")
                    .cep("12345678")
                    .number("999")
                    .city(city)
                    .state(state)
                    .active(true)
                    .build();

            var person = Person.builder()
                    .fullName("Fulano de Tal")
                    .birthDate(LocalDate.of(1990, 1, 1))
                    .address(address)
                    .build();

            when(businessRuleValidator.validate(person)).thenReturn(new ValidationResult());
            when(professionalDataBusinessRuleValidator.validate((ProfessionalData) null)).thenReturn(new ValidationResult());
            when(personRepository.save(person)).thenReturn(person);

            createPerson.create(person);

            assertTrue(person.hasMainAddress());
            assertEquals(address, person.getMainAddress());

            verify(personRepository).save(person);
    }

    @Test
    void shouldCreatePersonWithProfessionalDataWhenValidationPasses() {
        var address = Address.builder()
                .address("Rua Teste")
                .cep("12345678")
                .number("999")
                .city(city)
                .state(state)
                .isMain(true)
                .active(true)
                .build();

        var professionalData = ProfessionalData.builder()
                .companyName("Empresa Teste")
                .contractType(ContractType.CLT)
                .employmentStartDate(LocalDate.of(2020, 1, 1))
                .build();

        var person = Person.builder()
                .fullName("Fulano de Tal")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address(address)
                .mainAddress(address)
                .professionalData(professionalData)
                .build();

        when(businessRuleValidator.validate(person)).thenReturn(new ValidationResult());
        when(professionalDataBusinessRuleValidator.validate(professionalData)).thenReturn(new ValidationResult());
        when(personRepository.save(person)).thenReturn(person);

        createPerson.create(person);

        verify(personRepository).save(person);
    }

    @Test
    void shouldCreatePersonWithoutProfessionalData() {
        var address = Address.builder()
                .address("Rua Teste")
                .cep("12345678")
                .number("999")
                .city(city)
                .state(state)
                .isMain(true)
                .active(true)
                .build();

        var person = Person.builder()
                .fullName("Fulano de Tal")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address(address)
                .mainAddress(address)
                .professionalData(null)
                .build();

        when(businessRuleValidator.validate(person)).thenReturn(new ValidationResult());
        when(professionalDataBusinessRuleValidator.validate((ProfessionalData) null)).thenReturn(new ValidationResult());
        when(personRepository.save(person)).thenReturn(person);

        createPerson.create(person);

        verify(personRepository).save(person);
    }

    @Test
    void shouldNotCreatePersonWhenProfessionalDataContractTypeIsNull() {
        var address = Address.builder()
                .address("Rua Teste")
                .cep("12345678")
                .number("999")
                .city(city)
                .state(state)
                .isMain(true)
                .active(true)
                .build();

        var professionalData = ProfessionalData.builder()
                .companyName("Empresa Teste")
                .contractType(null)
                .employmentStartDate(LocalDate.of(2020, 1, 1))
                .build();

        var person = Person.builder()
                .fullName("Fulano de Tal")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address(address)
                .mainAddress(address)
                .professionalData(professionalData)
                .build();

        when(businessRuleValidator.validate(person)).thenReturn(new ValidationResult());

        ValidationResult professionalDataValidationResult = new ValidationResult();
        professionalDataValidationResult.addError("CONTRACT_TYPE_INVALID");
        when(professionalDataBusinessRuleValidator.validate(professionalData)).thenReturn(professionalDataValidationResult);

        assertThrows(BusinessRuleException.class, () -> createPerson.create(person));

        verifyNoInteractions(personRepository);
    }

    @Test
    void shouldNotCreatePersonWhenProfessionalDataStartDateIsInFuture() {
        var address = Address.builder()
                .address("Rua Teste")
                .cep("12345678")
                .number("999")
                .city(city)
                .state(state)
                .isMain(true)
                .active(true)
                .build();

        var futureDate = LocalDate.now().plusDays(1);
        var professionalData = ProfessionalData.builder()
                .companyName("Empresa Teste")
                .contractType(ContractType.CLT)
                .employmentStartDate(futureDate)
                .build();

        var person = Person.builder()
                .fullName("Fulano de Tal")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address(address)
                .mainAddress(address)
                .professionalData(professionalData)
                .build();

        when(businessRuleValidator.validate(person)).thenReturn(new ValidationResult());

        ValidationResult professionalDataValidationResult = new ValidationResult();
        professionalDataValidationResult.addError("EMPLOYMENT_START_DATE_INVALID");
        when(professionalDataBusinessRuleValidator.validate(professionalData)).thenReturn(professionalDataValidationResult);

        assertThrows(BusinessRuleException.class, () -> createPerson.create(person));

        verifyNoInteractions(personRepository);
    }

    @Test
    void shouldCreatePersonWhenProfessionalDataStartDateIsToday() {
        var address = Address.builder()
                .address("Rua Teste")
                .cep("12345678")
                .number("999")
                .city(city)
                .state(state)
                .isMain(true)
                .active(true)
                .build();

        var today = LocalDate.now();
        var professionalData = ProfessionalData.builder()
                .companyName("Empresa Teste")
                .contractType(ContractType.CLT)
                .employmentStartDate(today)
                .build();

        var person = Person.builder()
                .fullName("Fulano de Tal")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address(address)
                .mainAddress(address)
                .professionalData(professionalData)
                .build();

        when(businessRuleValidator.validate(person)).thenReturn(new ValidationResult());
        when(professionalDataBusinessRuleValidator.validate(professionalData)).thenReturn(new ValidationResult());
        when(personRepository.save(person)).thenReturn(person);

        createPerson.create(person);

        verify(personRepository).save(person);
    }
}