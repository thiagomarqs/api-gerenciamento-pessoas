package com.github.thiagomarqs.gerenciamentopessoas.domain.exception.messages;

public class AddressBusinessRuleMessages {
    public static final String CANT_SET_MAIN_ADDRESS_WHEN_MORE_THAN_ONE_ADDRESS = "Can't automatically set the main address when the person has more than one address.";
    public static final String CANT_REMOVE_MAIN_ADDRESS_WHEN_MORE_THAN_TWO_ADDRESSES_FORMATTED = "Failed to delete address of ID %s, can't automatically set the main address because this person has more than two addresses. Please set another address as the main address before deleting this one.";
    public static final String ADDRESS_BELONGS_TO_ANOTHER_USER = "Address already belongs to someone else.";
    public static final String ADDRESS_IS_INACTIVE_FORMATTED = "Address %s is inactive, please use an active address.";
    public static final String DUPLICATED_ADDRESS = "This address is already registered for this person.";
    public static final String CANT_DEACTIVATE_ADDRESS_WHEN_MORE_THAN_TWO_ADDRESSES_FORMATTED = "Failed to deactivate address of ID %s, can't automatically set the main address because this person has more than two addresses. Please set another address as the main address before deactivating this one." ;
    public static final String ONLY_ACTIVE_ADDRESS_FORMATTED = "Address %s is the only active address for this person. Please activate another address before deactivating this one.";
    public static final String TRYING_TO_SET_MORE_THAN_ONE_MAIN_ADDRESS = "More than one address was defined as the main one. Please set only one address as the main address and try again.";
    public static final String INVALID_CEPS_FORMATTED = "The following CEPs are invalid: %s.";
}
