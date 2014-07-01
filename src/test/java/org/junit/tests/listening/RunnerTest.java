package org.junit.tests.listening;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.testsupport.EventCollectorMatchers.hasNumberOfTestRunsFinished;
import static org.junit.testsupport.EventCollectorMatchers.hasNumberOfTestRunsStarted;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.testsupport.EventCollector;

public class RunnerTest {
    public static class Example {
        @Test
        public void empty() {
        }
    }

    @Test
    public void newTestCount() {
        final EventCollector eventCollector = runTest(Example.class);
        assertThat(eventCollector, hasNumberOfTestRunsStarted(1));
    }

    public static class ExampleTest extends TestCase {
        public void testEmpty() {
        }
    }

    @Test
    public void oldTestCount() {
        final EventCollector eventCollector = runTest(ExampleTest.class);
        assertThat(eventCollector, hasNumberOfTestRunsStarted(1));
    }

    public static class NewExample {
        @Test
        public void empty() {
        }
    }

    @Test
    public void testFinished() {
        final EventCollector eventCollector = runTest(NewExample.class);
        assertThat(eventCollector, hasNumberOfTestRunsFinished(1));
    }

    private EventCollector runTest(final Class<?> testClass) {
        final EventCollector eventCollector = new EventCollector();
        final JUnitCore runner = new JUnitCore();
        runner.addListener(eventCollector);
        runner.run(testClass);
        return eventCollector;
    }
}