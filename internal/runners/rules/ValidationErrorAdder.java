package org.junit.internal.runners.rules;

import org.junit.runners.model.FrameworkMember;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Adds a validation error to a list of existing errors
 */
class ValidationErrorAdder {
    private final Class<? extends Annotation> fAnnotation;

    ValidationErrorAdder(Class<? extends Annotation> annotation) {
        this.fAnnotation = annotation;
    }

    void addError(List<Throwable> errors, FrameworkMember<?> member, String suffix) {
        String message = "The @" + fAnnotation.getSimpleName() + " '"
                + member.getName() + "' " + suffix;
        errors.add(new Exception(message));
    }
}
