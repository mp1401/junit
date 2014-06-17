package org.junit.internal.runners.rules;

import org.junit.runners.model.FrameworkMember;

import java.util.List;

/**
 * Requires the validated member to be non-static
 */
class RequireDeclaredNonStaticStrategy extends ValidatorStrategy {
    RequireDeclaredNonStaticStrategy(ValidationErrorAdder fValidationErrorAdder) {
        super(fValidationErrorAdder);
    }

    @Override
    void addValidationErrorsForMemberToList(FrameworkMember<?> member, List<Throwable> errors) {
        if (member.isStatic()) {
            addError(errors, member, "must not be static or it has to be annotated with @ClassRule.");
        }
    }
}
