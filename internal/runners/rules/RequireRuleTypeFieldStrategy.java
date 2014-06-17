package org.junit.internal.runners.rules;

/**
 * Requires the member is a field implementing {@link org.junit.rules.MethodRule} or {@link org.junit.rules.TestRule}
 */
class RequireRuleTypeFieldStrategy extends RequireRuleTypeStrategy {
    RequireRuleTypeFieldStrategy(ValidationErrorAdder fValidationErrorAdder) {
        super(fValidationErrorAdder);
    }

    @Override
    protected String getErrorMessage() {
        return "must implement MethodRule or TestRule.";
    }
}
