package com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.person;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;
import com.github.thiagomarqs.gerenciamentopessoas.domain.repository.PersonRepository;
import jakarta.inject.Inject;
import org.springframework.stereotype.Component;

@Component
public class EditPerson {

    private final PersonRepository personRepository;
    private final FindPeople findPeople;

    @Inject
    public EditPerson(PersonRepository personRepository, FindPeople findPeople) {
        this.personRepository = personRepository;
        this.findPeople = findPeople;
    }

    public Person edit(Long personId, Person edited) {

        var person = findPeople.findOne(personId);

        person.setFullName(edited.getFullName());
        person.setBirthDate(edited.getBirthDate());

        if(!edited.isActive()) {
            person.getAddresses().forEach(a -> a.setActive(false));
            person.setActive(false);
        }

        return personRepository.save(person);

    }

}
