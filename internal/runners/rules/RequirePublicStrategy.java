package org.junit.internal.runners.rules;

import org.junit.runners.model.FrameworkMember;

import java.util.List;

/**
 * Requires the member to be public
 */
class RequirePublicStrategy extends ValidatorStrategy {
    RequirePublicStrategy(ValidationErrorAdder fValidationErrorAdder) {
        super(fValidationErrorAdder);
    }

    @Override
    void addValidationErrorsForMemberToList(FrameworkMember<?> member, List<Throwable> errors) {
        if (!member.isPublic()) {
            addError(errors, member, "must be public.");
        }
    }
}
