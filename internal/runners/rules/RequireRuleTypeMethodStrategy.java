package org.junit.internal.runners.rules;

/**
 * Require the member to return an implementation of {@link org.junit.rules.MethodRule} or
 * {@link org.junit.rules.TestRule}
 */
class RequireRuleTypeMethodStrategy extends RequireRuleTypeStrategy {
    RequireRuleTypeMethodStrategy(ValidationErrorAdder fValidationErrorAdder) {
        super(fValidationErrorAdder);
    }

    @Override
    protected String getErrorMessage() {
        return "must return an implementation of MethodRule or TestRule.";
    }
}
