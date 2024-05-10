package com.github.thiagomarqs.gerenciamentopessoas.domain.validator.usecase.address;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.messages.AddressBusinessRuleMessages;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.messages.EntityBusinessRuleMessages;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.messages.PersonBusinessRuleMessages;
import com.github.thiagomarqs.gerenciamentopessoas.domain.validator.ValidationResult;
import org.springframework.stereotype.Component;

@Component
public class RemoveAddressBusinessRuleValidator {

    public ValidationResult validate(Long addressId, Person person) {

        var result = new ValidationResult();

        validateAddressDoesNotExist(addressId, person, result);
        validateOnlyAddress(person, result);
        validateIfTryingToRemoveMainAddressAndPersonHasMoreThanTwoAddresses(addressId, person, result);

        return result;
    }

    private void validateIfTryingToRemoveMainAddressAndPersonHasMoreThanTwoAddresses(Long addressId, Person person, ValidationResult result) {
        boolean hasMoreThanTwoAddresses = person.getAddresses().size() > 2;
        boolean isTryingToDeleteMainAddress = person.getMainAddress().getId().equals(addressId);

        if(isTryingToDeleteMainAddress && hasMoreThanTwoAddresses) {
            var message = String.format(AddressBusinessRuleMessages.CANT_REMOVE_MAIN_ADDRESS_WHEN_MORE_THAN_TWO_ADDRESSES_FORMATTED, addressId);
            result.addError(message);
        }
    }

    private void validateOnlyAddress(Person person, ValidationResult result) {
        boolean isTheOnlyAddress = person.getAddresses().size() == 1;

        if(isTheOnlyAddress) {
            var message = PersonBusinessRuleMessages.PERSON_HAS_ONLY_ONE_ADDRESS;
            result.addError(message);
        }
    }

    private void validateAddressDoesNotExist(Long addressId, Person person, ValidationResult result) {
        boolean addressDoesNotExist = person.getAddresses().stream().noneMatch(a -> a.getId().equals(addressId));

        if(addressDoesNotExist) {
            var message = String.format(EntityBusinessRuleMessages.ENTITY_NOT_FOUND_WITH_ID_FORMATTED_WITH_CLASS_NAME, "Address") + addressId;
            result.addError(message);
        }
    }

}
