package org.junit.testsupport;

import static java.util.Collections.synchronizedList;
import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

/**
 * A {@link org.junit.runner.notification.RunListener} that collects all events.
 */
public class EventCollector extends RunListener {

    private final List<Description> testRunsStarted = synchronizedList(new ArrayList<Description>());

    private final List<Result> testRunsFinished = synchronizedList(new ArrayList<Result>());

    private final List<Description> testsStarted = synchronizedList(new ArrayList<Description>());

    private final List<Description> testsFinished = synchronizedList(new ArrayList<Description>());

    private final List<Failure> failures = synchronizedList(new ArrayList<Failure>());

    private final List<Failure> assumptionFailures = synchronizedList(new ArrayList<Failure>());

    private final List<Description> testsIgnored = synchronizedList(new ArrayList<Description>());

    @Override
    public void testRunStarted(final Description description) throws Exception {
        testRunsStarted.add(description);
    }

    @Override
    public void testRunFinished(final Result result) throws Exception {
        testRunsFinished.add(result);
    }

    @Override
    public void testStarted(final Description description) throws Exception {
        testsStarted.add(description);
    }

    @Override
    public void testFinished(final Description description) throws Exception {
        testsFinished.add(description);
    }

    @Override
    public void testFailure(final Failure failure) throws Exception {
        failures.add(failure);
    }

    @Override
    public void testAssumptionFailure(final Failure failure) {
        assumptionFailures.add(failure);
    }

    @Override
    public void testIgnored(final Description description) throws Exception {
        testsIgnored.add(description);
    }

    public List<Description> getTestRunsStarted() {
        return unmodifiableList(testRunsStarted);
    }

    public List<Result> getTestRunsFinished() {
        return unmodifiableList(testRunsFinished);
    }

    public List<Description> getTestsStarted() {
        return unmodifiableList(testsStarted);
    }

    public List<Description> getTestsFinished() {
        return unmodifiableList(testsFinished);
    }

    public List<Failure> getFailures() {
        return unmodifiableList(failures);
    }

    public List<Failure> getAssumptionFailures() {
        return unmodifiableList(assumptionFailures);
    }

    public List<Description> getTestsIgnored() {
        return unmodifiableList(testsIgnored);
    }

    @Override
    public String toString() {
        return testRunsStarted.size() + " test runs started, "
                + testRunsFinished.size() + " test runs finished, "
                + testsStarted.size() + " tests started, "
                + testsFinished.size() + " tests finished, " + failures.size()
                + " failures, " + assumptionFailures.size()
                + " assumption failures, " + testsIgnored.size()
                + " tests ignored";
    }
}