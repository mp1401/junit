package org.junit.internal.runners.rules;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runners.model.FrameworkMember;
import org.junit.runners.model.TestClass;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import static org.junit.internal.runners.rules.RuleMemberValidator.Builder.classRuleValidator;
import static org.junit.internal.runners.rules.RuleMemberValidator.Builder.testRuleValidator;

/**
 * A RuleFieldValidator validates the rule fields/methods of a
 * {@link org.junit.runners.model.TestClass}. All reasons for rejecting the
 * {@code TestClass} are written to a list of errors.
 *
 * There are four slightly different validators. The {@link #CLASS_RULE_VALIDATOR}
 * validates fields with a {@link ClassRule} annotation and the
 * {@link #RULE_VALIDATOR} validates fields with a {@link Rule} annotation.
 *
 * The {@link #CLASS_RULE_METHOD_VALIDATOR}
 * validates methods with a {@link ClassRule} annotation and the
 * {@link #RULE_METHOD_VALIDATOR} validates methods with a {@link Rule} annotation.
 */
public class RuleMemberValidator {
    /**
     * Validates fields with a {@link ClassRule} annotation.
     */
    public static final RuleMemberValidator CLASS_RULE_VALIDATOR;
    /**
     * Validates fields with a {@link Rule} annotation.
     */
    public static final RuleMemberValidator RULE_VALIDATOR;
    /**
     * Validates methods with a {@link ClassRule} annotation.
     */
    public static final RuleMemberValidator CLASS_RULE_METHOD_VALIDATOR;
    /**
     * Validates methods with a {@link Rule} annotation.
     */
    public static final RuleMemberValidator RULE_METHOD_VALIDATOR;

    static {
        CLASS_RULE_VALIDATOR = classRuleValidator()
                .declaringClassMustBePublic()
                .mustBeStatic()
                .mustBePublicMember()
                .fieldMustBeARule()
                .build();
        RULE_VALIDATOR = testRuleValidator()
                .mustBeNonStatic()
                .mustBePublicMember()
                .fieldMustBeARule()
                .build();
        CLASS_RULE_METHOD_VALIDATOR = classRuleValidator()
                .forMethods()
                .declaringClassMustBePublic()
                .mustBeStatic()
                .mustBePublicMember()
                .methodMustBeARule()
                .build();
        RULE_METHOD_VALIDATOR = testRuleValidator()
                .forMethods()
                .mustBeNonStatic()
                .mustBePublicMember()
                .methodMustBeARule()
                .build();
    }

    private final Class<? extends Annotation> fAnnotation;
    private final boolean fMethods;
    private final List<ValidatorStrategy> fValidatorStrategies;

    RuleMemberValidator(Class<? extends Annotation> annotation,
                        boolean methods,
                        List<ValidatorStrategy> validatorStrategies) {
        this.fAnnotation = annotation;
        this.fMethods = methods;
        this.fValidatorStrategies = validatorStrategies;
    }

    /**
     * Validate the {@link org.junit.runners.model.TestClass} and adds reasons
     * for rejecting the class to a list of errors.
     *
     * @param target the {@code TestClass} to validate.
     * @param errors the list of errors.
     */
    public void validate(TestClass target, List<Throwable> errors) {
        List<? extends FrameworkMember<?>> members = fMethods ? target.getAnnotatedMethods(fAnnotation)
                : target.getAnnotatedFields(fAnnotation);

        for (FrameworkMember<?> each : members) {
            validateMember(each, errors);
        }
    }

    private void validateMember(FrameworkMember<?> member, List<Throwable> errors) {
        for (ValidatorStrategy strategy : fValidatorStrategies) {
            strategy.addValidationErrorsForMemberToList(member, errors);
        }
    }

    static class Builder {
        private final Class<? extends Annotation> fAnnotation;
        private boolean fMethods;
        private final List<ValidatorStrategy> fValidatorStrategies;
        private final ValidationErrorAdder fErrorAdder;

        private Builder(Class<? extends Annotation> fAnnotation, ValidationErrorAdder fErrorAdder) {
            this.fAnnotation = fAnnotation;
            this.fMethods = false;
            this.fErrorAdder = fErrorAdder;
            this.fValidatorStrategies = new ArrayList<ValidatorStrategy>();
        }

        static Builder classRuleValidator() {
            return new Builder(ClassRule.class, new ValidationErrorAdder(ClassRule.class));
        }

        static Builder testRuleValidator() {
            return new Builder(Rule.class, new ValidationErrorAdder(Rule.class));
        }

        Builder forMethods() {
            fMethods = true;
            return this;
        }

        Builder declaringClassMustBePublic() {
            fValidatorStrategies.add(new RequirePublicClassStrategy(fErrorAdder));
            return this;
        }

        Builder mustBeStatic() {
            fValidatorStrategies.add(new RequireDeclaredStaticStrategy(fErrorAdder));
            return this;
        }

        Builder mustBeNonStatic() {
            fValidatorStrategies.add(new RequireDeclaredNonStaticStrategy(fErrorAdder));
            return this;
        }

        Builder mustBePublicMember() {
            fValidatorStrategies.add(new RequirePublicStrategy(fErrorAdder));
            return this;
        }

        Builder fieldMustBeARule() {
            fValidatorStrategies.add(new RequireRuleTypeFieldStrategy(fErrorAdder));
            return this;
        }

        Builder methodMustBeARule() {
            fValidatorStrategies.add(new RequireRuleTypeMethodStrategy(fErrorAdder));
            return this;
        }

        RuleMemberValidator build() {
            return new RuleMemberValidator(fAnnotation, fMethods, fValidatorStrategies);
        }
    }
}
