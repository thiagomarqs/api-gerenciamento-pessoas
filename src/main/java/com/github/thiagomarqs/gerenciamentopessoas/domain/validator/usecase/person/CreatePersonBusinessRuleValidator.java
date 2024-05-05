package com.github.thiagomarqs.gerenciamentopessoas.domain.validator.usecase.person;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.messages.AddressBusinessRuleMessages;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.messages.PersonBusinessRuleMessages;
import com.github.thiagomarqs.gerenciamentopessoas.domain.validator.ValidationResult;
import com.github.thiagomarqs.gerenciamentopessoas.domain.validator.address.AddressValidator;
import jakarta.inject.Inject;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CreatePersonBusinessRuleValidator {

    private AddressValidator addressValidator;

    @Inject
    public CreatePersonBusinessRuleValidator(AddressValidator addressValidator) {
        this.addressValidator = addressValidator;
    }

    public ValidationResult validate(Person person) {
        var result = new ValidationResult();

        validateNoAddresses(person, result);
        validateIfThereIsSomeInvalidCep(person, result);
        validateMoreThanOneMainAddress(person, result);
        validateIfCantSetMainAddressAutomatically(person, result);

        return result;
    }

    private void validateIfThereIsSomeInvalidCep(Person person, ValidationResult result) {
        List<Address> addresses = person.getAddresses();
        var addressValidationResult = addressValidator.validateAddressesCep(addresses);

        if(addressValidationResult.hasFailures()) {
            var invalidCeps = addressValidationResult.getInvalidCeps();
            var message = String.format(AddressBusinessRuleMessages.INVALID_CEPS_FORMATTED, invalidCeps.toString());
            result.addError(message);
        }
    }

    private void validateMoreThanOneMainAddress(Person person, ValidationResult result) {
        var addresses = person.getAddresses();
        var mainAddresses = addresses.stream().filter(a -> a.getIsMain() != null && a.getIsMain()).count();

        if(mainAddresses > 1) {
            String message = AddressBusinessRuleMessages.TRYING_TO_SET_MORE_THAN_ONE_MAIN_ADDRESS;
            result.addError(message);
        }
    }

    private void validateNoAddresses(Person person, ValidationResult result) {
        if(person.getAddresses().isEmpty()) {
            String message = PersonBusinessRuleMessages.CANT_CREATE_PERSON_WITHOUT_ADDRESS;
            result.addError(message);
        }
    }

    private void validateIfCantSetMainAddressAutomatically(Person person, ValidationResult result) {
        var hasMainAddress = person.hasMainAddress();
        var hasMoreThanOneAddress = person.getAddresses().size() > 1;

        if (!hasMainAddress && hasMoreThanOneAddress) {
            String message = AddressBusinessRuleMessages.CANT_SET_MAIN_ADDRESS_WHEN_MORE_THAN_ONE_ADDRESS;
            result.addError(message);
        }
    }

}
