package com.github.thiagomarqs.gerenciamentopessoas.domain.repository;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends CrudRepository<Person, Long> {

    Iterable<Person> findAllByActiveTrue();

    Iterable<Person> findAllByActiveFalse();

    Iterable<Person> findAllByFullNameContaining(String fullName);

    Iterable<Person> findAllByAddresses_City(String city);
}
