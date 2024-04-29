package com.github.thiagomarqs.gerenciamentopessoas.domain.exception;

public class BusinessRuleMessages {

    public static final String ENTITY_NOT_FOUND_WITH_ID = "Entity not found with ID ";
    public static final String ENTITY_NOT_FOUND_WITH_ID_FORMATTED_WITH_CLASS_NAME = "%s not found with ID ";
    public static final String CANT_SET_MAIN_ADDRESS_WHEN_MORE_THAN_ONE_ADDRESS = "Can't set the main address when the person has more than one address.";
    public static final String CANT_REMOVE_ADDRESS_WHEN_MORE_THAN_TWO_ADDRESSES_FORMATTED = "Failed to delete address of ID %s, can't automatically set the main address because this person has more than two addresses. Please set another address as the main address before deleting this one.";
    public static final String ADDRESS_BELONGS_TO_ANOTHER_USER = "Address already belongs to someone else.";
    public static final String ADDRESS_IS_INACTIVE_FORMATTED = "Address %s is inactive, please use an active address.";
    public static final String PERSON_IS_INACTIVE_FORMATTED = "Person %s is inactive and cannot be modified until it is reactivated.";
    public static final String DUPLICATED_ADDRESS = "This address is already registered for this person.";
    public static final String PERSON_HAS_ONLY_ONE_ADDRESS = "This person has only one address.";
    public static final String CANT_DEACTIVATE_ADDRESS_WHEN_MORE_THAN_TWO_ADDRESSES_FORMATTED = "Failed to deactivate address of ID %s, can't automatically set the main address because this person has more than two addresses. Please set another address as the main address before deactivating this one." ;
    public static final String ONLY_ACTIVE_ADDRESS_FORMATTED = "Address %s is the only active address for this person. Please activate another address before deactivating this one.";

}