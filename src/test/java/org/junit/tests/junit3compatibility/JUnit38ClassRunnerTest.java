package org.junit.tests.junit3compatibility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.testsupport.EventCollectorMatchers.hasNumberOfTestsStarted;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import junit.extensions.TestDecorator;
import junit.framework.JUnit4TestAdapter;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.Assert;
import org.junit.Test;
import org.junit.internal.runners.JUnit38ClassRunner;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.testsupport.EventCollector;

public class JUnit38ClassRunnerTest {
    public static class MyTest extends TestCase {
        public void testA() {

        }
    }

    @Test
    public void plansDecoratorCorrectly() {
        final JUnit38ClassRunner runner = new JUnit38ClassRunner(
                new TestDecorator(new TestSuite(MyTest.class)));
        assertEquals(1, runner.testCount());
    }

    public static class AnnotatedTest {
        @Test
        public void foo() {
            Assert.fail();
        }
    }

    @Test
    public void canUnadaptAnAdapter() {
        final JUnit38ClassRunner runner = new JUnit38ClassRunner(
                new JUnit4TestAdapter(AnnotatedTest.class));
        final Result result = new JUnitCore().run(runner);
        final Failure failure = result.getFailures().get(0);
        assertEquals(
                Description.createTestDescription(AnnotatedTest.class, "foo"),
                failure.getDescription());
    }

    static public class OneTest extends TestCase {
        public void testOne() {
        }
    }

    @Test
    public void testListener() throws Exception {
        final EventCollector eventCollector = new EventCollector();
        final JUnitCore runner = new JUnitCore();
        runner.addListener(eventCollector);
        final Result result = runner.run(OneTest.class);
        assertThat(eventCollector, hasNumberOfTestsStarted(1));
        assertEquals(
                Description.createTestDescription(OneTest.class, "testOne"),
                eventCollector.getTestsStarted().get(0));
        assertEquals(1, result.getRunCount());
    }

    public static class ClassWithInvalidMethod extends TestCase {
        @SuppressWarnings("unused")
        private void testInvalid() {
        }
    }

    @Test
    public void invalidTestMethodReportedCorrectly() {
        final Result result = JUnitCore
                .runClasses(ClassWithInvalidMethod.class);
        final Failure failure = result.getFailures().get(0);
        assertEquals("warning", failure.getDescription().getMethodName());
        assertEquals("junit.framework.TestSuite$1", failure.getDescription()
                .getClassName());
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public static @interface MyAnnotation {
    }

    public static class JUnit3ClassWithAnnotatedMethod extends TestCase {
        @MyAnnotation
        public void testAnnotated() {
        }

        public void testNotAnnotated() {
        }
    }

    public static class DerivedAnnotatedMethod extends
            JUnit3ClassWithAnnotatedMethod {
    }

    @Test
    public void getDescriptionWithAnnotation() {
        final JUnit38ClassRunner runner = new JUnit38ClassRunner(
                JUnit3ClassWithAnnotatedMethod.class);
        assertAnnotationFiltering(runner);
    }

    @Test
    public void getDescriptionWithAnnotationInSuper() {
        final JUnit38ClassRunner runner = new JUnit38ClassRunner(
                DerivedAnnotatedMethod.class);
        assertAnnotationFiltering(runner);
    }

    private void assertAnnotationFiltering(final JUnit38ClassRunner runner) {
        final Description d = runner.getDescription();
        assertEquals(2, d.testCount());
        for (final Description methodDesc : d.getChildren()) {
            if (methodDesc.getMethodName().equals("testAnnotated")) {
                assertNotNull(methodDesc.getAnnotation(MyAnnotation.class));
            } else {
                assertNull(methodDesc.getAnnotation(MyAnnotation.class));
            }
        }
    }

    public static class RejectAllTestsFilter extends Filter {
        @Override
        public boolean shouldRun(final Description description) {
            return description.isSuite();
        }

        @Override
        public String describe() {
            return "filter all";
        }
    }

    /**
     * Test that NoTestsRemainException is thrown when all methods have been
     * filtered.
     */
    @Test(expected = NoTestsRemainException.class)
    public void filterNoTestsRemain() throws NoTestsRemainException {
        final JUnit38ClassRunner runner = new JUnit38ClassRunner(OneTest.class);
        runner.filter(new RejectAllTestsFilter());
    }
}