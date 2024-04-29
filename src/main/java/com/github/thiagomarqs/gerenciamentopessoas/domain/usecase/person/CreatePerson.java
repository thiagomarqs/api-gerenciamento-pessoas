package com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.person;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.BusinessRuleException;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.BusinessRuleMessages;
import com.github.thiagomarqs.gerenciamentopessoas.domain.repository.PersonRepository;
import jakarta.inject.Inject;
import org.springframework.stereotype.Component;

@Component
public class CreatePerson {

    private final PersonRepository personRepository;

    @Inject
    public CreatePerson(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public Person create(Person person) {

        var mainAddress = person.getMainAddress();
        var hasMoreThanOneAddress = person.getAddresses().size() > 1;

        if (mainAddress == null && hasMoreThanOneAddress) {
            throw new BusinessRuleException(BusinessRuleMessages.CANT_SET_MAIN_ADDRESS_WHEN_MORE_THAN_ONE_ADDRESS);
        }

        Address firstAddress = person.getAddresses().getFirst();
        person.setMainAddress(firstAddress);

        return personRepository.save(person);
    }

}
