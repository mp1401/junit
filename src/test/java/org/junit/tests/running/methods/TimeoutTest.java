package org.junit.tests.running.methods;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import junit.framework.JUnit4TestAdapter;
import junit.framework.TestResult;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

public class TimeoutTest {

    public static class FailureWithTimeoutTest {
        @Test(timeout = 1000)
        public void failure() {
            fail();
        }
    }

    @Test
    public void failureWithTimeout() throws Exception {
        final JUnitCore core = new JUnitCore();
        final Result result = core.run(FailureWithTimeoutTest.class);
        assertEquals(1, result.getRunCount());
        assertEquals(1, result.getFailureCount());
        assertEquals(AssertionError.class, result.getFailures().get(0)
                .getException().getClass());
    }

    public static class FailureWithTimeoutRunTimeExceptionTest {
        @Test(timeout = 1000)
        public void failure() {
            throw new NullPointerException();
        }
    }

    @Test
    public void failureWithTimeoutRunTimeException() throws Exception {
        final JUnitCore core = new JUnitCore();
        final Result result = core
                .run(FailureWithTimeoutRunTimeExceptionTest.class);
        assertEquals(1, result.getRunCount());
        assertEquals(1, result.getFailureCount());
        assertEquals(NullPointerException.class, result.getFailures().get(0)
                .getException().getClass());
    }

    public static class SuccessWithTimeoutTest {
        @Test(timeout = 1000)
        public void success() {
        }
    }

    @Test
    public void successWithTimeout() throws Exception {
        final JUnitCore core = new JUnitCore();
        final Result result = core.run(SuccessWithTimeoutTest.class);
        assertEquals(1, result.getRunCount());
        assertEquals(0, result.getFailureCount());
    }

    public static class TimeoutFailureTest {
        @Test(timeout = 100)
        public void success() throws InterruptedException {
            Thread.sleep(40000);
        }
    }

    @Ignore("was breaking gump")
    @Test
    public void timeoutFailure() throws Exception {
        final JUnitCore core = new JUnitCore();
        final Result result = core.run(TimeoutFailureTest.class);
        assertEquals(1, result.getRunCount());
        assertEquals(1, result.getFailureCount());
        assertEquals(InterruptedException.class, result.getFailures().get(0)
                .getException().getClass());
    }

    public static class InfiniteLoopTest {
        @Test(timeout = 100)
        public void failure() {
            infiniteLoop();
        }

        private void infiniteLoop() {
            for (;;) {
                try {
                    Thread.sleep(10);
                } catch (final InterruptedException e) {
                }
            }
        }
    }

    @Test
    public void infiniteLoop() throws Exception {
        final JUnitCore core = new JUnitCore();
        final Result result = core.run(InfiniteLoopTest.class);
        assertEquals(1, result.getRunCount());
        assertEquals(1, result.getFailureCount());
        final Throwable exception = result.getFailures().get(0).getException();
        assertTrue(exception.getMessage().contains(
                "test timed out after 100 milliseconds"));
    }

    public static class ImpatientLoopTest {
        @Test(timeout = 1)
        public void failure() {
            infiniteLoop();
        }

        private void infiniteLoop() {
            for (;;)
                ;
        }
    }

    @Ignore("This breaks sporadically with time differences just slightly more than 200ms")
    @Test
    public void infiniteLoopRunsForApproximatelyLengthOfTimeout()
            throws Exception {
        // "prime the pump": running these beforehand makes the runtimes more
        // predictable
        // (because of class loading?)
        JUnitCore.runClasses(InfiniteLoopTest.class, ImpatientLoopTest.class);
        final long longTime = runAndTime(InfiniteLoopTest.class);
        final long shortTime = runAndTime(ImpatientLoopTest.class);
        final long difference = longTime - shortTime;
        assertTrue(String.format("Difference was %sms", difference),
                difference < 200);
    }

    private long runAndTime(final Class<?> clazz) {
        final JUnitCore core = new JUnitCore();
        final long startTime = System.currentTimeMillis();
        core.run(clazz);
        final long totalTime = System.currentTimeMillis() - startTime;
        return totalTime;
    }

    private String stackForException(final Throwable exception) {
        final Writer buffer = new StringWriter();
        final PrintWriter writer = new PrintWriter(buffer);
        exception.printStackTrace(writer);
        return buffer.toString();
    }

    @Test
    public void stalledThreadAppearsInStackTrace() throws Exception {
        final JUnitCore core = new JUnitCore();
        final Result result = core.run(InfiniteLoopTest.class);
        assertEquals(1, result.getRunCount());
        assertEquals(1, result.getFailureCount());
        final Throwable exception = result.getFailures().get(0).getException();
        assertThat(stackForException(exception), containsString("infiniteLoop")); // Make
                                                                                  // sure
                                                                                  // we
                                                                                  // have
                                                                                  // the
                                                                                  // stalled
                                                                                  // frame
                                                                                  // on
                                                                                  // the
                                                                                  // stack
                                                                                  // somewhere
    }

    public static class InfiniteLoopMultithreaded {

        private static class ThreadTest implements Runnable {
            private boolean fStall;

            public ThreadTest(final boolean stall) {
                fStall = stall;
            }

            public void run() {
                if (fStall)
                    for (;;)
                        ;
                try {
                    Thread.sleep(500);
                } catch (final InterruptedException e) {
                }
            }
        }

        public void failure(final boolean mainThreadStalls) throws Exception {
            final Thread t1 = new Thread(new ThreadTest(false), "timeout-thr1");
            final Thread t2 = new Thread(new ThreadTest(!mainThreadStalls),
                    "timeout-thr2");
            final Thread t3 = new Thread(new ThreadTest(false), "timeout-thr3");
            t1.start();
            t2.start();
            t3.start();
            if (mainThreadStalls)
                for (;;)
                    ;
            t1.join();
            t2.join();
            t3.join();
        }
    }

