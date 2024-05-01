package com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.person;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.EntityNotFoundException;
import com.github.thiagomarqs.gerenciamentopessoas.domain.repository.PersonRepository;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

@Component
public class FindPeople {

    private final PersonRepository personRepository;

    @Inject
    public FindPeople(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    Person findOne(@NotNull Long id) {
        return personRepository.findById(id).orElseThrow(() -> EntityNotFoundException.of(id, Person.class));
    }

    Iterable<Person> findMany(Iterable<Long> ids) {
        return personRepository.findAllById(ids);
    }

    Iterable<Person> findAll() {
        return personRepository.findAll();
    }

    Iterable<Person> findAllActive() {
        return personRepository.findAllByActiveTrue();
    }

    Iterable<Person> findAllInactive() {
        return personRepository.findAllByActiveFalse();
    }

    Iterable<Person> findAllByFullNameLike(@NotBlank String fullName) {
        return personRepository.findAllByFullNameContaining(fullName);
    }

    Iterable<Person> findAllByAddressCity(@NotBlank String city) {
        return personRepository.findAllByAddresses_City(city);
    }

}
