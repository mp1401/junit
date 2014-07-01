package org.junit.tests.running.classes;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.testsupport.EventCollectorMatchers.*;

import java.util.List;

import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.internal.AssumptionViolatedException;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerScheduler;
import org.junit.tests.experimental.rules.RuleMemberValidatorTest.TestWithNonStaticClassRule;
import org.junit.tests.experimental.rules.RuleMemberValidatorTest.TestWithProtectedClassRule;
import org.junit.testsupport.EventCollector;

public class ParentRunnerTest {
    public static String log = "";

    public static class FruitTest {
        @Test
        public void apple() {
            log += "apple ";
        }

        @Test
        public void /* must hash-sort after "apple" */Banana() {
            log += "banana ";
        }
    }

    @Test
    public void useChildHarvester() throws InitializationError {
        log = "";
        final ParentRunner<?> runner = new BlockJUnit4ClassRunner(
                FruitTest.class);
        runner.setScheduler(new RunnerScheduler() {
            public void schedule(final Runnable childStatement) {
                log += "before ";
                childStatement.run();
                log += "after ";
            }

            public void finished() {
                log += "afterAll ";
            }
        });

        runner.run(new RunNotifier());
        assertEquals("before apple after before banana after afterAll ", log);
    }

    @Test
    public void testMultipleFilters() throws Exception {
        final JUnitCore junitCore = new JUnitCore();
        final Request request = Request.aClass(ExampleTest.class);
        final Request requestFiltered = request
                .filterWith(new Exclude("test1"));
        final Request requestFilteredFiltered = requestFiltered
                .filterWith(new Exclude("test2"));
        final Result result = junitCore.run(requestFilteredFiltered);
        assertThat(result.getFailures(), isEmpty());
        assertEquals(1, result.getRunCount());
    }

    private Matcher<List<?>> isEmpty() {
        return new TypeSafeMatcher<List<?>>() {
            public void describeTo(final org.hamcrest.Description description) {
                description.appendText("is empty");
            }

            @Override
            public boolean matchesSafely(final List<?> item) {
                return item.size() == 0;
            }
        };
    }

    private static class Exclude extends Filter {
        private final String methodName;

        public Exclude(final String methodName) {
            this.methodName = methodName;
        }

        @Override
        public boolean shouldRun(final Description description) {
            return !description.getMethodName().equals(methodName);
        }

        @Override
        public String describe() {
            return "filter method name: " + methodName;
        }
    }

    public static class ExampleTest {
        @Test
        public void test1() throws Exception {
        }

        @Test
        public void test2() throws Exception {
        }

        @Test
        public void test3() throws Exception {
        }
    }

    @Test
    public void failWithHelpfulMessageForProtectedClassRule() {
        assertClassHasFailureMessage(TestWithProtectedClassRule.class,
                "The @ClassRule 'temporaryFolder' must be public.");
    }

    @Test
    public void failWithHelpfulMessageForNonStaticClassRule() {
        assertClassHasFailureMessage(TestWithNonStaticClassRule.class,
                "The @ClassRule 'temporaryFolder' must be static.");
    }

    static class NonPublicTestClass {
        public NonPublicTestClass() {
        }
    }

    @Test
    public void cannotBeCreatedWithNonPublicTestClass() {
        assertClassHasFailureMessage(
                NonPublicTestClass.class,
                "The class org.junit.tests.running.classes.ParentRunnerTest$NonPublicTestClass is not public.");
    }

    private void assertClassHasFailureMessage(final Class<?> klass,
            final String message) {
        final JUnitCore junitCore = new JUnitCore();
        final Request request = Request.aClass(klass);
        final Result result = junitCore.run(request);
        assertThat(result.getFailureCount(), is(2)); // the second failure is no
                                                     // runnable methods
        assertThat(result.getFailures().get(0).getMessage(),
                is(equalTo(message)));
    }

    public static class AssertionErrorAtParentLevelTest {
        @BeforeClass
        public static void beforeClass() throws Throwable {
            throw new AssertionError("Thrown from @BeforeClass");
        }

        @Test
        public void test() {
        }
    }

    @Test
    public void assertionErrorAtParentLevelTest() throws InitializationError {
        final EventCollector collector = runTestWithParentRunner(AssertionErrorAtParentLevelTest.class);
        assertThat(
                collector,
                allOf(hasNoAssumptionFailure(), hasSingleFailure(),
                        hasNumberOfTestsIgnored(0),
                        hasNumberOfTestsFinished(0), hasNumberOfTestsStarted(0)));
    }

    public static class AssumptionViolatedAtParentLevelTest {
        @BeforeClass
        public static void beforeClass() {
            throw new AssumptionViolatedException("Thrown from @BeforeClass");
        }

        @Test
        public void test() {
        }
    }

    @Test
    public void assumptionViolatedAtParentLevel() throws InitializationError {
        final EventCollector collector = runTestWithParentRunner(AssumptionViolatedAtParentLevelTest.class);
        assertThat(
                collector,
                allOf(hasSingleAssumptionFailure(), hasNoFailure(),
                        hasNumberOfTestsIgnored(0),
                        hasNumberOfTestsFinished(0), hasNumberOfTestsStarted(0)));
    }

    public static class TestTest {
        @Test
        public void pass() {
        }

        @Test
        public void fail() {
            throw new AssertionError("Thrown from @Test");
        }

        @Ignore
        @Test
        public void ignore() {
        }

        @Test
        public void assumptionFail() {
            throw new AssumptionViolatedException("Thrown from @Test");
        }
    }

    @Test
    public void parentRunnerTestMethods() throws InitializationError {
        final EventCollector collector = runTestWithParentRunner(TestTest.class);
        assertThat(
                collector,
                allOf(hasSingleAssumptionFailure(), hasSingleFailure(),
                        hasNumberOfTestsIgnored(1),
                        hasNumberOfTestsFinished(3), hasNumberOfTestsStarted(3)));
    }

    private EventCollector runTestWithParentRunner(final Class<?> testClass)
            throws InitializationError {
        final EventCollector collector = new EventCollector();
        final RunNotifier runNotifier = new RunNotifier();
        runNotifier.addListener(collector);
        final ParentRunner runner = new BlockJUnit4ClassRunner(testClass);
        runner.run(runNotifier);
        return collector;
    }
}