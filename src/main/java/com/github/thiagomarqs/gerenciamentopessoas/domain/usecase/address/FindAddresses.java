package com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.address;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.EntityNotFoundException;
import com.github.thiagomarqs.gerenciamentopessoas.domain.repository.AddressRepository;
import com.github.thiagomarqs.gerenciamentopessoas.domain.repository.PersonRepository;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FindAddresses {

    private final AddressRepository addressRepository;
    private final PersonRepository personRepository;

    @Inject
    public FindAddresses(AddressRepository addressRepository, PersonRepository personRepository) {
        this.addressRepository = addressRepository;
        this.personRepository = personRepository;
    }

    public Address findOne(@NotNull Long addressId) {
        return addressRepository.findById(addressId).orElseThrow(() -> EntityNotFoundException.of(addressId, Address.class));
    }

    public List<Address> findAll(@NotNull Long personId) {
        return addressRepository.findAllByPersonId(personId);
    }

    public List<Address> findAllByActive(@NotNull Long personId, @NotNull boolean active) {
        return addressRepository.findAllByPersonIdAndActive(personId, active);
    }

    public List<Address> findAllByAddressLike(@NotNull Long personId, @NotBlank String address) {
        return addressRepository.findAllByPersonIdAndAddressContainingIgnoreCase(personId, address);
    }

    public Address findMainAddress(@NotNull Long personId) {
        return personRepository.findMainAddressById(personId);
    }
}
