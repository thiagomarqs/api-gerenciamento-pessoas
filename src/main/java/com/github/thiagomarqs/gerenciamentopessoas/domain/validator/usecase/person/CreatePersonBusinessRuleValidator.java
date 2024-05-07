package com.github.thiagomarqs.gerenciamentopessoas.domain.validator.usecase.person;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.messages.AddressBusinessRuleMessages;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.messages.EntityBusinessRuleMessages;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.messages.PersonBusinessRuleMessages;
import com.github.thiagomarqs.gerenciamentopessoas.domain.validator.ValidationResult;
import com.github.thiagomarqs.gerenciamentopessoas.domain.validator.address.AddressValidator;
import jakarta.inject.Inject;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CreatePersonBusinessRuleValidator {

    private final ValidationResult result = new ValidationResult();
    private final AddressValidator addressValidator;

    @Inject
    public CreatePersonBusinessRuleValidator(AddressValidator addressValidator) {
        this.addressValidator = addressValidator;
    }

    public ValidationResult validate(Person person) {

        var isNull = validateNull(person);

        if(isNull) return result;

        validateNoAddresses(person);
        validateIfThereIsSomeInvalidCep(person);
        validateMoreThanOneMainAddress(person);
        validateIfCantSetMainAddressAutomatically(person);

        return result;
    }

    private boolean validateNull(Person person) {
        if(person == null) {
            var message = String.format(EntityBusinessRuleMessages.NULL_ENTITY_FORMATTED_WITH_CLASS_NAME, Person.class.getSimpleName());
            result.addError(message);
            return true;
        }

        return false;
    }

    private void validateIfThereIsSomeInvalidCep(Person person) {
        List<Address> addresses = person.getAddresses();
        var addressValidationResult = addressValidator.validateAddressesCep(addresses);

        if(addressValidationResult.hasFailures()) {
            var invalidCeps = addressValidationResult.getInvalidCeps();
            var message = String.format(AddressBusinessRuleMessages.INVALID_CEPS_FORMATTED, invalidCeps.toString());
            result.addError(message);
        }
    }

    private void validateMoreThanOneMainAddress(Person person) {
        var addresses = person.getAddresses();
        var mainAddresses = addresses.stream().filter(a -> a.getIsMain() != null && a.getIsMain()).count();

        if(mainAddresses > 1) {
            String message = AddressBusinessRuleMessages.TRYING_TO_SET_MORE_THAN_ONE_MAIN_ADDRESS;
            result.addError(message);
        }
    }

    private void validateNoAddresses(Person person) {
        if(person.getAddresses().isEmpty()) {
            String message = PersonBusinessRuleMessages.CANT_CREATE_PERSON_WITHOUT_ADDRESS;
            result.addError(message);
        }
    }

    private void validateIfCantSetMainAddressAutomatically(Person person) {
        var hasMainAddress = person.hasMainAddress();
        var hasMoreThanOneAddress = person.getAddresses().size() > 1;

        if (!hasMainAddress && hasMoreThanOneAddress) {
            String message = AddressBusinessRuleMessages.CANT_SET_MAIN_ADDRESS_WHEN_MORE_THAN_ONE_ADDRESS;
            result.addError(message);
        }
    }

}