    public static class InfiniteLoopWithStuckThreadTest {
        @Rule
        public TestRule globalTimeout = new Timeout(100, TimeUnit.MILLISECONDS)
                .lookForStuckThread(true);

        @Test
        public void failure() throws Exception {
            (new InfiniteLoopMultithreaded()).failure(false);
        }
    }

    public static class InfiniteLoopStuckInMainThreadTest {
        @Rule
        public TestRule globalTimeout = new Timeout(100, TimeUnit.MILLISECONDS)
                .lookForStuckThread(true);

        @Test
        public void failure() throws Exception {
            (new InfiniteLoopMultithreaded()).failure(true);
        }
    }

    @Test
    public void timeoutFailureMultithreaded() throws Exception {
        final JUnitCore core = new JUnitCore();
        final Result result = core.run(InfiniteLoopWithStuckThreadTest.class);
        assertEquals(1, result.getRunCount());
        assertEquals(2, result.getFailureCount());
        final Throwable exception[] = new Throwable[2];
        for (int i = 0; i < 2; i++)
            exception[i] = result.getFailures().get(i).getException();
        assertThat(exception[0].getMessage(),
                containsString("test timed out after 100 milliseconds"));
        assertThat(stackForException(exception[0]),
                containsString("Thread.join"));
        assertThat(exception[1].getMessage(),
                containsString("Appears to be stuck in thread timeout-thr2"));
    }

    @Test
    public void timeoutFailureMultithreadedStuckInMain() throws Exception {
        final JUnitCore core = new JUnitCore();
        final Result result = core.run(InfiniteLoopStuckInMainThreadTest.class);
        assertEquals(1, result.getRunCount());
        assertEquals(1, result.getFailureCount());
        final Throwable exception = result.getFailures().get(0).getException();
        assertThat(exception.getMessage(),
                containsString("test timed out after 100 milliseconds"));
        assertThat(exception.getMessage(),
                not(containsString("Appears to be stuck")));
    }

    public static class LockedWithDeadlockTest {
        @Rule
        public TestRule globalTimeout = new Timeout(100, TimeUnit.MILLISECONDS)
                .lookForStuckThread(true).printFullThreadStackDump(true);

        final ReentrantLock lock1 = new ReentrantLock();

        final ReentrantLock lock2 = new ReentrantLock();

        private class LockedThread1 extends Thread {

            public LockedThread1() {
                super("Thread-locked-1");
            }

            @Override
            public void run() {
                lock1.lock();
                try {
                    Thread.sleep(50);
                } catch (final InterruptedException e) {
                    // Ignore
                }
                lock2.lock();
            }
        }

        private class LockedThread2 extends Thread {

            public LockedThread2() {
                super("Thread-locked-2");
            }

            @Override
            public void run() {
                lock2.lock();
                try {
                    Thread.sleep(50);
                } catch (final InterruptedException e) {
                    // Ignore
                }
                lock1.lock();
            }
        }

        @Test
        public void failure() throws Exception {
            final LockedThread1 thread1 = new LockedThread1();
            final LockedThread2 thread2 = new LockedThread2();

            thread1.start();
            thread2.start();

            thread1.join();
            thread2.join();
        }
    }

    @Test
    public void timeoutFailureMultithreadedDeadlockWithFullDump()
            throws Exception {
        final JUnitCore core = new JUnitCore();
        final Result result = core.run(LockedWithDeadlockTest.class);
        assertEquals(1, result.getRunCount());
        assertEquals(2, result.getFailureCount());
        final Throwable exception[] = new Throwable[2];
        for (int i = 0; i < 2; i++)
            exception[i] = result.getFailures().get(i).getException();
        assertThat(exception[0].getMessage(),
                containsString("test timed out after 100 milliseconds"));
        assertThat(stackForException(exception[0]),
                containsString("Thread.join"));
        assertThat(exception[1].getMessage(),
                containsString("Appears to be stuck => Full thread dump"));
        // TODO: this is really not nice and safe
        assertTrue(
                "Failed to find Thread-locked-1 information in full thread dump:\n"
                        + exception[1].getMessage(),
                exception[1]
                        .getMessage()
                        .matches(
                                "(?s).*\"Thread-locked-1\".*State=WAITING.*at .*ReentrantLock\\.lock.*at org\\.junit\\.tests\\.running\\.methods\\.TimeoutTest\\$LockedWithDeadlockTest\\$LockedThread1\\.run.*"));
        assertTrue(
                "Failed to find Thread-locked-2 information in full thread dump:\n"
                        + exception[1].getMessage(),
                exception[1]
                        .getMessage()
                        .matches(
                                "(?s).*\"Thread-locked-2\".*State=WAITING.*at .*ReentrantLock\\.lock.*at org\\.junit\\.tests\\.running\\.methods\\.TimeoutTest\\$LockedWithDeadlockTest\\$LockedThread2\\.run.*"));
    }

    @Test
    public void compatibility() {
        final TestResult result = new TestResult();
        new JUnit4TestAdapter(InfiniteLoopTest.class).run(result);
        assertEquals(1, result.errorCount());
    }

    public static class WillTimeOut {
        static boolean afterWasCalled = false;

        @Test(timeout = 1)
        public void test() {
            for (;;) {
                try {
                    Thread.sleep(10000);
                } catch (final InterruptedException e) {
                    // ok, tests are over
                }
            }
        }

        @After
        public void after() {
            afterWasCalled = true;
        }
    }

    @Test
    public void makeSureAfterIsCalledAfterATimeout() {
        JUnitCore.runClasses(WillTimeOut.class);
        assertThat(WillTimeOut.afterWasCalled, is(true));
    }
}