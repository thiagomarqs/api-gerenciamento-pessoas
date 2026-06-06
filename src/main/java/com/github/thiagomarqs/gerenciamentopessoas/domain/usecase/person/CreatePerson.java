package com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.person;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;
import com.github.thiagomarqs.gerenciamentopessoas.domain.repository.PersonRepository;
import com.github.thiagomarqs.gerenciamentopessoas.domain.validator.usecase.person.CreatePersonBusinessRuleValidator;
import com.github.thiagomarqs.gerenciamentopessoas.domain.validator.usecase.professionaldata.ProfessionalDataBusinessRuleValidator;
import jakarta.inject.Inject;
import org.springframework.stereotype.Component;

@Component
public class CreatePerson {

    private final PersonRepository personRepository;
    private final CreatePersonBusinessRuleValidator businessRuleValidator;
    private final ProfessionalDataBusinessRuleValidator professionalDataBusinessRuleValidator;

    @Inject
    public CreatePerson(PersonRepository personRepository, CreatePersonBusinessRuleValidator businessRuleValidator, ProfessionalDataBusinessRuleValidator professionalDataBusinessRuleValidator) {
        this.personRepository = personRepository;
        this.businessRuleValidator = businessRuleValidator;
        this.professionalDataBusinessRuleValidator = professionalDataBusinessRuleValidator;
    }

    public Person create(Person person) {

        throwIfFailsValidation(person);
        throwIfFailsProfessionalDataValidation(person);
        setMainAddress(person);

        return personRepository.save(person);
    }

    private void throwIfFailsValidation(Person person) {
        businessRuleValidator
                .validate(person)
                .throwIfHasErrors();
    }

    private void throwIfFailsProfessionalDataValidation(Person person) {
        professionalDataBusinessRuleValidator
                .validate(person.getProfessionalData())
                .throwIfHasErrors();
    }

    private void setMainAddress(Person person) {
        var hasMainAddressBeenChosenAlready = person.hasMainAddress();

        if(!hasMainAddressBeenChosenAlready) {
            setAddressAutomatically(person);
            return;
        }

        var newMainAddress = person.getAddresses().stream().filter(Address::getIsMain).findFirst().get();

        person.setMainAddress(newMainAddress);
    }

    private void setAddressAutomatically(Person person) {
        Address firstAddress = person.getAddresses().getFirst();
        person.setMainAddress(firstAddress);
    }

}