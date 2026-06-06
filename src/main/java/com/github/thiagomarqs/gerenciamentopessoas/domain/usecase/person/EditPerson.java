package com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.person;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.ProfessionalData;
import com.github.thiagomarqs.gerenciamentopessoas.domain.repository.PersonRepository;
import com.github.thiagomarqs.gerenciamentopessoas.domain.validator.usecase.professionaldata.ProfessionalDataBusinessRuleValidator;
import jakarta.inject.Inject;
import org.springframework.stereotype.Component;

@Component
public class EditPerson {

    private final PersonRepository personRepository;
    private final FindPeople findPeople;
    private final ProfessionalDataBusinessRuleValidator professionalDataValidator;

    @Inject
    public EditPerson(PersonRepository personRepository, FindPeople findPeople, ProfessionalDataBusinessRuleValidator professionalDataValidator) {
        this.personRepository = personRepository;
        this.findPeople = findPeople;
        this.professionalDataValidator = professionalDataValidator;
    }

    public Person edit(Long personId, Person edited) {

        var person = findPeople.findOne(personId);

        if(!edited.getFullName().isBlank()) {
            person.setFullName(edited.getFullName());
        }

        if(edited.getBirthDate() != null) {
            person.setBirthDate(edited.getBirthDate());
        }

        if(!edited.isActive()) {
            person.getAddresses().forEach(a -> a.setActive(false));
            person.setActive(false);
        }

        if(!person.isActive() && edited.isActive()) {
            person.getAddresses().forEach(a -> a.setActive(true));
            person.setActive(true);
        }

        handleProfessionalData(person, edited);

        return personRepository.save(person);

    }

    private void handleProfessionalData(Person person, Person edited) {
        var editedProfessionalData = edited.getProfessionalData();

        if (editedProfessionalData != null) {
            validateProfessionalData(editedProfessionalData);
            updateProfessionalData(person, editedProfessionalData);
        } else {
            removeProfessionalData(person);
        }
    }

    private void removeProfessionalData(Person person) {
        person.setProfessionalData(null);
    }

    private void validateProfessionalData(ProfessionalData professionalData) {
        var result = professionalDataValidator.validate(professionalData);
        result.throwIfHasErrors();
    }

    private void updateProfessionalData(Person person, ProfessionalData editedProfessionalData) {
        var existingProfessionalData = person.getProfessionalData();

        if (existingProfessionalData != null) {
            mergeProfessionalData(existingProfessionalData, editedProfessionalData);
        } else {
            editedProfessionalData.setPerson(person);
            person.setProfessionalData(editedProfessionalData);
        }
    }

    private void mergeProfessionalData(ProfessionalData existing, ProfessionalData edited) {
        if (edited.getCompanyName() != null) {
            existing.setCompanyName(edited.getCompanyName());
        }
        if (edited.getContractType() != null) {
            existing.setContractType(edited.getContractType());
        }
        if (edited.getEmploymentStartDate() != null) {
            existing.setEmploymentStartDate(edited.getEmploymentStartDate());
        }
    }

}
