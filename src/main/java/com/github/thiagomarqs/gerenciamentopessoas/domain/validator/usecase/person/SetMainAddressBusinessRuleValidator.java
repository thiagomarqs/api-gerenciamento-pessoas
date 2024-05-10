package com.github.thiagomarqs.gerenciamentopessoas.domain.validator.usecase.person;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.messages.AddressBusinessRuleMessages;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.messages.PersonBusinessRuleMessages;
import com.github.thiagomarqs.gerenciamentopessoas.domain.validator.ValidationResult;
import org.springframework.stereotype.Component;

@Component
public class SetMainAddressBusinessRuleValidator {

    public ValidationResult validate(Person person, Address newMainAddress) {
        var result = new ValidationResult();

        validateInactivePerson(person, result);
        validateInactiveAddress(newMainAddress, result);

        return result;
    }

    private void validateInactivePerson(Person person, ValidationResult result) {
        if(!person.isActive()) {
            var message = String.format(PersonBusinessRuleMessages.PERSON_IS_INACTIVE_FORMATTED, person.getId());
            result.addError(message);
        }
    }

    private void validateInactiveAddress(Address address, ValidationResult result) {
        if(!address.getActive()) {
            var message = String.format(AddressBusinessRuleMessages.ADDRESS_IS_INACTIVE_FORMATTED, address.getAddress());
            result.addError(message);
        }
    }

}
