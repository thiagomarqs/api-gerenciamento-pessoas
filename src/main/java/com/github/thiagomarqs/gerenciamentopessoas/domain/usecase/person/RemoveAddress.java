package com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.person;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Person;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.BusinessRuleException;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.EntityNotFoundException;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.messages.AddressBusinessRuleMessages;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.messages.PersonBusinessRuleMessages;
import com.github.thiagomarqs.gerenciamentopessoas.domain.repository.AddressRepository;
import com.github.thiagomarqs.gerenciamentopessoas.domain.repository.PersonRepository;
import jakarta.inject.Inject;
import org.springframework.stereotype.Component;

@Component
public class RemoveAddress {

    private final PersonRepository personRepository;
    private final FindPeople findPeople;
    private final AddressRepository addressRepository;

    @Inject
    public RemoveAddress(PersonRepository personRepository, FindPeople findPeople, AddressRepository addressRepository) {
        this.personRepository = personRepository;
        this.findPeople = findPeople;
        this.addressRepository = addressRepository;
    }

    public Person remove(Long personId, Long addressId) {
        var person = findPeople.findOne(personId);

        throwIfFailsValidation(addressId, person);

        Address addressToRemove = person.getAddresses().stream()
            .filter(a -> a.getId().equals(addressId))
            .findFirst()
            .orElseThrow(() -> EntityNotFoundException.of(addressId, Address.class));

        person.getAddresses().remove(addressToRemove);

        var newMainAddress = person.getAddresses().getFirst();

        person.setMainAddress(newMainAddress);

        addressRepository.delete(addressToRemove);

        return personRepository.save(person);
    }

    private static void throwIfFailsValidation(Long addressId, Person person) {
        boolean existentAddress = person.getAddresses().stream().anyMatch(a -> a.getId().equals(addressId));

        if(!existentAddress) {
            throw EntityNotFoundException.of(addressId, Address.class);
        }

        boolean isTheOnlyAddress = person.getAddresses().size() == 1;

        if(isTheOnlyAddress) {
            throw new BusinessRuleException(PersonBusinessRuleMessages.PERSON_HAS_ONLY_ONE_ADDRESS);
        }

        boolean hasOnlyTwoAddresses = person.getAddresses().size() == 2;

        if(!hasOnlyTwoAddresses) {
            String message = String.format(AddressBusinessRuleMessages.CANT_REMOVE_ADDRESS_WHEN_MORE_THAN_TWO_ADDRESSES_FORMATTED, addressId);
            throw new BusinessRuleException(message);
        }
    }
}
