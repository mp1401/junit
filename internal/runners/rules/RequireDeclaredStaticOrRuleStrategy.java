package org.junit.internal.runners.rules;

import org.junit.Rule;
import org.junit.runners.model.FrameworkMember;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Requires the member to be either static or annotated as {@link org.junit.Rule}
 */
class RequireDeclaredStaticOrRuleStrategy extends ValidatorStrategy {
    RequireDeclaredStaticOrRuleStrategy(ValidationErrorAdder fValidationErrorAdder) {
        super(fValidationErrorAdder);
    }

    @Override
    public void addValidationErrorsForMemberToList(FrameworkMember<?> member, List<Throwable> errors) {
        if (!member.isStatic() && !isTestRule(member)) {
            addError(errors, member, "must be static.");
        }
    }

    private boolean isTestRule(FrameworkMember<?> member) {
        for (Annotation annotation : member.getAnnotations()) {
            if (Rule.class.equals(annotation.annotationType())) {
                return true;
            }
        }
        return false;
    }
}
