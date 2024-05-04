package com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.person;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.BusinessRuleException;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.messages.AddressBusinessRuleMessages;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.messages.PersonBusinessRuleMessages;
import com.github.thiagomarqs.gerenciamentopessoas.domain.repository.PersonRepository;
import com.github.thiagomarqs.gerenciamentopessoas.validation.AddressValidator;
import jakarta.inject.Inject;
import org.springframework.stereotype.Component;

@Component
public class CreatePerson {

    private final PersonRepository personRepository;
    private AddressValidator addressValidator;

    @Inject
    public CreatePerson(PersonRepository personRepository, AddressValidator addressValidator) {
        this.personRepository = personRepository;
        this.addressValidator = addressValidator;
    }

    public Person create(Person person) {

        throwIfInvalid(person);
        setMainAddress(person);

        return personRepository.save(person);
    }

    private void setMainAddress(Person person) {
        var hasMainAddressAlreadyBeenChosen = person.hasMainAddress();

        if(!hasMainAddressAlreadyBeenChosen) {
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

    private void throwIfInvalid(Person person) {
        throwIfNoAddresses(person);
        addressValidator.throwIfSomeInvalidCep(person.getAddresses());
        throwIfMoreThanOneMainAddress(person);
        throwIfCantSetMainAddressAutomatically(person);
    }

    private void throwIfMoreThanOneMainAddress(Person person) {
        var addresses = person.getAddresses();
        var mainAddresses = addresses.stream().filter(a -> a.getIsMain() != null && a.getIsMain()).count();

        if(mainAddresses > 1) {
            throw new BusinessRuleException(AddressBusinessRuleMessages.TRYING_TO_SET_MORE_THAN_ONE_MAIN_ADDRESS);
        }
    }

    private void throwIfNoAddresses(Person person) {
        if(person.getAddresses().isEmpty()) {
            throw new BusinessRuleException(PersonBusinessRuleMessages.CANT_CREATE_PERSON_WITHOUT_ADDRESS);
        }
    }

    private void throwIfCantSetMainAddressAutomatically(Person person) {
        var hasMainAddress = person.hasMainAddress();
        var hasMoreThanOneAddress = person.getAddresses().size() > 1;

        if (!hasMainAddress && hasMoreThanOneAddress) {
            throw new BusinessRuleException(AddressBusinessRuleMessages.CANT_SET_MAIN_ADDRESS_WHEN_MORE_THAN_ONE_ADDRESS);
        }
    }

}
