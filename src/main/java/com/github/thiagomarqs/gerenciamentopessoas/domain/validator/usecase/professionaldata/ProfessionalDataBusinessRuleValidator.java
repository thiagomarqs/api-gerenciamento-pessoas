package com.github.thiagomarqs.gerenciamentopessoas.domain.validator.usecase.professionaldata;

import com.github.thiagomarqs.gerenciamentopessoas.controller.dto.professionaldata.request.ProfessionalDataRequest;
import com.github.thiagomarqs.gerenciamentopessoas.controller.dto.professionaldata.request.UpdateProfessionalDataRequest;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.ProfessionalData;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.messages.ProfessionalDataBusinessRuleMessages;
import com.github.thiagomarqs.gerenciamentopessoas.domain.validator.ValidationResult;
import jakarta.inject.Inject;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ProfessionalDataBusinessRuleValidator {

    @Inject
    public ProfessionalDataBusinessRuleValidator() {
    }

    public ValidationResult validate(ProfessionalDataRequest request) {
        var result = new ValidationResult();

        if (request == null) {
            return result;
        }

        validateContractTypeForCreation(request.contractType(), result);
        validateEmploymentStartDateForCreation(request.employmentStartDate(), result);

        return result;
    }

    public ValidationResult validate(UpdateProfessionalDataRequest request) {
        var result = new ValidationResult();

        if (request == null) {
            return result;
        }

        validateContractType(request.contractType(), result);
        validateEmploymentStartDate(request.employmentStartDate(), result);

        return result;
    }

    public ValidationResult validate(ProfessionalData professionalData) {
        var result = new ValidationResult();

        if (professionalData == null) {
            return result;
        }

        validateContractType(professionalData.getContractType(), result);
        validateEmploymentStartDate(professionalData.getEmploymentStartDate(), result);

        return result;
    }

    private void validateContractTypeForCreation(com.github.thiagomarqs.gerenciamentopessoas.domain.entity.ContractType contractType, ValidationResult result) {
        if (contractType == null) {
            result.addError(ProfessionalDataBusinessRuleMessages.CONTRACT_TYPE_REQUIRED);
            return;
        }

        if (contractType != com.github.thiagomarqs.gerenciamentopessoas.domain.entity.ContractType.CLT
                && contractType != com.github.thiagomarqs.gerenciamentopessoas.domain.entity.ContractType.PJ) {
            result.addError(ProfessionalDataBusinessRuleMessages.CONTRACT_TYPE_INVALID);
        }
    }

    private void validateContractType(com.github.thiagomarqs.gerenciamentopessoas.domain.entity.ContractType contractType, ValidationResult result) {
        if (contractType == null) {
            return;
        }

        if (contractType != com.github.thiagomarqs.gerenciamentopessoas.domain.entity.ContractType.CLT
                && contractType != com.github.thiagomarqs.gerenciamentopessoas.domain.entity.ContractType.PJ) {
            result.addError(ProfessionalDataBusinessRuleMessages.CONTRACT_TYPE_INVALID);
        }
    }

    private void validateEmploymentStartDateForCreation(LocalDate employmentStartDate, ValidationResult result) {
        validateEmploymentStartDate(employmentStartDate, result);
    }

    private void validateEmploymentStartDate(LocalDate employmentStartDate, ValidationResult result) {
        if (employmentStartDate == null) {
            return;
        }

        if (employmentStartDate.isAfter(LocalDate.now())) {
            result.addError(ProfessionalDataBusinessRuleMessages.FUTURE_START_DATE);
        }
    }
}
