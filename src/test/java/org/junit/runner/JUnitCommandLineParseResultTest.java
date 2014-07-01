package org.junit.runner;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.IncludeCategories;
import org.junit.rules.ExpectedException;
import org.junit.runner.manipulation.Filter;

public class JUnitCommandLineParseResultTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private final JUnitCommandLineParseResult jUnitCommandLineParseResult = new JUnitCommandLineParseResult();

    @Test
    public void shouldStopParsingOptionsUponDoubleHyphenArg() throws Exception {
        final String[] restOfArgs = jUnitCommandLineParseResult.parseOptions(
                "--0", "--1", "--", "--2", "--3");

        assertThat(restOfArgs, is(new String[] { "--2", "--3" }));
    }

    @Test
    public void shouldParseFilterArgWithEqualsSyntax() throws Exception {
        final String value = IncludeCategories.class.getName() + "="
                + DummyCategory0.class.getName();
        jUnitCommandLineParseResult.parseOptions("--filter=" + value);

        final List<String> specs = jUnitCommandLineParseResult.getFilterSpecs();

        assertThat(specs, hasItems(value));
    }

    @Test
    public void shouldCreateFailureUponBaldFilterOptionNotFollowedByValue() {
        jUnitCommandLineParseResult.parseOptions("--filter");

        final Runner runner = jUnitCommandLineParseResult.createRequest(
                new Computer()).getRunner();
        final Description description = runner.getDescription().getChildren()
                .get(0);

        assertThat(description.toString(),
                containsString("initializationError"));
    }

    @Test
    public void shouldParseFilterArgInWhichValueIsASeparateArg()
            throws Exception {
        final String value = IncludeCategories.class.getName() + "="
                + DummyCategory0.class.getName();
        jUnitCommandLineParseResult.parseOptions("--filter", value);

        final List<String> specs = jUnitCommandLineParseResult.getFilterSpecs();

        assertThat(specs, hasItems(value));
    }

    @Test
    public void shouldStopParsingOptionsUponNonOption() throws Exception {
        final String[] restOfArgs = jUnitCommandLineParseResult
                .parseOptions(new String[] { "--0", "--1", "2", "3" });

        assertThat(restOfArgs, is(new String[] { "2", "3" }));
    }

    @Test
    public void shouldCreateFailureUponUnknownOption() throws Exception {
        final String unknownOption = "--unknown-option";
        jUnitCommandLineParseResult
                .parseOptions(new String[] { unknownOption });

        final Runner runner = jUnitCommandLineParseResult.createRequest(
                new Computer()).getRunner();
        final Description description = runner.getDescription().getChildren()
                .get(0);

        assertThat(description.toString(),
                containsString("initializationError"));
    }

    @Test
    public void shouldCreateFailureUponUncreatedFilter() throws Exception {
        jUnitCommandLineParseResult.parseOptions(new String[] { "--filter="
                + FilterFactoryStub.class.getName() });

        final Runner runner = jUnitCommandLineParseResult.createRequest(
                new Computer()).getRunner();
        final Description description = runner.getDescription().getChildren()
                .get(0);

        assertThat(description.toString(),
                containsString("initializationError"));
    }

    @Test
    public void shouldCreateFailureUponUnfoundFilterFactory() throws Exception {
        final String nonExistentFilterFactory = "NonExistentFilterFactory";
        jUnitCommandLineParseResult.parseOptions(new String[] { "--filter="
                + nonExistentFilterFactory });

        final Runner runner = jUnitCommandLineParseResult.createRequest(
                new Computer()).getRunner();
        final Description description = runner.getDescription().getChildren()
                .get(0);

        assertThat(description.toString(),
                containsString("initializationError"));
    }

    @Test
    public void shouldAddToClasses() {
        jUnitCommandLineParseResult
                .parseParameters(new String[] { DummyTest.class.getName() });

        final List<Class<?>> classes = jUnitCommandLineParseResult.getClasses();
        final Class<?> testClass = classes.get(0);

        assertThat(testClass.getName(), is(DummyTest.class.getName()));
    }

    @Test
    public void shouldCreateFailureUponUnknownTestClass() throws Exception {
        final String unknownTestClass = "UnknownTestClass";
        jUnitCommandLineParseResult
                .parseParameters(new String[] { unknownTestClass });

        final Runner runner = jUnitCommandLineParseResult.createRequest(
                new Computer()).getRunner();
        final Description description = runner.getDescription().getChildren()
                .get(0);

        assertThat(description.toString(),
                containsString("initializationError"));
    }

    public static class FilterFactoryStub implements FilterFactory {
        public Filter createFilter(final FilterFactoryParams params)
                throws FilterNotCreatedException {
            throw new FilterNotCreatedException(new Exception("stub"));
        }
    }

    public static interface DummyCategory0 {
    }

    public static class DummyTest {
        @Test
        public void dummyTest() {
        }
    }
}