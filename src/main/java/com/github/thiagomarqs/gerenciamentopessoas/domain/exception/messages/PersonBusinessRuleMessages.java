package com.github.thiagomarqs.gerenciamentopessoas.domain.exception.messages;

public class PersonBusinessRuleMessages {
    public static final String PERSON_IS_INACTIVE_FORMATTED = "Person %s is inactive and cannot be modified until it is reactivated.";
    public static final String PERSON_HAS_ONLY_ONE_ADDRESS = "This person has only one address.";
    public static final String CANT_CREATE_PERSON_WITHOUT_ADDRESS = "Cannot create a person without an address.";
}
