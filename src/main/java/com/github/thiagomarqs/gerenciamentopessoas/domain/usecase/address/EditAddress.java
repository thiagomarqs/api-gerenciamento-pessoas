package com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.address;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;
import com.github.thiagomarqs.gerenciamentopessoas.domain.repository.AddressRepository;
import com.github.thiagomarqs.gerenciamentopessoas.domain.validator.usecase.address.EditAddressBusinessRuleValidator;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

@Component
public class EditAddress {

    private final AddressRepository addressRepository;
    private final FindAddresses findAddresses;
    private final EditAddressBusinessRuleValidator businessRuleValidator;

    @Inject
    public EditAddress(AddressRepository addressRepository, FindAddresses findAddresses, EditAddressBusinessRuleValidator businessRuleValidator) {
        this.addressRepository = addressRepository;
        this.findAddresses = findAddresses;
        this.businessRuleValidator = businessRuleValidator;
    }

    public Address edit(@NotNull Long addressId, @NotNull Address edited) {

        var address = findAddresses.findOne(addressId);
        var person = address.getPerson();

        edited.setId(addressId);
        edited.setPerson(person);

        throwIfFailsValidation(edited);
        automaticallyChangeMainAddressIfDeactivatingMainAddress(edited);
        updateAddressFields(edited, address);

        return addressRepository.save(address);

    }

    private void throwIfFailsValidation(Address edited) {
        businessRuleValidator
                .validate(edited)
                .throwIfHasErrors();
    }

    private void automaticallyChangeMainAddressIfDeactivatingMainAddress(Address edited) {
        var person = edited.getPerson();
        var addressId = edited.getId();
        boolean isDeactivatingMainAddress = !edited.getActive() && person.getMainAddress().getId().equals(addressId);

        if(isDeactivatingMainAddress) {
            automaticallyChangeMainAddressToRemainingActiveAddress(addressId, person);
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
        if (edited.getActive() != null) {
            address.setActive(edited.getActive());
        }
    }
}
