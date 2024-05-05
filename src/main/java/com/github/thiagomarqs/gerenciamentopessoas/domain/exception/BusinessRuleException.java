package com.github.thiagomarqs.gerenciamentopessoas.domain.exception;

public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String s) {
        super(s);
    }

    public static BusinessRuleException ofMany(Iterable<String> messages) {
        var message = String.join(";", messages);
        return new BusinessRuleException(message);
    }

}
