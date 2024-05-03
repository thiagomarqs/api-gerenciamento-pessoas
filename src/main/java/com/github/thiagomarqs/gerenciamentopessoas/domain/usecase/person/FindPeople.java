package com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.person;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.EntityNotFoundException;
import com.github.thiagomarqs.gerenciamentopessoas.domain.repository.PersonRepository;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FindPeople {

    private final PersonRepository personRepository;

    @Inject
    public FindPeople(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public Person findOne(@NotNull Long id) {
        return personRepository.findById(id).orElseThrow(() -> EntityNotFoundException.of(id, Person.class));
    }

    public List<Person> findMany(Iterable<Long> ids) {
        return personRepository.findAllById(ids);
    }

    public List<Person> findAll() {
        return personRepository.findAll();
    }

    public List<Person> findAllByActive(boolean active) {
        return personRepository.findAllByActive(active);
    }

    public List<Person> findAllByFullNameLike(@NotBlank String fullName) {
        return personRepository.findAllByFullNameContainingIgnoreCase(fullName);
    }

    public List<Person> findAllByAddressCity(@NotBlank String city) {
        return personRepository.findAllByAddresses_City(city);
    }

}
