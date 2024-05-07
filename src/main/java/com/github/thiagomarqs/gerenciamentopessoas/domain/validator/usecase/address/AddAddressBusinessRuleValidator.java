package com.github.thiagomarqs.gerenciamentopessoas.domain.validator.usecase.address;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.messages.AddressBusinessRuleMessages;
import com.github.thiagomarqs.gerenciamentopessoas.domain.validator.ValidationResult;
import com.github.thiagomarqs.gerenciamentopessoas.domain.validator.address.AddressValidator;
import jakarta.inject.Inject;
import org.springframework.stereotype.Component;

@Component
public class AddAddressBusinessRuleValidator {

    private final AddressValidator addressValidator;

    @Inject
    public AddAddressBusinessRuleValidator(AddressValidator addressValidator) {
        this.addressValidator = addressValidator;
    }

    public ValidationResult validate(Address newAddress) {
        var result = new ValidationResult();

        validateDuplicatedAddress(newAddress, result);
        validateCep(newAddress, result);

        return result;
    }

    private void validateCep(Address edited, ValidationResult result) {
        var isValid = addressValidator.validateCep(edited);
        var cep = edited.getCep();

        if(!isValid) {
            var message = String.format(AddressBusinessRuleMessages.INVALID_CEPS_FORMATTED, cep);
            result.addError(message);
        }
    }

    private void validateDuplicatedAddress(Address newAddress, ValidationResult result) {
        var person = newAddress.getPerson();
        boolean isDuplicate = verifyIfAddressIsDuplicated(newAddress, person);

        if(isDuplicate) {
           var message = AddressBusinessRuleMessages.DUPLICATED_ADDRESS;
           result.addError(message);
        }
    }

    private boolean verifyIfAddressIsDuplicated(Address newAddress, Person person) {
        return person.getAddresses().stream().anyMatch(a ->
                a.getAddress().equals(newAddress.getAddress()) &&
                        a.getCep().equals(newAddress.getCep()) &&
                        a.getNumber().equals(newAddress.getNumber()) &&
                        a.getCity().equals(newAddress.getCity()) &&
                        a.getState().equals(newAddress.getState())
        );
    }

}
