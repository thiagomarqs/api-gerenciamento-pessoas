package com.github.thiagomarqs.gerenciamentopessoas.domain.validator.usecase.professionaldata;

import com.github.thiagomarqs.gerenciamentopessoas.controller.dto.professionaldata.request.ProfessionalDataRequest;
import com.github.thiagomarqs.gerenciamentopessoas.controller.dto.professionaldata.request.UpdateProfessionalDataRequest;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.ContractType;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.ProfessionalData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ProfessionalDataBusinessRuleValidatorTest {

    @InjectMocks
    ProfessionalDataBusinessRuleValidator validator;

    @ParameterizedTest
    @MethodSource("provideValidProfessionalDataRequests")
    void shouldReturnValidResultWhenRequestIsValid(ProfessionalDataRequest request) {
        var result = validator.validate(request);
        assertFalse(result.hasErrors());
    }

    @ParameterizedTest
    @NullSource
    void shouldReturnValidResultWhenRequestIsNull(ProfessionalDataRequest request) {
        var result = validator.validate(request);
        assertFalse(result.hasErrors());
    }

    @Test
    void shouldReturnInvalidResultWhenRequestHasNullContractType() {
        var request = new ProfessionalDataRequest(
                "Empresa Teste",
                null,
                LocalDate.now().minusDays(1)
        );

        var result = validator.validate(request);

        assertTrue(result.hasErrors());
        assertTrue(result.getErrors().contains("Tipo de contrato é obrigatório."));
    }

    @Test
    void shouldReturnInvalidResultWhenRequestHasFutureStartDate() {
        var request = new ProfessionalDataRequest(
                "Empresa Teste",
                ContractType.CLT,
                LocalDate.now().plusDays(1)
        );

        var result = validator.validate(request);

        assertTrue(result.hasErrors());
        assertTrue(result.getErrors().contains("A data de início do vínculo não pode ser futura."));
    }

    @Test
    void shouldReturnInvalidResultWhenRequestHasNullContractTypeAndFutureStartDate() {
        var request = new ProfessionalDataRequest(
                "Empresa Teste",
                null,
                LocalDate.now().plusDays(1)
        );

        var result = validator.validate(request);

        assertTrue(result.hasErrors());
        assertTrue(result.getErrors().contains("Tipo de contrato é obrigatório."));
        assertTrue(result.getErrors().contains("A data de início do vínculo não pode ser futura."));
    }

    @ParameterizedTest
    @MethodSource("provideValidProfessionalDataEntities")
    void shouldReturnValidResultWhenEntityIsValid(ProfessionalData professionalData) {
        var result = validator.validate(professionalData);
        assertFalse(result.hasErrors());
    }

    @Test
    void shouldReturnValidResultWhenEntityHasNullContractType() {
        var professionalData = ProfessionalData.builder()
                .companyName("Empresa Teste")
                .contractType(null)
                .employmentStartDate(LocalDate.now().minusDays(1))
                .build();

        var result = validator.validate(professionalData);

        assertFalse(result.hasErrors());
    }

    @Test
    void shouldReturnInvalidResultWhenEntityHasFutureStartDate() {
        var professionalData = ProfessionalData.builder()
                .companyName("Empresa Teste")
                .contractType(ContractType.CLT)
                .employmentStartDate(LocalDate.now().plusDays(1))
                .build();

        var result = validator.validate(professionalData);

        assertTrue(result.hasErrors());
        assertTrue(result.getErrors().contains("A data de início do vínculo não pode ser futura."));
    }

    @Test
    void shouldReturnInvalidResultWhenEntityHasNullContractTypeAndFutureStartDate() {
        var professionalData = ProfessionalData.builder()
                .companyName("Empresa Teste")
                .contractType(null)
                .employmentStartDate(LocalDate.now().plusDays(1))
                .build();

        var result = validator.validate(professionalData);

        assertTrue(result.hasErrors());
        assertTrue(result.getErrors().contains("A data de início do vínculo não pode ser futura."));
    }

    @Test
    void shouldReturnValidResultWhenEntityIsNull() {
        var result = validator.validate((ProfessionalData) null);
        assertFalse(result.hasErrors());
    }

    @Test
    void shouldReturnValidResultWhenEntityHasNullStartDate() {
        var professionalData = ProfessionalData.builder()
                .companyName("Empresa Teste")
                .contractType(ContractType.CLT)
                .employmentStartDate(null)
                .build();

        var result = validator.validate(professionalData);
        assertFalse(result.hasErrors());
    }

    @ParameterizedTest
    @MethodSource("provideValidUpdateProfessionalDataRequests")
    void shouldReturnValidResultWhenUpdateRequestIsValid(UpdateProfessionalDataRequest request) {
        var result = validator.validate(request);
        assertFalse(result.hasErrors());
    }

    @Test
    void shouldReturnValidResultWhenUpdateRequestHasNullContractType() {
        var request = new UpdateProfessionalDataRequest(
                "Empresa Teste",
                null,
                LocalDate.now().minusDays(1)
        );

        var result = validator.validate(request);
        assertFalse(result.hasErrors());
    }

    @Test
    void shouldReturnInvalidResultWhenUpdateRequestHasFutureStartDate() {
        var request = new UpdateProfessionalDataRequest(
                "Empresa Teste",
                ContractType.CLT,
                LocalDate.now().plusDays(1)
        );

        var result = validator.validate(request);

        assertTrue(result.hasErrors());
        assertTrue(result.getErrors().contains("A data de início do vínculo não pode ser futura."));
    }

    @Test
    void shouldReturnValidResultWhenUpdateRequestIsNull() {
        var result = validator.validate((UpdateProfessionalDataRequest) null);
        assertFalse(result.hasErrors());
    }

    @Test
    void shouldReturnValidResultWhenUpdateRequestHasNullStartDate() {
        var request = new UpdateProfessionalDataRequest(
                "Empresa Teste",
                ContractType.CLT,
                null
        );

        var result = validator.validate(request);
        assertFalse(result.hasErrors());
    }

    private static Stream<Arguments> provideValidProfessionalDataRequests() {
        return Stream.of(
                Arguments.of(new ProfessionalDataRequest(
                        "Empresa Teste",
                        ContractType.CLT,
                        LocalDate.now().minusDays(1)
                )),
                Arguments.of(new ProfessionalDataRequest(
                        "Empresa Teste",
                        ContractType.PJ,
                        LocalDate.now()
                ))
        );
    }

    private static Stream<Arguments> provideValidProfessionalDataEntities() {
        return Stream.of(
                Arguments.of(ProfessionalData.builder()
                        .companyName("Empresa Teste")
                        .contractType(ContractType.CLT)
                        .employmentStartDate(LocalDate.now().minusDays(1))
                        .build()),
                Arguments.of(ProfessionalData.builder()
                        .companyName("Empresa Teste")
                        .contractType(ContractType.PJ)
                        .employmentStartDate(LocalDate.now())
                        .build()),
                Arguments.of(ProfessionalData.builder()
                        .companyName("Empresa Teste")
                        .contractType(ContractType.CLT)
                        .employmentStartDate(LocalDate.now().minusDays(1))
                        .build())
        );
    }

    private static Stream<Arguments> provideValidUpdateProfessionalDataRequests() {
        return Stream.of(
                Arguments.of(new UpdateProfessionalDataRequest(
                        "Empresa Teste",
                        ContractType.CLT,
                        LocalDate.now().minusDays(1)
                )),
                Arguments.of(new UpdateProfessionalDataRequest(
                        "Empresa Teste",
                        ContractType.PJ,
                        LocalDate.now()
                ))
        );
    }
}
