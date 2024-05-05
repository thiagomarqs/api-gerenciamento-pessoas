package com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.person;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.EntityNotFoundException;
import com.github.thiagomarqs.gerenciamentopessoas.domain.repository.PersonRepository;
import com.github.thiagomarqs.gerenciamentopessoas.domain.validator.usecase.person.SetMainAddressBusinessRuleValidator;
import jakarta.inject.Inject;
import org.springframework.stereotype.Component;

@Component
public class SetMainAddress {

    private final PersonRepository personRepository;
    private final FindPeople findPeople;
    private final SetMainAddressBusinessRuleValidator businessRuleValidator;

    @Inject
    public SetMainAddress(PersonRepository personRepository, FindPeople findPeople, SetMainAddressBusinessRuleValidator businessRuleValidator) {
        this.personRepository = personRepository;
        this.findPeople = findPeople;
        this.businessRuleValidator = businessRuleValidator;
    }

    public Person set(Long personId, Long newMainAddressId) {

        var person = findPeople.findOne(personId);
        var newMainAddress = getNewMainAddress(newMainAddressId, person);

        throwIfFailsValidation(person, newMainAddress);

        person.setMainAddress(newMainAddress);

        return personRepository.save(person);
    }

    private void throwIfFailsValidation(Person person, Address newMainAddress) {
        businessRuleValidator
                .validate(person, newMainAddress)
                .throwIfHasErrors();
    }

    private Address getNewMainAddress(Long newMainAddressId, Person person) {
        return person.getAddresses()
                .stream()
                .filter(a -> a.getId().equals(newMainAddressId)).findFirst()
                .orElseThrow(() -> EntityNotFoundException.of(newMainAddressId, Address.class));
    }

}
