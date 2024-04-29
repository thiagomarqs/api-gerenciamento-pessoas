package com.github.thiagomarqs.gerenciamentopessoas.domain.usecase.address;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.EntityNotFoundException;
import com.github.thiagomarqs.gerenciamentopessoas.domain.repository.AddressRepository;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

@Component
public class FindAddresses {

    private final AddressRepository addressRepository;

    @Inject
    public FindAddresses(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public Address findOne(@NotNull Long addressId) {
        return addressRepository.findById(addressId).orElseThrow(() -> EntityNotFoundException.of(addressId, Address.class));
    }

    public Iterable<Address> findAll(@NotNull Long personId) {
        return addressRepository.findAllByPersonId(personId);
    }

    public Iterable<Address> findAllActive(@NotNull Long personId) {
        return addressRepository.findAllActiveByPersonId(personId);
    }

    public Iterable<Address> findAllInactive(@NotNull Long personId) {
        return addressRepository.findAllInactiveByPersonId(personId);
    }

    public Iterable<Address> findAllByAddressLike(@NotNull Long personId, @NotBlank String address) {
        return addressRepository.findAllByPersonIdAndAddressContaining(personId, address);
    }

}
