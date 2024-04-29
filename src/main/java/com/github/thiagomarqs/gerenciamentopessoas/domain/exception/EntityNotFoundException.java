package com.github.thiagomarqs.gerenciamentopessoas.domain.exception;

import com.github.thiagomarqs.gerenciamentopessoas.domain.exception.messages.EntityBusinessRuleMessages;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(Long id) {
        super(EntityBusinessRuleMessages.ENTITY_NOT_FOUND_WITH_ID + id);
    }

    public EntityNotFoundException(Long id, String className) {
        super(String.format(EntityBusinessRuleMessages.ENTITY_NOT_FOUND_WITH_ID_FORMATTED_WITH_CLASS_NAME, className) + id);
    }

    public static RuntimeException of(Long id, Class clazz) {
        var className = clazz.getSimpleName();
        return new EntityNotFoundException(id, className);
    }

    public static RuntimeException of(Long id) {
        return new EntityNotFoundException(id);
    }
}
