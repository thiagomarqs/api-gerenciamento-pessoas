package com.github.thiagomarqs.gerenciamentopessoas.validation;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import com.github.thiagomarqs.gerenciamentopessoas.integration.address.AddressFinder;
import jakarta.inject.Inject;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AddressValidator {

    @Inject
    AddressFinder addressFinder;

    public AddressValidationResult validateMany(List<Address> addresses) {
        var ceps = addresses.stream().map(Address::getCep).toArray(String[]::new);
        return validateManyByCep(ceps);
    }

    public AddressValidationResult validateManyByCep(String[] ceps) {
        var result = new AddressValidationResult();

        for (var cep : ceps) {
            var isValid = validateByCep(cep);
            if(!isValid) result.addValidationFailure(cep);
        }

        return result;
    }

    public boolean validateByCep(String cep) {
        try {
            var address = addressFinder.findAddressByCep(cep);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

}
