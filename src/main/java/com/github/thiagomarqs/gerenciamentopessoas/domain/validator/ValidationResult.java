package com.github.thiagomarqs.gerenciamentopessoas.domain.validator;

import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.BusinessRuleException;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {

    private final List<String> errors = new ArrayList<>();

    public void addError(String errorMessage) {
        this.errors.add(errorMessage);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public List<String> getErrors() {
        return errors;
    }

    public void throwIfHasErrors() {
        if(this.hasErrors()) throw BusinessRuleException.ofMany(this.getErrors());
    }

}
