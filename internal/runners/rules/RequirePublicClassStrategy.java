package org.junit.internal.runners.rules;

import org.junit.runners.model.FrameworkMember;

import java.lang.reflect.Modifier;
import java.util.List;

/**
 * Requires the member's declaring class to be public
 */
class RequirePublicClassStrategy extends ValidatorStrategy {
    RequirePublicClassStrategy(ValidationErrorAdder fValidationErrorAdder) {
        super(fValidationErrorAdder);
    }

    @Override
    public void addValidationErrorsForMemberToList(FrameworkMember<?> member, List<Throwable> errors) {
        if (!isDeclaringClassPublic(member)) {
            addError(errors, member, " must be declared in a public class.");
        }
    }

    private boolean isDeclaringClassPublic(FrameworkMember<?> member) {
        return Modifier.isPublic(member.getDeclaringClass().getModifiers());
    }
}
