package org.junit.runner.notification;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.testsupport.EventCollectorMatchers.hasNumberOfTestsStarted;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.testsupport.EventCollector;

/**
 * Testing RunNotifier in concurrent access.
 * 
 * @author Tibor Digana (tibor17)
 * @version 4.12
 * @since 4.12
 */
public final class ConcurrentRunNotifierTest {
    private static final long TIMEOUT = 3;

    private final RunNotifier fNotifier = new RunNotifier();

    @Test
    public void realUsage() throws Exception {
        final EventCollector listener1 = new EventCollector();
        final EventCollector listener2 = new EventCollector();
        fNotifier.addListener(listener1);
        fNotifier.addListener(listener2);

        final int numParallelTests = 4;
        final ExecutorService pool = Executors
                .newFixedThreadPool(numParallelTests);
        for (int i = 0; i < numParallelTests; ++i) {
            pool.submit(new Runnable() {
                public void run() {
                    fNotifier.fireTestStarted(null);
                }
            });
        }
        pool.shutdown();
        assertTrue(pool.awaitTermination(TIMEOUT, TimeUnit.SECONDS));

        fNotifier.removeListener(listener1);
        fNotifier.removeListener(listener2);

        assertThat(listener1, hasNumberOfTestsStarted(numParallelTests));
        assertThat(listener2, hasNumberOfTestsStarted(numParallelTests));
    }

    private static class ExaminedListener extends RunListener {
        final boolean throwFromTestStarted;

        volatile boolean hasTestFailure = false;

        ExaminedListener(final boolean throwFromTestStarted) {
            this.throwFromTestStarted = throwFromTestStarted;
        }

        @Override
        public void testStarted(final Description description) throws Exception {
            if (throwFromTestStarted) {
                throw new Exception();
            }
        }

        @Override
        public void testFailure(final Failure failure) throws Exception {
            hasTestFailure = true;
        }
    }

    private abstract class AbstractConcurrentFailuresTest {

        protected abstract void addListener(ExaminedListener listener);

        public void test() throws Exception {
            int totalListenersFailures = 0;

            final Random random = new Random(42);
            final ExaminedListener[] examinedListeners = new ExaminedListener[1000];
            for (int i = 0; i < examinedListeners.length; ++i) {
                final boolean fail = random.nextDouble() >= 0.5d;
                if (fail) {
                    ++totalListenersFailures;
                }
                examinedListeners[i] = new ExaminedListener(fail);
            }

            final AtomicBoolean condition = new AtomicBoolean(true);
            final CyclicBarrier trigger = new CyclicBarrier(2);
            final CountDownLatch latch = new CountDownLatch(10);

            final ExecutorService notificationsPool = Executors
                    .newFixedThreadPool(4);
            notificationsPool.submit(new Callable<Void>() {
                public Void call() throws Exception {
                    trigger.await();
                    while (condition.get()) {
                        fNotifier.fireTestStarted(null);
                        latch.countDown();
                    }
                    fNotifier.fireTestStarted(null);
                    return null;
                }
            });

            // Wait for callable to start
            trigger.await(TIMEOUT, TimeUnit.SECONDS);

            // Wait for callable to fire a few events
            latch.await(TIMEOUT, TimeUnit.SECONDS);

            for (final ExaminedListener examinedListener : examinedListeners) {
                addListener(examinedListener);
            }

            notificationsPool.shutdown();
            condition.set(false);
            assertTrue(notificationsPool.awaitTermination(TIMEOUT,
                    TimeUnit.SECONDS));

            if (totalListenersFailures != 0) {
                // If no listener failures, then all the listeners do not report
                // any failure.
                final int countTestFailures = examinedListeners.length
                        - countReportedTestFailures(examinedListeners);
                assertThat(totalListenersFailures, is(countTestFailures));
            }
        }
    }

    /**
     * Verifies that listeners added while tests are run concurrently are
     * notified about test failures.
     */
    @Test
    public void reportConcurrentFailuresAfterAddListener() throws Exception {
        new AbstractConcurrentFailuresTest() {
            @Override
            protected void addListener(final ExaminedListener listener) {
                fNotifier.addListener(listener);
            }
        }.test();
    }

    /**
     * Verifies that listeners added with addFirstListener() while tests are run
     * concurrently are notified about test failures.
     */
    @Test
    public void reportConcurrentFailuresAfterAddFirstListener()
            throws Exception {
        new AbstractConcurrentFailuresTest() {
            @Override
            protected void addListener(final ExaminedListener listener) {
                fNotifier.addFirstListener(listener);
            }
        }.test();
    }

    private static int countReportedTestFailures(
            final ExaminedListener[] listeners) {
        int count = 0;
        for (final ExaminedListener listener : listeners) {
            if (listener.hasTestFailure) {
                ++count;
            }
        }
        return count;
    }
}