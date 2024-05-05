package com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.address;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import com.github.thiagomarqs.gerenciamentopessoas.domain.repository.AddressRepository;
import com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.person.FindPeople;
import com.github.thiagomarqs.gerenciamentopessoas.domain.validator.usecase.address.AddAddressBusinessRuleValidator;
import jakarta.inject.Inject;
import org.springframework.stereotype.Component;

@Component
public class AddAddress {

    private final AddressRepository addressRepository;
    private final FindPeople findPeople;
    private final AddAddressBusinessRuleValidator businessRuleValidator;

    @Inject
    public AddAddress(AddressRepository addressRepository, FindPeople findPeople, AddAddressBusinessRuleValidator businessRuleValidator) {
        this.addressRepository = addressRepository;
        this.findPeople = findPeople;
        this.businessRuleValidator = businessRuleValidator;
    }

    public Address add(Long personId, Address newAddress) {

        var person = findPeople.findOne(personId);

        newAddress.setPerson(person);

        throwIfFailsValidation(newAddress);

        return addressRepository.save(newAddress);
    }

    private void throwIfFailsValidation(Address newAddress) {
        businessRuleValidator
                .validate(newAddress)
                .throwIfHasErrors();
    }
}