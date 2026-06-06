package com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.person;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.ContractType;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.ProfessionalData;
import com.github.thiagomarqs.gerenciamentopessoas.domain.repository.PersonRepository;
import com.github.thiagomarqs.gerenciamentopessoas.domain.validator.ValidationResult;
import com.github.thiagomarqs.gerenciamentopessoas.domain.validator.usecase.professionaldata.ProfessionalDataBusinessRuleValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EditPersonTest {

    @Mock
    PersonRepository personRepository;

    @Mock
    FindPeople findPeople;

    @Mock
    ProfessionalDataBusinessRuleValidator professionalDataValidator;

    @InjectMocks
    EditPerson editPerson;

    String state = "Estado Teste";
    String city = "Teste";

    @Test
    void shouldEditPerson() {
        var personId = 1L;

        var address = Address.builder()
                .id(1L)
                .address("Rua Teste")
                .cep("12345678")
                .active(true)
                .city(city)
                .state(state)
                .build();

        String oldFullName = "Fulano de Tal";
        LocalDate oldBirthDate = LocalDate.of(1990, 1, 1);

        var person = Person.builder()
                .id(personId)
                .fullName(oldFullName)
                .active(true)
                .birthDate(oldBirthDate)
                .address(address)
                .mainAddress(address)
                .build();

        String newFullName = "Fulano Beltrano de Tal";
        LocalDate newBirthDate = LocalDate.of(2000, 2, 2);

        var edited = Person.builder()
                .id(personId)
                .fullName(newFullName)
                .active(true)
                .birthDate(newBirthDate)
                .build();

        when(findPeople.findOne(personId)).thenReturn(person);

        assertTrue(person.getAddresses().contains(address));
        assertEquals(person.getMainAddress(), address);
        assertEquals(address.getPerson(), person);
        assertEquals(oldFullName, person.getFullName());
        assertEquals(oldBirthDate, person.getBirthDate());

        editPerson.edit(personId, edited);

        assertTrue(person.getAddresses().contains(address));
        assertEquals(address, person.getMainAddress());
        assertEquals(person, address.getPerson());
        assertEquals(newFullName, person.getFullName());
        assertEquals(newBirthDate, person.getBirthDate());

    }

    @Test
    void shouldInactivateAllAddressesWhenInactivatingPerson() {

        var personId = 1L;

        var address = Address.builder()
                .id(1L)
                .address("Rua Teste 111")
                .cep("12345678")
                .active(true)
                .city(city)
                .state(state)
                .isMain(true)
                .build();

        var address2 = Address.builder()
                .id(2L)
                .address("Avenida Teste 555")
                .cep("87654321")
                .active(true)
                .city(city)
                .state(state)
                .build();

        var person = Person.builder()
                .id(personId)
                .fullName("Fulano de Tal")
                .active(true)
                .birthDate(LocalDate.of(1990, 1, 1))
                .address(address)
                .address(address2)
                .mainAddress(address)
                .build();

        var edited = Person.builder()
                .id(personId)
                .fullName("Fulano de Tal")
                .active(false)
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        when(findPeople.findOne(personId)).thenReturn(person);

        person.getAddresses().forEach(a -> {
            assertTrue(a.getActive());
            assertEquals(person, a.getPerson());
        });

        editPerson.edit(personId, edited);

        person.getAddresses().forEach(a -> {
            assertFalse(a.getActive());
            assertEquals(person, a.getPerson());
        });

    }

    @Test
    void shouldActivateAllAddressesWhenReactivatingPerson() {

        var personId = 1L;

        var address = Address.builder()
                .id(1L)
                .address("Rua Teste 111")
                .cep("12345678")
                .active(false)
                .city(city)
                .state(state)
                .isMain(true)
                .build();

        var address2 = Address.builder()
                .id(2L)
                .address("Avenida Teste 555")
                .cep("87654321")
                .active(false)
                .city(city)
                .state(state)
                .build();

        var person = Person.builder()
                .id(personId)
                .fullName("Fulano de Tal")
                .active(false)
                .birthDate(LocalDate.of(1990, 1, 1))
                .address(address)
                .address(address2)
                .mainAddress(address)
                .build();

        var edited = Person.builder()
                .id(personId)
                .fullName("Fulano de Tal")
                .active(true)
                .birthDate(LocalDate.of(1990, 1, 1))
                .address(address)
                .mainAddress(address)
                .build();

        when(findPeople.findOne(personId)).thenReturn(person);

        assertFalse(person.isActive());

        person.getAddresses().forEach(a -> {
            assertFalse(a.getActive());
            assertEquals(person, a.getPerson());
        });

        editPerson.edit(personId, edited);

        assertTrue(person.isActive());

        person.getAddresses().forEach(a -> {
            assertTrue(a.getActive());
            assertEquals(person, a.getPerson());
        });

    }

    @Test
    void shouldUpdateProfessionalDataWhenPresent() {
        var personId = 1L;

        var oldProfessionalData = ProfessionalData.builder()
                .id(1L)
                .companyName("Old Company")
                .contractType(ContractType.CLT)
                .employmentStartDate(LocalDate.of(2020, 1, 1))
                .build();

        var person = Person.builder()
                .id(personId)
                .fullName("Fulano de Tal")
                .active(true)
                .birthDate(LocalDate.of(1990, 1, 1))
                .professionalData(oldProfessionalData)
                .build();

        var newProfessionalData = ProfessionalData.builder()
                .companyName("New Company")
                .contractType(ContractType.PJ)
                .employmentStartDate(LocalDate.of(2023, 6, 1))
                .build();

        var edited = Person.builder()
                .id(personId)
                .fullName("Fulano de Tal")
                .active(true)
                .birthDate(LocalDate.of(1990, 1, 1))
                .professionalData(newProfessionalData)
                .build();

        when(findPeople.findOne(personId)).thenReturn(person);
        when(professionalDataValidator.validate(newProfessionalData)).thenReturn(new ValidationResult());

        assertEquals(oldProfessionalData, person.getProfessionalData());

        editPerson.edit(personId, edited);

        assertEquals(newProfessionalData.getCompanyName(), person.getProfessionalData().getCompanyName());
        assertEquals(newProfessionalData.getContractType(), person.getProfessionalData().getContractType());
        assertEquals(newProfessionalData.getEmploymentStartDate(), person.getProfessionalData().getEmploymentStartDate());
    }

    @Test
    void shouldAddProfessionalDataWhenPersonHasNone() {
        var personId = 1L;

        var person = Person.builder()
                .id(personId)
                .fullName("Fulano de Tal")
                .active(true)
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        var professionalData = ProfessionalData.builder()
                .companyName("New Company")
                .contractType(ContractType.PJ)
                .employmentStartDate(LocalDate.of(2023, 6, 1))
                .build();

        var edited = Person.builder()
                .id(personId)
                .fullName("Fulano de Tal")
                .active(true)
                .birthDate(LocalDate.of(1990, 1, 1))
                .professionalData(professionalData)
                .build();

        when(findPeople.findOne(personId)).thenReturn(person);
        when(professionalDataValidator.validate(professionalData)).thenReturn(new ValidationResult());

        assertNull(person.getProfessionalData());

        editPerson.edit(personId, edited);

        assertNotNull(person.getProfessionalData());
        assertEquals(professionalData.getCompanyName(), person.getProfessionalData().getCompanyName());
        assertEquals(professionalData.getContractType(), person.getProfessionalData().getContractType());
        assertEquals(professionalData.getEmploymentStartDate(), person.getProfessionalData().getEmploymentStartDate());
    }

    @Test
    void shouldRemoveProfessionalDataWhenNull() {
        var personId = 1L;

        var professionalData = ProfessionalData.builder()
                .id(1L)
                .companyName("Company")
                .contractType(ContractType.CLT)
                .employmentStartDate(LocalDate.of(2020, 1, 1))
                .build();

        var person = Person.builder()
                .id(personId)
                .fullName("Fulano de Tal")
                .active(true)
                .birthDate(LocalDate.of(1990, 1, 1))
                .professionalData(professionalData)
                .build();

        var edited = Person.builder()
                .id(personId)
                .fullName("Fulano de Tal")
                .active(true)
                .birthDate(LocalDate.of(1990, 1, 1))
                .professionalData(null)
                .build();

        when(findPeople.findOne(personId)).thenReturn(person);

        assertNotNull(person.getProfessionalData());
        assertEquals("Fulano de Tal", person.getFullName());
        assertTrue(person.isActive());
        assertEquals(LocalDate.of(1990, 1, 1), person.getBirthDate());

        editPerson.edit(personId, edited);

        assertNull(person.getProfessionalData());
        assertEquals("Fulano de Tal", person.getFullName());
        assertTrue(person.isActive());
        assertEquals(LocalDate.of(1990, 1, 1), person.getBirthDate());
    }

    @Test
    void shouldThrowExceptionWhenContractTypeIsNull() {
        var personId = 1L;

        var person = Person.builder()
                .id(personId)
                .fullName("Fulano de Tal")
                .active(true)
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        var professionalData = ProfessionalData.builder()
                .companyName("Company")
                .contractType(null)
                .employmentStartDate(LocalDate.of(2023, 6, 1))
                .build();

        var edited = Person.builder()
                .id(personId)
                .fullName("Fulano de Tal")
                .active(true)
                .birthDate(LocalDate.of(1990, 1, 1))
                .professionalData(professionalData)
                .build();

        when(findPeople.findOne(personId)).thenReturn(person);

        ValidationResult validationResult = new ValidationResult();
        validationResult.addError("CONTRACT_TYPE_INVALID");
        when(professionalDataValidator.validate(professionalData)).thenReturn(validationResult);

        assertThrows(com.github.thiagomarqs.gerenciamentopessoas.domain.exception.BusinessRuleException.class, () -> {
            editPerson.edit(personId, edited);
        });
    }

    @Test
    void shouldThrowExceptionWhenEmploymentStartDateIsFuture() {
        var personId = 1L;

        var person = Person.builder()
                .id(personId)
                .fullName("Fulano de Tal")
                .active(true)
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        var professionalData = ProfessionalData.builder()
                .companyName("Company")
                .contractType(ContractType.CLT)
                .employmentStartDate(LocalDate.now().plusDays(1))
                .build();

        var edited = Person.builder()
                .id(personId)
                .fullName("Fulano de Tal")
                .active(true)
                .birthDate(LocalDate.of(1990, 1, 1))
                .professionalData(professionalData)
                .build();

        when(findPeople.findOne(personId)).thenReturn(person);

        ValidationResult validationResult = new ValidationResult();
        validationResult.addError("EMPLOYMENT_START_DATE_INVALID");
        when(professionalDataValidator.validate(professionalData)).thenReturn(validationResult);

        assertThrows(com.github.thiagomarqs.gerenciamentopessoas.domain.exception.BusinessRuleException.class, () -> {
            editPerson.edit(personId, edited);
        });
    }

    @Test
    void shouldPartiallyUpdateProfessionalData() {
        var personId = 1L;

        var existingProfessionalData = ProfessionalData.builder()
                .id(1L)
                .companyName("Old Company")
                .contractType(ContractType.CLT)
                .employmentStartDate(LocalDate.of(2020, 1, 1))
                .build();

        var person = Person.builder()
                .id(personId)
                .fullName("Fulano de Tal")
                .active(true)
                .birthDate(LocalDate.of(1990, 1, 1))
                .professionalData(existingProfessionalData)
                .build();

        var partialUpdate = ProfessionalData.builder()
                .companyName("New Company")
                .contractType(null)
                .employmentStartDate(null)
                .build();

        var edited = Person.builder()
                .id(personId)
                .fullName("Fulano de Tal")
                .active(true)
                .birthDate(LocalDate.of(1990, 1, 1))
                .professionalData(partialUpdate)
                .build();

        when(findPeople.findOne(personId)).thenReturn(person);
        when(professionalDataValidator.validate(partialUpdate)).thenReturn(new ValidationResult());

        assertEquals("Old Company", person.getProfessionalData().getCompanyName());
        assertEquals(ContractType.CLT, person.getProfessionalData().getContractType());
        assertEquals(LocalDate.of(2020, 1, 1), person.getProfessionalData().getEmploymentStartDate());

        editPerson.edit(personId, edited);

        assertEquals("New Company", person.getProfessionalData().getCompanyName());
        assertEquals(ContractType.CLT, person.getProfessionalData().getContractType());
        assertEquals(LocalDate.of(2020, 1, 1), person.getProfessionalData().getEmploymentStartDate());
    }

    @Test
    void shouldPartiallyUpdateContractTypeOnly() {
        var personId = 1L;

        var existingProfessionalData = ProfessionalData.builder()
                .id(1L)
                .companyName("Old Company")
                .contractType(ContractType.CLT)
                .employmentStartDate(LocalDate.of(2020, 1, 1))
                .build();

        var person = Person.builder()
                .id(personId)
                .fullName("Fulano de Tal")
                .active(true)
                .birthDate(LocalDate.of(1990, 1, 1))
                .professionalData(existingProfessionalData)
                .build();

        var partialUpdate = ProfessionalData.builder()
                .companyName(null)
                .contractType(ContractType.PJ)
                .employmentStartDate(null)
                .build();

        var edited = Person.builder()
                .id(personId)
                .fullName("Fulano de Tal")
                .active(true)
                .birthDate(LocalDate.of(1990, 1, 1))
                .professionalData(partialUpdate)
                .build();

        when(findPeople.findOne(personId)).thenReturn(person);
        when(professionalDataValidator.validate(partialUpdate)).thenReturn(new ValidationResult());

        assertEquals(ContractType.CLT, person.getProfessionalData().getContractType());

        editPerson.edit(personId, edited);

        assertEquals("Old Company", person.getProfessionalData().getCompanyName());
        assertEquals(ContractType.PJ, person.getProfessionalData().getContractType());
        assertEquals(LocalDate.of(2020, 1, 1), person.getProfessionalData().getEmploymentStartDate());
    }

}