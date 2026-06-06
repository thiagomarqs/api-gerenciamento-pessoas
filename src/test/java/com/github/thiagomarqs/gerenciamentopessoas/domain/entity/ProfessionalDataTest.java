package com.github.thiagomarqs.gerenciamentopessoas.domain.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ProfessionalDataTest {

    @Test
    void shouldBuildProfessionalDataUsingBuilder() {
        // Arrange
        Long expectedId = 1L;
        String expectedCompanyName = "Empresa Teste";
        ContractType expectedContractType = ContractType.CLT;
        LocalDate expectedStartDate = LocalDate.of(2020, 1, 1);
        Person expectedPerson = Person.builder()
                .fullName("Fulano de Tal")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        // Act
        ProfessionalData professionalData = ProfessionalData.builder()
                .id(expectedId)
                .companyName(expectedCompanyName)
                .contractType(expectedContractType)
                .employmentStartDate(expectedStartDate)
                .person(expectedPerson)
                .build();

        // Assert
        assertEquals(expectedId, professionalData.getId());
        assertEquals(expectedCompanyName, professionalData.getCompanyName());
        assertEquals(expectedContractType, professionalData.getContractType());
        assertEquals(expectedStartDate, professionalData.getEmploymentStartDate());
        assertEquals(expectedPerson, professionalData.getPerson());
    }

    @Test
    void shouldBuildProfessionalDataWithMinimalFields() {
        // Arrange
        String expectedCompanyName = "Empresa Teste";
        ContractType expectedContractType = ContractType.PJ;
        LocalDate expectedStartDate = LocalDate.of(2020, 1, 1);

        // Act
        ProfessionalData professionalData = ProfessionalData.builder()
                .companyName(expectedCompanyName)
                .contractType(expectedContractType)
                .employmentStartDate(expectedStartDate)
                .build();

        // Assert
        assertNull(professionalData.getId());
        assertEquals(expectedCompanyName, professionalData.getCompanyName());
        assertEquals(expectedContractType, professionalData.getContractType());
        assertEquals(expectedStartDate, professionalData.getEmploymentStartDate());
        assertNull(professionalData.getPerson());
    }

    @Test
    void shouldCreateProfessionalDataUsingConstructorWithAllFields() {
        // Arrange
        String companyName = "Empresa Teste";
        ContractType contractType = ContractType.CLT;
        LocalDate startDate = LocalDate.of(2020, 1, 1);
        Person person = Person.builder()
                .fullName("Fulano de Tal")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        // Act
        ProfessionalData professionalData = new ProfessionalData(companyName, contractType, startDate, person);

        // Assert
        assertEquals(companyName, professionalData.getCompanyName());
        assertEquals(contractType, professionalData.getContractType());
        assertEquals(startDate, professionalData.getEmploymentStartDate());
        assertEquals(person, professionalData.getPerson());
        assertNull(professionalData.getId());
    }

    @Test
    void shouldCreateProfessionalDataUsingNoArgsConstructor() {
        // Act
        ProfessionalData professionalData = new ProfessionalData();

        // Assert
        assertNotNull(professionalData);
        assertNull(professionalData.getId());
        assertNull(professionalData.getCompanyName());
        assertNull(professionalData.getContractType());
        assertNull(professionalData.getEmploymentStartDate());
        assertNull(professionalData.getPerson());
    }

    @Test
    void shouldMaintainRelationshipWithPerson() {
        // Arrange
        Person person = Person.builder()
                .fullName("Fulano de Tal")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        ProfessionalData professionalData = ProfessionalData.builder()
                .id(1L)
                .companyName("Empresa Teste")
                .contractType(ContractType.CLT)
                .employmentStartDate(LocalDate.of(2020, 1, 1))
                .person(person)
                .build();

        // Act
        person.setProfessionalData(professionalData);

        // Assert
        assertEquals(professionalData, person.getProfessionalData());
        assertEquals(person, professionalData.getPerson());
    }
}
