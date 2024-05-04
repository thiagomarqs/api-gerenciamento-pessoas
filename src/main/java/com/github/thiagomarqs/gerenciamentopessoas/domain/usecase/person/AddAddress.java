package com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.person;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.BusinessRuleException;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.messages.AddressBusinessRuleMessages;
import com.github.thiagomarqs.gerenciamentopessoas.domain.repository.AddressRepository;
import com.github.thiagomarqs.gerenciamentopessoas.validation.AddressValidator;
import jakarta.inject.Inject;
import org.springframework.stereotype.Component;

@Component
public class AddAddress {

    private final AddressRepository addressRepository;
    private final FindPeople findPeople;
    private final AddressValidator addressValidator;

    @Inject
    public AddAddress(AddressRepository addressRepository, FindPeople findPeople, AddressValidator addressValidator) {
        this.addressRepository = addressRepository;
        this.findPeople = findPeople;
        this.addressValidator = addressValidator;
    }

    public Address add(Long personId, Address newAddress) {

        var person = findPeople.findOne(personId);

        throwIfInvalid(newAddress, person);

        newAddress.setPerson(person);

        return addressRepository.save(newAddress);
    }

    private void throwIfInvalid(Address newAddress, Person person) {
        throwIfDuplicatedAddress(newAddress, person);
        addressValidator.throwIfInvalidCep(newAddress);
    }


    private void throwIfDuplicatedAddress(Address newAddress, Person person) {
        boolean isDuplicate = person.getAddresses().stream().anyMatch(a ->
                a.getAddress().equals(newAddress.getAddress()) &&
                a.getCep().equals(newAddress.getCep()) &&
                a.getNumber().equals(newAddress.getNumber()) &&
                a.getCity().equals(newAddress.getCity()) &&
                a.getState().equals(newAddress.getState())
        );

        if(isDuplicate) {
            throw new BusinessRuleException(AddressBusinessRuleMessages.DUPLICATED_ADDRESS);
        }
    }

}
