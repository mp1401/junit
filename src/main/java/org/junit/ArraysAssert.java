package org.junit;

import java.lang.reflect.Array;

import org.junit.internal.ArrayComparisonFailure;

/**
 * Assertions on arrays.
 * 
 * @since 4.12
 */
class ArraysAssert {
    private ArraysAssert() {
    }

    /**
     * Asserts that two boolean arrays are equal. If they are not, an
     * {@link AssertionError} is thrown with the given message. If
     * {@code expecteds} and {@code actuals} are {@code null}, they are
     * considered equal.
     * 
     * @param message
     *            the identifying message for the {@link AssertionError} (can be
     *            {@code null})
     * @param expecteds
     *            boolean array with expected values
     * @param actuals
     *            boolean array with expected values
     */
    public static void assertEquals(final String message,
            final boolean[] expecteds, final boolean[] actuals)
            throws ArrayComparisonFailure {
        internalAssertEquals(message, expecteds, actuals,
                new ComparisonHelper() {
                    public int getExpectedArrayLength() {
                        return expecteds.length;
                    }

                    public int getActualArrayLength() {
                        return actuals.length;
                    }

                    public void assertElementEquals(final int index) {
                        Assert.assertEquals(expecteds[index], actuals[index]);
                    }
                });
    }

    /**
     * Asserts that two byte arrays are equal. If they are not, an
     * {@link AssertionError} is thrown with the given message. If
     * {@code expecteds} and {@code actuals} are {@code null}, they are
     * considered equal.
     * 
     * @param message
     *            the identifying message for the {@link AssertionError} (can be
     *            {@code null})
     * @param expecteds
     *            byte array with expected values
     * @param actuals
     *            byte array with expected values
     */
    public static void assertEquals(final String message,
            final byte[] expecteds, final byte[] actuals)
            throws ArrayComparisonFailure {
        internalAssertEquals(message, expecteds, actuals,
                new ComparisonHelper() {
                    public int getExpectedArrayLength() {
                        return expecteds.length;
                    }

                    public int getActualArrayLength() {
                        return actuals.length;
                    }

                    public void assertElementEquals(final int index) {
                        Assert.assertEquals(expecteds[index], actuals[index]);
                    }
                });
    }

    /**
     * Asserts that two char arrays are equal. If they are not, an
     * {@link AssertionError} is thrown with the given message. If
     * {@code expecteds} and {@code actuals} are {@code null}, they are
     * considered equal.
     * 
     * @param message
     *            the identifying message for the {@link AssertionError} (can be
     *            {@code null})
     * @param expecteds
     *            char array with expected values
     * @param actuals
     *            char array with expected values
     */
    public static void assertEquals(final String message,
            final char[] expecteds, final char[] actuals)
            throws ArrayComparisonFailure {
        internalAssertEquals(message, expecteds, actuals,
                new ComparisonHelper() {
                    public int getExpectedArrayLength() {
                        return expecteds.length;
                    }

                    public int getActualArrayLength() {
                        return actuals.length;
                    }

                    public void assertElementEquals(final int index) {
                        Assert.assertEquals(expecteds[index], actuals[index]);
                    }
                });
    }

    /**
     * Asserts that two short arrays are equal. If they are not, an
     * {@link AssertionError} is thrown with the given message. If
     * {@code expecteds} and {@code actuals} are {@code null}, they are
     * considered equal.
     * 
     * @param message
     *            the identifying message for the {@link AssertionError} (can be
     *            {@code null})
     * @param expecteds
     *            short array with expected values
     * @param actuals
     *            short array with expected values
     */
    public static void assertEquals(final String message,
            final short[] expecteds, final short[] actuals) {
        internalAssertEquals(message, expecteds, actuals,
                new ComparisonHelper() {
                    public int getExpectedArrayLength() {
                        return expecteds.length;
                    }

                    public int getActualArrayLength() {
                        return actuals.length;
                    }

                    public void assertElementEquals(final int index) {
                        Assert.assertEquals(expecteds[index], actuals[index]);
                    }
                });
    }

