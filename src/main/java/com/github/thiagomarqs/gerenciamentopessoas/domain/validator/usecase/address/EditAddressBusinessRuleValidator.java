package com.github.thiagomarqs.gerenciamentopessoas.domain.validator.usecase.address;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.messages.AddressBusinessRuleMessages;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.messages.PersonBusinessRuleMessages;
import com.github.thiagomarqs.gerenciamentopessoas.domain.validator.ValidationResult;
import com.github.thiagomarqs.gerenciamentopessoas.domain.validator.address.AddressValidator;
import jakarta.inject.Inject;
import org.springframework.stereotype.Component;

@Component
public class EditAddressBusinessRuleValidator {

    private final AddressValidator addressValidator;

    @Inject
    public EditAddressBusinessRuleValidator(AddressValidator addressValidator) {
        this.addressValidator = addressValidator;
    }

    public ValidationResult validate(Address edited) {
        var result = new ValidationResult();

        validateIfInvalidWhenDeactivatingAddress(edited, result);
        addressValidator.validateInvalidCep(edited, result);

        return result;
    }

    private void validateIfInvalidWhenDeactivatingAddress(Address edited, ValidationResult result) {
        var isTryingToDeactivateAddress = !edited.getActive();

        if (isTryingToDeactivateAddress) {
            validateIfCantDeactivateMainAddressWhenThereAreMoreThanTwoAddresses(edited, result);
            validateIfInvalidWhenDeactivatingLastAddress(edited, result);
            validateIfInvalidWhenAllOtherAddressesAreAlreadyDeactivated(edited, result);
        }
    }

    private void validateIfCantDeactivateMainAddressWhenThereAreMoreThanTwoAddresses(Address edited, ValidationResult result) {
        var person = edited.getPerson();
        var addressId = edited.getId();
        boolean isPersonMainAddress = person.getMainAddress().getId().equals(addressId);
        int addressCount = person.getAddresses().size();

        if(isPersonMainAddress && addressCount > 2) {
            var message = AddressBusinessRuleMessages.CANT_DEACTIVATE_ADDRESS_WHEN_MORE_THAN_TWO_ADDRESSES_FORMATTED;
            result.addError(message);
        }
    }

    private void validateIfInvalidWhenDeactivatingLastAddress(Address edited, ValidationResult result) {
        var person = edited.getPerson();
        int addressCount = person.getAddresses().size();

        if(addressCount == 1) {
            var message = PersonBusinessRuleMessages.PERSON_HAS_ONLY_ONE_ADDRESS;
            result.addError(message);
        }
    }

    private void validateIfInvalidWhenAllOtherAddressesAreAlreadyDeactivated(Address edited, ValidationResult result) {
        var person = edited.getPerson();
        var addressId = edited.getId();
        boolean areAllOtherAddressesDeactivated = areAllOtherAddressesDeactivated(person, addressId);

        if(areAllOtherAddressesDeactivated) {
            var message = AddressBusinessRuleMessages.ONLY_ACTIVE_ADDRESS_FORMATTED;
            result.addError(message);
        }
    }

    private boolean areAllOtherAddressesDeactivated(Person person, Long addressId) {
        return person.getAddresses()
                .stream()
                .filter(a -> !a.getId().equals(addressId))
                .noneMatch(Address::getActive);
    }

}
