package com.github.thiagomarqs.gerenciamentopessoas.domain.validator;

import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.BusinessRuleException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ValidationResult {

    private List<String> errors = new ArrayList<>();

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

    public <T extends Exception> void throwIfHasErrors(Function<List<String>, T> exceptionFunction) throws T {
        if(this.hasErrors()) throw exceptionFunction.apply(this.getErrors());
    }
}