    /**
     * Asserts that two int arrays are equal. If they are not, an
     * {@link AssertionError} is thrown with the given message. If
     * {@code expecteds} and {@code actuals} are {@code null}, they are
     * considered equal.
     * 
     * @param message
     *            the identifying message for the {@link AssertionError} (can be
     *            {@code null})
     * @param expecteds
     *            int array with expected values
     * @param actuals
     *            int array with expected values
     */
    public static void assertEquals(final String message,
            final int[] expecteds, final int[] actuals)
            throws ArrayComparisonFailure {
        internalAssertEquals(message, expecteds, actuals,
                new ComparisonHelper() {
                    public int getExpectedArrayLength() {
                        return expecteds.length;
                    }

                    public int getActualArrayLength() {
                        return actuals.length;
                    }

                    public void assertElementEquals(final int index) {
                        Assert.assertEquals(expecteds[index], actuals[index]);
                    }
                });
    }

    /**
     * Asserts that two long arrays are equal. If they are not, an
     * {@link AssertionError} is thrown with the given message. If
     * {@code expecteds} and {@code actuals} are {@code null}, they are
     * considered equal.
     * 
     * @param message
     *            the identifying message for the {@link AssertionError} (can be
     *            {@code null})
     * @param expecteds
     *            long array with expected values
     * @param actuals
     *            long array with expected values
     */
    public static void assertEquals(final String message,
            final long[] expecteds, final long[] actuals)
            throws ArrayComparisonFailure {
        internalAssertEquals(message, expecteds, actuals,
                new ComparisonHelper() {
                    public int getExpectedArrayLength() {
                        return expecteds.length;
                    }

                    public int getActualArrayLength() {
                        return actuals.length;
                    }

                    public void assertElementEquals(final int index) {
                        Assert.assertEquals(expecteds[index], actuals[index]);
                    }
                });
    }

    /**
     * Asserts that two double arrays are equal. If they are not, an
     * {@link AssertionError} is thrown with the given message. If
     * {@code expecteds} and {@code actuals} are {@code null}, they are
     * considered equal.
     * 
     * @param message
     *            the identifying message for the {@link AssertionError} (can be
     *            {@code null})
     * @param expecteds
     *            double array with expected values
     * @param actuals
     *            double array with expected values
     * @param delta
     *            the maximum delta between {@code expecteds[i]} and
     *            {@code actuals[i]} for which both numbers are still considered
     *            equal
     */
    public static void assertEquals(final String message,
            final double[] expecteds, final double[] actuals, final double delta) {
        internalAssertEquals(message, expecteds, actuals,
                new ComparisonHelper() {
                    public int getExpectedArrayLength() {
                        return expecteds.length;
                    }

                    public int getActualArrayLength() {
                        return actuals.length;
                    }

                    public void assertElementEquals(final int index) {
                        Assert.assertEquals(expecteds[index], actuals[index],
                                delta);
                    }
                });
    }

    /**
     * Asserts that two float arrays are equal. If they are not, an
     * {@link AssertionError} is thrown with the given message. If
     * {@code expecteds} and {@code actuals} are {@code null}, they are
     * considered equal.
     * 
     * @param message
     *            the identifying message for the {@link AssertionError} (can be
     *            {@code null})
     * @param expecteds
     *            float array with expected values
     * @param actuals
     *            float array with expected values
     * @param delta
     *            the maximum delta between {@code expecteds[i]} and
     *            {@code actuals[i]} for which both numbers are still considered
     *            equal
     */
    public static void assertEquals(final String message,
            final float[] expecteds, final float[] actuals, final float delta) {
        internalAssertEquals(message, expecteds, actuals,
                new ComparisonHelper() {
                    public int getExpectedArrayLength() {
                        return expecteds.length;
                    }

                    public int getActualArrayLength() {
                        return actuals.length;
                    }

                    public void assertElementEquals(final int index) {
                        Assert.assertEquals(expecteds[index], actuals[index],
                                delta);
                    }
                });
    }

