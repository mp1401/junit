package org.junit.internal.runners.rules;

import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;
import org.junit.runners.model.FrameworkMember;

import java.util.List;

/**
 * Base class for requiring the member to be of the type or return either {@link org.junit.rules.MethodRule} or
 * {@link org.junit.rules.TestRule}
 */
abstract class RequireRuleTypeStrategy extends ValidatorStrategy {
    RequireRuleTypeStrategy(ValidationErrorAdder fValidationErrorAdder) {
        super(fValidationErrorAdder);
    }

    @Override
    void addValidationErrorsForMemberToList(FrameworkMember<?> member, List<Throwable> errors) {
        if (!isMethodRule(member) && !isTestRule(member)) {
            addError(errors, member, getErrorMessage());
        }
    }

    protected abstract String getErrorMessage();

    protected boolean isTestRule(FrameworkMember<?> member) {
        return TestRule.class.isAssignableFrom(member.getType());
    }

    protected boolean isMethodRule(FrameworkMember<?> member) {
        return MethodRule.class.isAssignableFrom(member.getType());
    }
}
