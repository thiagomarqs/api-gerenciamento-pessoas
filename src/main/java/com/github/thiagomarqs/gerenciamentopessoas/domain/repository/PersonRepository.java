package com.github.thiagomarqs.gerenciamentopessoas.domain.repository;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    List<Person> findAllByActive(boolean active);

    List<Person> findAllByFullNameContainingIgnoreCase(String fullName);

    List<Person> findAllByAddresses_City(String city);

    @Query("SELECT p.mainAddress FROM Person p WHERE p.id = :id")
    Address findMainAddressById(Long id);
}
