package com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.address;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;
import com.github.thiagomarqs.gerenciamentopessoas.domain.repository.AddressRepository;
import com.github.thiagomarqs.gerenciamentopessoas.domain.repository.PersonRepository;
import com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.person.FindPeople;
import com.github.thiagomarqs.gerenciamentopessoas.domain.validator.usecase.address.RemoveAddressBusinessRuleValidator;
import jakarta.inject.Inject;
import org.springframework.stereotype.Component;

@Component
public class RemoveAddress {

    private final PersonRepository personRepository;
    private final FindPeople findPeople;
    private final AddressRepository addressRepository;
    private final RemoveAddressBusinessRuleValidator businessRuleValidator;

    @Inject
    public RemoveAddress(PersonRepository personRepository, FindPeople findPeople, AddressRepository addressRepository, RemoveAddressBusinessRuleValidator businessRuleValidator) {
        this.personRepository = personRepository;
        this.findPeople = findPeople;
        this.addressRepository = addressRepository;
        this.businessRuleValidator = businessRuleValidator;
    }

    public Person remove(Long personId, Long addressId) {
        var person = findPeople.findOne(personId);

        throwIfFailsValidation(addressId, person);
        deleteAddress(person, addressId);
        setMainAddressIfNecessary(person);

        return personRepository.save(person);
    }

    private void throwIfFailsValidation(Long addressId, Person person) {
        businessRuleValidator
                .validate(addressId, person)
                .throwIfHasErrors();
    }

    private void deleteAddress(Person person, Long addressId) {

        if(person.getMainAddress().getId().equals(addressId)) {
            person.setMainAddress(null);
        }

        person.getAddresses().removeIf(a -> a.getId().equals(addressId));

        addressRepository.deleteById(addressId);
    }

    private void setMainAddressIfNecessary(Person person) {
        if(person.getMainAddress() == null) {
            var newMainAddress = person.getAddresses().getFirst();
            person.setMainAddress(newMainAddress);
        }
    }

}
