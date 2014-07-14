package junit.textui;

import java.io.PrintStream;
import java.text.NumberFormat;
import java.util.Enumeration;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestFailure;
import junit.framework.TestListener;
import junit.framework.TestResult;
import junit.runner.BaseTestRunner;

public class ResultPrinter implements TestListener {
    PrintStream fWriter;

    int fColumn = 0;

    public ResultPrinter(final PrintStream writer) {
        fWriter = writer;
    }

    /* API for use by textui.TestRunner */

    synchronized void print(final TestResult result, final long runTime) {
        printHeader(runTime);
        printErrors(result);
        printFailures(result);
        printFooter(result);
    }

    void printWaitPrompt() {
        getWriter().println();
        getWriter().println("<RETURN> to continue");
    }

    /* Internal methods */

    protected void printHeader(final long runTime) {
        getWriter().println();
        getWriter().println("Time: " + elapsedTimeAsString(runTime));
    }

    protected void printErrors(final TestResult result) {
        printDefects(result.errors(), result.errorCount(), "error");
    }

    protected void printFailures(final TestResult result) {
        printDefects(result.failures(), result.failureCount(), "failure");
    }

    protected void printDefects(final Enumeration<TestFailure> booBoos,
            final int count, final String type) {
        if (count == 0)
            return;
        if (count == 1) {
            getWriter().println("There was " + count + " " + type + ":");
        } else {
            getWriter().println("There were " + count + " " + type + "s:");
        }
        for (int i = 1; booBoos.hasMoreElements(); i++) {
            printDefect(booBoos.nextElement(), i);
        }
    }

    public void printDefect(final TestFailure booBoo, final int count) { // only
                                                                         // public
                                                                         // for
                                                                         // testing
                                                                         // purposes
        printDefectHeader(booBoo, count);
        printDefectTrace(booBoo);
    }

    protected void printDefectHeader(final TestFailure booBoo, final int count) {
        // I feel like making this a println, then adding a line giving the
        // throwable a chance to print something
        // before we get to the stack trace.
        getWriter().print(count + ") " + booBoo.failedTest());
    }

    protected void printDefectTrace(final TestFailure booBoo) {
        getWriter().print(BaseTestRunner.getFilteredTrace(booBoo.trace()));
    }

    protected void printFooter(final TestResult result) {
        if (result.wasSuccessful()) {
            getWriter().println();
            getWriter().print("OK");
            getWriter().println(
                    " (" + result.runCount() + " test"
                            + (result.runCount() == 1 ? "" : "s") + ")");

        } else {
            getWriter().println();
            getWriter().println("FAILURES!!!");
            getWriter().println(
                    "Tests run: " + result.runCount() + ",  Failures: "
                            + result.failureCount() + ",  Errors: "
                            + result.errorCount());
        }
        getWriter().println();
    }

    /**
     * Returns the formatted string of the elapsed time. Duplicated from
     * BaseTestRunner. Fix it.
     */
    protected String elapsedTimeAsString(final long runTime) {
        return NumberFormat.getInstance().format((double) runTime / 1000);
    }

    public PrintStream getWriter() {
        return fWriter;
    }

    /**
     * @see junit.framework.TestListener#addError(Test, Throwable)
     */
    public void addError(final Test test, final Throwable e) {
        getWriter().print("E");
    }

    /**
     * @see junit.framework.TestListener#addFailure(Test, AssertionFailedError)
     */
    public void addFailure(final Test test, final AssertionFailedError t) {
        getWriter().print("F");
    }

    /**
     * @see junit.framework.TestListener#endTest(Test)
     */
    public void endTest(final Test test) {
    }

    /**
     * @see junit.framework.TestListener#startTest(Test)
     */
    public void startTest(final Test test) {
        getWriter().print(".");
        if (fColumn++ >= 40) {
            getWriter().println();
            fColumn = 0;
        }
    }

}
