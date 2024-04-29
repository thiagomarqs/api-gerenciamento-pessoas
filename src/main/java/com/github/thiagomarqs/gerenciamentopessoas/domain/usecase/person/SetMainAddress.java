package com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.person;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.BusinessRuleException;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.BusinessRuleMessages;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.EntityNotFoundException;
import com.github.thiagomarqs.gerenciamentopessoas.domain.repository.AddressRepository;
import com.github.thiagomarqs.gerenciamentopessoas.domain.repository.PersonRepository;
import jakarta.inject.Inject;
import org.springframework.stereotype.Component;

@Component
public class SetMainAddress {

    private final PersonRepository personRepository;
    private final AddressRepository addressRepository;
    private final FindPeople findPeople;

    @Inject
    public SetMainAddress(PersonRepository personRepository, AddressRepository addressRepository, FindPeople findPeople) {
        this.personRepository = personRepository;
        this.addressRepository = addressRepository;
        this.findPeople = findPeople;
    }

    public Person set(Long personId, Long newMainAddressId) {

        var person = findPeople.findOne(personId);
        var newMainAddress = addressRepository.findById(newMainAddressId).orElseThrow(() -> EntityNotFoundException.of(newMainAddressId));

        throwIfInactivePerson(person);
        throwIfInactiveAddress(newMainAddress);
        throwIfSomeoneElsesAddress(person, newMainAddress);

        person.setMainAddress(newMainAddress);

        return personRepository.save(person);
    }

    private void throwIfInactivePerson(Person person) {
        if(!person.isActive()) {
            throw new BusinessRuleException(String.format(BusinessRuleMessages.PERSON_IS_INACTIVE_FORMATTED, person.getId()));
        }
    }

    private void throwIfInactiveAddress(Address address) {
        if(!address.isActive()) {
            throw new BusinessRuleException(String.format(BusinessRuleMessages.ADDRESS_IS_INACTIVE_FORMATTED, address.getAddress()));
        }
    }

    private void throwIfSomeoneElsesAddress(Person person, Address address) {
        boolean isSomeoneElsesAddress = address.getPerson() != null && !address.getPerson().equals(person);

        if(isSomeoneElsesAddress) {
            throw new BusinessRuleException(BusinessRuleMessages.ADDRESS_BELONGS_TO_ANOTHER_USER);
        }
    }

}
