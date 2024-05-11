package com.github.thiagomarqs.gerenciamentopessoas.domain.validator.address;

import com.github.thiagomarqs.gerenciamentopessoas.domain.entity.Address;
import com.github.thiagomarqs.gerenciamentopessoas.integration.address.AddressFinder;
import jakarta.inject.Inject;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AddressValidator {

    @Inject
    AddressFinder addressFinder;

    public AddressValidationResult validateManyByCep(List<Address> addresses) {
        var ceps = addresses.stream().map(Address::getCep).toArray(String[]::new);
        return validateManyCeps(ceps);
    }

    public boolean validateOneByCep(Address edited) {
        var cep = edited.getCep();
        return validateByCep(cep);
    }

    private AddressValidationResult validateManyCeps(String[] ceps) {
        var result = new AddressValidationResult();

        for (var cep : ceps) {
            var isValid = validateByCep(cep);
            if(!isValid) result.addValidationFailure(cep);
        }

        return result;
    }

    private boolean validateByCep(String cep) {
        var isPatternValid = validateCepPattern(cep);
        var finderResult = addressFinder.findAddressByCep(cep);
        return finderResult.getIsSuccessful() && isPatternValid;
    }

    private boolean validateCepPattern(String cep) {
        return cep.matches("\\d{5}-\\d{3}");
    }

}
