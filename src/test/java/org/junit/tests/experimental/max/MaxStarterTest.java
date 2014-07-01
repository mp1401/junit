package org.junit.tests.experimental.max;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.max.MaxCore;
import org.junit.internal.runners.JUnit38ClassRunner;
import org.junit.runner.Computer;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.notification.Failure;
import org.junit.tests.AllTests;
import org.junit.testsupport.EventCollector;

public class MaxStarterTest {
    private MaxCore fMax;

    private File fMaxFile;

    @Before
    public void createMax() {
        fMaxFile = new File("MaxCore.ser");
        if (fMaxFile.exists()) {
            fMaxFile.delete();
        }
        fMax = MaxCore.storedLocally(fMaxFile);
    }

    @After
    public void forgetMax() {
        fMaxFile.delete();
    }

    public static class TwoTests {
        @Test
        public void succeed() {
        }

        @Test
        public void dontSucceed() {
            fail();
        }
    }

    @Test
    public void twoTestsNotRunComeBackInRandomOrder() {
        final Request request = Request.aClass(TwoTests.class);
        final List<Description> things = fMax.sortedLeavesForTest(request);
        final Description succeed = Description.createTestDescription(TwoTests.class,
                "succeed");
        final Description dontSucceed = Description.createTestDescription(
                TwoTests.class, "dontSucceed");
        assertTrue(things.contains(succeed));
        assertTrue(things.contains(dontSucceed));
        assertEquals(2, things.size());
    }

    @Test
    public void preferNewTests() {
        final Request one = Request.method(TwoTests.class, "succeed");
        fMax.run(one);
        final Request two = Request.aClass(TwoTests.class);
        final List<Description> things = fMax.sortedLeavesForTest(two);
        final Description dontSucceed = Description.createTestDescription(
                TwoTests.class, "dontSucceed");
        assertEquals(dontSucceed, things.get(0));
        assertEquals(2, things.size());
    }

    // This covers a seemingly-unlikely case, where you had a test that failed
    // on the
    // last run and you also introduced new tests. In such a case it pretty much
    // doesn't matter
    // which order they run, you just want them both to be early in the sequence
    @Test
    public void preferNewTestsOverTestsThatFailed() {
        final Request one = Request.method(TwoTests.class, "dontSucceed");
        fMax.run(one);
        final Request two = Request.aClass(TwoTests.class);
        final List<Description> things = fMax.sortedLeavesForTest(two);
        final Description succeed = Description.createTestDescription(TwoTests.class,
                "succeed");
        assertEquals(succeed, things.get(0));
        assertEquals(2, things.size());
    }

    @Test
    public void preferRecentlyFailed() {
        final Request request = Request.aClass(TwoTests.class);
        fMax.run(request);
        final List<Description> tests = fMax.sortedLeavesForTest(request);
        final Description dontSucceed = Description.createTestDescription(
                TwoTests.class, "dontSucceed");
        assertEquals(dontSucceed, tests.get(0));
    }

    @Test
    public void sortTestsInMultipleClasses() {
        final Request request = Request.classes(Computer.serial(), TwoTests.class,
                TwoTests.class);
        fMax.run(request);
        final List<Description> tests = fMax.sortedLeavesForTest(request);
        final Description dontSucceed = Description.createTestDescription(
                TwoTests.class, "dontSucceed");
        assertEquals(dontSucceed, tests.get(0));
        assertEquals(dontSucceed, tests.get(1));
    }

    public static class TwoUnEqualTests {
        @Test
        public void slow() throws InterruptedException {
            Thread.sleep(100);
            fail();
        }

        @Test
        public void fast() {
            fail();
        }

    }

    @Test
    public void rememberOldRuns() {
        fMax.run(TwoUnEqualTests.class);

        final MaxCore reincarnation = MaxCore.storedLocally(fMaxFile);
        final List<Failure> failures = reincarnation.run(TwoUnEqualTests.class)
                .getFailures();
        assertEquals("fast", failures.get(0).getDescription().getMethodName());
        assertEquals("slow", failures.get(1).getDescription().getMethodName());
    }

    @Test
    public void preferFast() {
        final Request request = Request.aClass(TwoUnEqualTests.class);
        fMax.run(request);
        final Description thing = fMax.sortedLeavesForTest(request).get(1);
        assertEquals(Description.createTestDescription(TwoUnEqualTests.class,
                "slow"), thing);
    }

