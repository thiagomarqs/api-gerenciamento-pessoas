package com.github.thiagomarqs.gerenciamentopessoas.validation;

import java.util.ArrayList;
import java.util.List;

public class AddressValidationResult {

    List<String> invalidCeps = new ArrayList<>();

    public void addValidationFailure(String cep) {
        this.invalidCeps.add(cep);
    }

    public boolean hasFailures() {
        return !invalidCeps.isEmpty();
    }

    public List<String> getInvalidCeps() {
        return invalidCeps;
    }

}