    /**
     * Asserts that two object arrays are deeply equal. If they are not, an
     * {@link AssertionError} is thrown with the given message. If
     * {@code expecteds} and {@code actuals} are {@code null}, they are
     * considered equal.
     * 
     * @param message
     *            the identifying message for the {@link AssertionError} (can be
     *            {@code null})
     * @param expecteds
     *            object array with expected values
     * @param actuals
     *            object array with expected values
     */
    public static void assertDeepEquals(final String message,
            final Object[] expecteds, final Object[] actuals) {
        internalAssertEquals(message, expecteds, actuals,
                new ComparisonHelper() {
                    public int getExpectedArrayLength() {
                        return expecteds.length;
                    }

                    public int getActualArrayLength() {
                        return actuals.length;
                    }

                    public void assertElementEquals(final int index) {
                        final Object expected = expecteds[index];
                        final Object actual = actuals[index];

                        if (isArray(expected) && isArray(actual)) {
                            try {
                                assertDeepEqualsViaReflection(message,
                                        expected, actual);
                            } catch (final ArrayComparisonFailure e) {
                                e.addDimension(index);
                                throw e;
                            }
                        } else {
                            Assert.assertEquals(expected, actual);
                        }
                    }
                });
    }

    private static void assertDeepEqualsViaReflection(final String message,
            final Object expecteds, final Object actuals) {
        internalAssertEquals(message, expecteds, actuals,
                new ComparisonHelper() {
                    public int getExpectedArrayLength() {
                        return Array.getLength(expecteds);
                    }

                    public int getActualArrayLength() {
                        return Array.getLength(actuals);
                    }

                    public void assertElementEquals(final int index) {
                        final Object expected = Array.get(expecteds, index);
                        final Object actual = Array.get(actuals, index);

                        if (isArray(expected) && isArray(actual)) {
                            try {
                                assertDeepEqualsViaReflection(message,
                                        expected, actual);
                            } catch (final ArrayComparisonFailure e) {
                                e.addDimension(index);
                                throw e;
                            }
                        } else {
                            Assert.assertEquals(expected, actual);
                        }
                    }
                });
    }

    private static boolean isArray(final Object expected) {
        return expected != null && expected.getClass().isArray();
    }

    private static void internalAssertEquals(final String message,
            final Object expecteds, final Object actuals,
            final ComparisonHelper comparisonHelper) {
        final String messagePrefix = message == null ? "" : message + ": ";

        if (arraysTriviallyEqual(messagePrefix, expecteds, actuals)) {
            return;
        }

        assertElementsEqual(messagePrefix, comparisonHelper);
    }

    private static boolean arraysTriviallyEqual(final String messagePrefix,
            final Object expecteds, final Object actuals) {
        if (expecteds == actuals) {
            return true;
        }
        if (expecteds == null) {
            Assert.fail(messagePrefix + "expected array was null");
        }
        if (actuals == null) {
            Assert.fail(messagePrefix + "actual array was null");
        }
        return false;
    }

    private static void assertElementsEqual(final String messagePrefix,
            final ComparisonHelper comparisonHelper) {
        final int actualsLength = comparisonHelper.getActualArrayLength();
        final int expectedsLength = comparisonHelper.getExpectedArrayLength();
        if (actualsLength != expectedsLength) {
            Assert.fail(messagePrefix
                    + "array lengths differed, expected.length="
                    + expectedsLength + " actual.length=" + actualsLength);
        }
        int index = 0;
        try {
            for (; index < expectedsLength; index++) {
                comparisonHelper.assertElementEquals(index);
            }
        } catch (final ArrayComparisonFailure e) {
            /*
             * If we get here, the above assertElementEquals() was comparing two
             * arrays. Rethrow the exception so the caller can optionally add
             * dimensions.
             */
            throw e;
        } catch (final AssertionError e) {
            throw new ArrayComparisonFailure(messagePrefix, e, index);
        }
    }

    /**
     * Provides generic access to the arrays being compared.
     */
    interface ComparisonHelper {

        /**
         * Gets the length of the array of expected values.
         */
        int getExpectedArrayLength();

        /**
         * Gets the length of the array of actual values.
         */
        int getActualArrayLength();

        /**
         * Asserts that the two elements at the given index are equal.
         */
        void assertElementEquals(int index);
    }
}