    @Test
    public void listenersAreCalledCorrectlyInTheFaceOfFailures()
            throws Exception {
        final EventCollector listener = new EventCollector();
        final JUnitCore core = new JUnitCore();
        core.addListener(listener);
        fMax.run(Request.aClass(TwoTests.class), core);
        assertEquals(1, listener.getTestRunsFinished().get(0).getFailureCount());
    }

    @Test
    public void testsAreOnlyIncludedOnceWhenExpandingForSorting()
            throws Exception {
        final Result result = fMax.run(Request.aClass(TwoTests.class));
        assertEquals(2, result.getRunCount());
    }

    public static class TwoOldTests extends TestCase {
        public void testOne() {
        }

        public void testTwo() {
        }
    }

    @Test
    public void junit3TestsAreRunOnce() throws Exception {
        final Result result = fMax.run(Request.aClass(TwoOldTests.class),
                new JUnitCore());
        assertEquals(2, result.getRunCount());
    }

    @Test
    public void filterSingleMethodFromOldTestClass() throws Exception {
        final Description method = Description.createTestDescription(
                TwoOldTests.class, "testOne");
        final Filter filter = Filter.matchMethodDescription(method);
        final JUnit38ClassRunner child = new JUnit38ClassRunner(TwoOldTests.class);
        child.filter(filter);
        assertEquals(1, child.testCount());
    }

    @Test
    public void testCountsStandUpToFiltration() {
        assertFilterLeavesTestUnscathed(AllTests.class);
    }

    private void assertFilterLeavesTestUnscathed(final Class<?> testClass) {
        final Request oneClass = Request.aClass(testClass);
        final Request filtered = oneClass.filterWith(new Filter() {
            @Override
            public boolean shouldRun(final Description description) {
                return true;
            }

            @Override
            public String describe() {
                return "Everything";
            }
        });

        final int filterCount = filtered.getRunner().testCount();
        final int coreCount = oneClass.getRunner().testCount();
        assertEquals("Counts match up in " + testClass, coreCount, filterCount);
    }

    private static class MalformedJUnit38Test {
        private MalformedJUnit38Test() {
        }

        @SuppressWarnings("unused")
        public void testSucceeds() {
        }
    }

    @Test
    public void maxShouldSkipMalformedJUnit38Classes() {
        final Request request = Request.aClass(MalformedJUnit38Test.class);
        fMax.run(request);
    }

    public static class MalformedJUnit38TestMethod extends TestCase {
        @SuppressWarnings("unused")
        private void testNothing() {
        }
    }

    @Test
    public void correctErrorFromMalformedTest() {
        final Request request = Request.aClass(MalformedJUnit38TestMethod.class);
        final JUnitCore core = new JUnitCore();
        final Request sorted = fMax.sortRequest(request);
        final Runner runner = sorted.getRunner();
        final Result result = core.run(runner);
        final Failure failure = result.getFailures().get(0);
        assertThat(failure.toString(),
                containsString("MalformedJUnit38TestMethod"));
        assertThat(failure.toString(), containsString("testNothing"));
        assertThat(failure.toString(), containsString("isn't public"));
    }

    public static class HalfMalformedJUnit38TestMethod extends TestCase {
        public void testSomething() {
        }

        @SuppressWarnings("unused")
        private void testNothing() {
        }
    }

    @Test
    public void halfMalformed() {
        assertThat(JUnitCore.runClasses(HalfMalformedJUnit38TestMethod.class)
                .getFailureCount(), is(1));
    }

    @Test
    public void correctErrorFromHalfMalformedTest() {
        final Request request = Request.aClass(HalfMalformedJUnit38TestMethod.class);
        final JUnitCore core = new JUnitCore();
        final Request sorted = fMax.sortRequest(request);
        final Runner runner = sorted.getRunner();
        final Result result = core.run(runner);
        final Failure failure = result.getFailures().get(0);
        assertThat(failure.toString(),
                containsString("MalformedJUnit38TestMethod"));
        assertThat(failure.toString(), containsString("testNothing"));
        assertThat(failure.toString(), containsString("isn't public"));
    }
}