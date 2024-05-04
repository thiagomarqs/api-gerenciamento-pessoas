package com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.address;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.BusinessRuleException;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.messages.AddressBusinessRuleMessages;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.messages.PersonBusinessRuleMessages;
import com.github.thiagomarqs.gerenciamentopessoas.domain.repository.AddressRepository;
import com.github.thiagomarqs.gerenciamentopessoas.validation.AddressValidator;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

@Component
public class EditAddress {

    private final AddressRepository addressRepository;
    private final FindAddresses findAddresses;
    private final AddressValidator addressValidator;

    @Inject
    public EditAddress(AddressRepository addressRepository, FindAddresses findAddresses, AddressValidator addressValidator) {
        this.addressRepository = addressRepository;
        this.findAddresses = findAddresses;
        this.addressValidator = addressValidator;
    }

    public Address edit(@NotNull Long addressId, @NotNull Address edited) {

        var address = findAddresses.findOne(addressId);
        var person = address.getPerson();

        throwIfInvalid(addressId, edited, person);

        boolean isDeactivatingMainAddress = !edited.isActive() && person.getMainAddress().getId().equals(addressId);

        if(isDeactivatingMainAddress) {
            automaticallyChangeMainAddressToRemainingActiveAddress(addressId, person);
        }

        updateAddressFields(edited, address);

        return addressRepository.save(address);

    }

    private void throwIfInvalid(Long addressId, Address edited, Person person) {
        throwIfInvalidWhenDeactivatingAddress(addressId, edited, person);
        addressValidator.throwIfInvalidCep(edited);
    }

    private void throwIfInvalidWhenDeactivatingAddress(Long addressId, Address edited, Person person) {
        boolean isTryingToDeactivateAddress = !edited.isActive();
        boolean isPersonMainAddress = person.getMainAddress().getId().equals(addressId);
        int addressCount = person.getAddresses().size();
        boolean areAllOtherAddressesDeactivated = person.getAddresses()
                .stream()
                .filter(a -> !a.getId().equals(addressId))
                .noneMatch(Address::isActive);

        if(isTryingToDeactivateAddress) {
            if(isPersonMainAddress && addressCount > 2) {
                throw new BusinessRuleException(AddressBusinessRuleMessages.CANT_DEACTIVATE_ADDRESS_WHEN_MORE_THAN_TWO_ADDRESSES_FORMATTED);
            }

            if(addressCount == 1) {
                throw new BusinessRuleException(PersonBusinessRuleMessages.PERSON_HAS_ONLY_ONE_ADDRESS);
            }

            if(areAllOtherAddressesDeactivated) {
                throw new BusinessRuleException(AddressBusinessRuleMessages.ONLY_ACTIVE_ADDRESS_FORMATTED);
            }
        }
    }

    private void automaticallyChangeMainAddressToRemainingActiveAddress(Long addressId, Person person) {
        var remainingActiveAddress = person.getAddresses()
                .stream()
                .filter(a -> !a.getId().equals(addressId))
                .findFirst()
                .get();

        person.setMainAddress(remainingActiveAddress);
    }

    private void updateAddressFields(Address edited, Address address) {
        if (edited.getAddress() != null) {
            address.setAddress(edited.getAddress());
        }
        if (edited.getCep() != null) {
            address.setCep(edited.getCep());
        }
        if (edited.getNumber() != null) {
            address.setNumber(edited.getNumber());
        }
        if (edited.getState() != null) {
            address.setState(edited.getState());
        }
        if (edited.getCity() != null) {
            address.setCity(edited.getCity());
        }
        if (edited.isActive() != null) {
            address.setActive(edited.isActive());
        }
    }
}
