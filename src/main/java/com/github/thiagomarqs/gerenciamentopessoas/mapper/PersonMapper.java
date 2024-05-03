package com.github.thiagomarqs.gerenciamentopessoas.mapper;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;
import com.github.thiagomarqs.gerenciamentopessoas.dto.person.EditPersonRequest;
import com.github.thiagomarqs.gerenciamentopessoas.dto.person.CreatePersonRequest;
import com.github.thiagomarqs.gerenciamentopessoas.dto.person.EditedPersonResponse;
import com.github.thiagomarqs.gerenciamentopessoas.dto.person.PersonResponse;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface PersonMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    Person createPersonRequestToPerson(CreatePersonRequest createPersonRequest);

    PersonResponse personToPersonResponse(Person person);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    @Mapping(target = "mainAddress", ignore = true)
    Person editPersonRequestToPerson(EditPersonRequest request);

    EditedPersonResponse personToEditedPersonResponse(Person edited);

    List<PersonResponse> personListToPersonResponseList(List<Person> people);

    @AfterMapping
    default void setPerson(@MappingTarget Person person) {
        if (person.getAddresses() != null) {
            person.getAddresses().forEach(address -> address.setPerson(person));
        }
        if(person.getMainAddress() != null) {
            person.getMainAddress().setPerson(person);
        }
    }
}
