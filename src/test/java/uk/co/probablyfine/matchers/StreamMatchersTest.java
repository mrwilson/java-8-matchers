package uk.co.probablyfine.matchers;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Test;
import uk.co.probablyfine.matchers.function.DescribableFunction;

import java.util.stream.BaseStream;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.not;
import static uk.co.probablyfine.matchers.Java8Matchers.where;

public class StreamMatchersTest {
    @Test
    public void equalTo_failureDifferingSingleItem() throws Exception {
        assertThat(Stream.of("a"), is(not(StreamMatchers.equalTo(Stream.of("b")))));
    }

    @Test
    public void contains_failureDifferingSingleItem() throws Exception {
        assertThat(Stream.of("a"), not(StreamMatchers.contains("b")));
    }

    @Test
    public void equalTo_failureDifferingLength() throws Exception {
        assertThat(Stream.of("a"), is(not(StreamMatchers.equalTo(Stream.of("a", "b")))));
    }

    @Test
    public void contains_failureDifferingLength() throws Exception {
        assertThat(Stream.of("a"), not(StreamMatchers.contains("a", "b")));
    }

    @Test
    public void equalTo_failureDifferingItems() throws Exception {
        assertThat(Stream.of("a","c"), is(not(StreamMatchers.equalTo(Stream.of("a", "b")))));
    }

    @Test
    public void contains_failureDifferingItems() throws Exception {
        assertThat(Stream.of("a","c"), not(StreamMatchers.contains("a", "b")));
    }

    @Test
    public void equalTo_successEmpty() throws Exception {
        assertThat(Stream.empty(), StreamMatchers.equalTo(Stream.empty()));
    }

    @Test
    public void empty_Success() throws Exception {
        assertThat(Stream.empty(), StreamMatchers.empty());
    }

    @Test
    public void empty_Failure() throws Exception {
        Helper.testFailingMatcher(Stream.of(3), StreamMatchers.empty(), "An empty Stream", "A non empty Stream starting with <3>");
    }

    @Test
    public void equalToIntStream_success() throws Exception {
        assertThat(IntStream.range(1, 10), StreamMatchers.equalTo(IntStream.range(1, 10)));
    }

    @Test
    public void containsIntStream_success() throws Exception {
        assertThat(IntStream.range(1, 4), StreamMatchers.contains(1,2,3));
    }

    @Test
    public void equalTo_successManyItems() throws Exception {
        assertThat(Stream.of("a", "b", "c"), StreamMatchers.equalTo(Stream.of("a", "b", "c")));
    }

    @Test
    public void contains_successManyItems() throws Exception {
        assertThat(Stream.of("a", "b", "c"), StreamMatchers.contains("a", "b", "c"));
    }

    @Test
    public void contains_is_nullsafe() {
        assertThat(Stream.of("a", null, "c"), StreamMatchers.contains("a", null, "c"));
    }

    @Test
    public void allMatch_success() throws Exception {
        assertThat(Stream.of("bar","baz"), StreamMatchers.allMatch(containsString("a")));
    }

    @Test
    public void allMatch_failure() throws Exception {
        Matcher<Stream<String>> matcher = StreamMatchers.allMatch(containsString("a"));
        Stream<String> testData = Stream.of("bar", "bar", "foo", "grault", "garply", "waldo");
        Helper.testFailingMatcher(testData, matcher, "All to match <a string containing \"a\">", "Item 2 failed to match: \"foo\"");
    }

    @Test
    public void allMatchInt_failure() throws Exception {
        Matcher<IntStream> matcher = StreamMatchers.allMatchInt(Matchers.lessThan(3));
        IntStream testData = IntStream.range(0, 10);
        Helper.testFailingMatcher(testData, matcher, "All to match <a value less than <3>>", "Item 3 failed to match: <3>");
    }

    @Test
    public void allMatchLong_failure() throws Exception {
        Matcher<LongStream> matcher = StreamMatchers.allMatchLong(Matchers.lessThan(3L));
        LongStream testData = LongStream.range(0, 10);
        Helper.testFailingMatcher(testData, matcher, "All to match <a value less than <3L>>", "Item 3 failed to match: <3L>");
    }

    @Test
    public void allMatchDouble_failure() throws Exception {
        Matcher<DoubleStream> matcher = StreamMatchers.allMatchDouble(Matchers.lessThan(3d));
        DoubleStream testData = DoubleStream.iterate(0d, d -> d + 1).limit(10);
        Helper.testFailingMatcher(testData, matcher, "All to match <a value less than <3.0>>", "Item 3 failed to match: <3.0>");
    }

    @Test
    public void allMatch_empty() throws Exception {
        assertThat(Stream.empty(), StreamMatchers.allMatch(containsString("foo")));
    }

    @Test
    public void anyMatch_success() throws Exception {
        assertThat(Stream.of("bar", "bar", "foo", "grault", "garply", "waldo"), StreamMatchers.anyMatch(containsString("ald")));
    }

    @Test
    public void anyMatch_failure() throws Exception {
        Matcher<Stream<String>> matcher = StreamMatchers.anyMatch(containsString("z"));
        Stream<String> testData = Stream.of("bar", "bar", "foo", "grault", "garply", "waldo");
        Helper.testFailingMatcher(testData, matcher, "Any to match <a string containing \"z\"", "None of these items matched: [\"bar\",\"bar\",\"foo\",\"grault\",\"garply\",\"waldo\"]");
    }

    @Test
    public void anyMatchInt_success() throws Exception {
        assertThat(IntStream.range(0, 1_000), StreamMatchers.anyMatchInt(equalTo(10)));
    }

    @Test
    public void anyMatchInt_failure() throws Exception {
        Helper.testFailingMatcher(IntStream.range(0, 5), StreamMatchers.anyMatchInt(equalTo(101)), "Any to match <<101>>", "None of these items matched: [<0>,<1>,<2>,<3>,<4>]");
    }

    @Test
    public void anyMatchLong_success() throws Exception {
        assertThat(LongStream.range(0, 1_000), StreamMatchers.anyMatchLong(equalTo(10L)));
    }

    @Test
    public void anyMatchLong_failure() throws Exception {
        Helper.testFailingMatcher(LongStream.range(0, 5), StreamMatchers.anyMatchLong(equalTo(101L)), "Any to match <<101L>>", "None of these items matched: [<0L>,<1L>,<2L>,<3L>,<4L>]");
    }

    @Test
    public void anyMatchDouble_success() throws Exception {
        assertThat(DoubleStream.iterate(0d, i -> i + 1), StreamMatchers.anyMatchDouble(equalTo(10d)));
    }

    @Test
    public void anyMatchDouble_failure() throws Exception {
        Helper.testFailingMatcher(DoubleStream.iterate(0d, i -> i + 1).limit(5), StreamMatchers.anyMatchDouble(equalTo(101d)), "Any to match <<101.0>>", "None of these items matched: [<0.0>,<1.0>,<2.0>,<3.0>,<4.0>]");
    }

    @Test
    public void anyMatch_empty() throws Exception {
        assertThat(Stream.empty(), Matchers.not(StreamMatchers.anyMatch(containsString("foo"))));
    }

    @Test
    public void startsWithMatcher_success() throws Exception {
        assertThat(Stream.iterate(0, i -> i + 1), StreamMatchers.startsWith(Stream.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), 10));
    }

    @Test
    public void startsWithMatcher_successBothInfinite() throws Exception {
        assertThat(Stream.iterate(0, i -> i + 1), StreamMatchers.startsWith(Stream.iterate(0, i -> i + 1), 10));
    }

    @Test
    public void startsWithMatcherInt_successBothInfinite() throws Exception {
        assertThat(IntStream.iterate(0, i -> i + 1), StreamMatchers.startsWith(IntStream.iterate(0, i -> i + 1), 10));
    }

    @Test
    public void startsWithMatcherLong_successBothInfinite() throws Exception {
        assertThat(LongStream.iterate(0, i -> i + 1), StreamMatchers.startsWith(LongStream.iterate(0, i -> i + 1), 10));
    }

    @Test
    public void startsWithMatcherDouble_successBothInfinite() throws Exception {
        assertThat(DoubleStream.iterate(0, i -> i + 1), StreamMatchers.startsWith(DoubleStream.iterate(0, i -> i + 1), 10));
    }


    @Test
    public void startsWithItems_success() throws Exception {
        assertThat(Stream.of("a", "b", "c", "d", "e", "f", "g", "h"), StreamMatchers.startsWith("a", "b", "c", "d", "e"));
    }

    @Test
    public void startsWithItemsIntStream_success() throws Exception {
        assertThat(IntStream.range(0, Integer.MAX_VALUE), StreamMatchers.startsWithInt(0, 1, 2, 3, 4));
    }

    @Test
    public void equalTo_failureMessages() throws Exception {
        Matcher<Stream<String>> matcher = StreamMatchers.equalTo(Stream.of("a", "b", "c", "d", "e", "f", "g", "h"));
        Stream<String> testData = Stream.of("a", "b", "c", "d", "e");
        Helper.testFailingMatcher(testData, matcher, "Stream of [\"a\",\"b\",\"c\",\"d\",\"e\",\"f\",\"g\",\"h\"]", "Stream of [\"a\",\"b\",\"c\",\"d\",\"e\"]");
    }

    @Test
    public void equalTo_handles_types() {
        Stream<Character> expectedStream = Stream.of('x', 'y', 'z');
        assertThat("xyz", where(s -> s.chars().mapToObj(i -> (char) i), StreamMatchers.equalTo(expectedStream)));

        BaseStream<Character, Stream<Character>> expectedBaseStream = Stream.of('x', 'y', 'z');
        assertThat("xyz", where(s -> s.chars().mapToObj(i -> (char) i), StreamMatchers.equalTo(expectedBaseStream)));

        DescribableFunction<String, BaseStream<Character, Stream<Character>>> characters = s -> s.chars().mapToObj(i -> (char) i);
        assertThat("xyz", where(characters, StreamMatchers.equalTo(Stream.of('x', 'y', 'z'))));
    }

    @Test
    public void contains_handles_types() {
        assertThat("xyz", where(s -> s.chars().mapToObj(i -> (char) i), StreamMatchers.contains('x', 'y', 'z')));

        DescribableFunction<String, BaseStream<Character, Stream<Character>>> characters = s -> s.chars().mapToObj(i -> (char) i);
        assertThat("xyz", where(characters, StreamMatchers.contains('x', 'y', 'z')));
        assertThat("xyz", where(characters, not(StreamMatchers.contains('x', 'y'))));
    }


    @Test
    public void contains_failureMessages() throws Exception {
        Stream<String> testData = Stream.of("a", "b", "c", "d", "e");
        Matcher<Stream<String>> matcher = StreamMatchers.contains("a", "b", "c", "d", "e", "f", "g", "h");
        Helper.testFailingMatcher(testData, matcher, "Stream of [\"a\",\"b\",\"c\",\"d\",\"e\",\"f\",\"g\",\"h\"]", "Stream of [\"a\",\"b\",\"c\",\"d\",\"e\"]");
    }

    @Test
    public void equalToIntStream_failureMessages() throws Exception {
        IntStream testData = IntStream.range(8, 10);
        Matcher<IntStream> matcher = StreamMatchers.equalTo(IntStream.range(0, 6));
        Helper.testFailingMatcher(testData, matcher, "Stream of [<0>,<1>,<2>,<3>,<4>,<5>]", "Stream of [<8>,<9>]");
    }

    @Test
    public void startsWithAll_success() throws Exception {
        assertThat(Stream.generate(() -> 10), StreamMatchers.startsWithAll(equalTo(10),100));
    }

    @Test
    public void startsWithAll_fail() throws Exception {
        Helper.testFailingMatcher(Stream.generate(() -> 11), StreamMatchers.startsWithAll(equalTo(10), 100), "First 100 to match <<10>>", "Item 0 failed to match: <11>");
    }

    @Test
    public void startsWithAllInt_success() throws Exception {
        assertThat(IntStream.generate(() -> 10), StreamMatchers.startsWithAllInt(equalTo(10), 100));
    }

    @Test
    public void startsWithAllInt_fail() throws Exception {
        Helper.testFailingMatcher(IntStream.iterate(0, i -> i + 1), StreamMatchers.startsWithAllInt(Matchers.lessThan(3), 100), "First 100 to match <a value less than <3>>", "Item 3 failed to match: <3>");
    }

    @Test
    public void startsWithAllLong_success() throws Exception {
        assertThat(LongStream.generate(() -> 10), StreamMatchers.startsWithAllLong(equalTo(10L), 100));
    }

    @Test
    public void startsWithAllLong_fail() throws Exception {
        Helper.testFailingMatcher(LongStream.iterate(0, i -> i + 1), StreamMatchers.startsWithAllLong(Matchers.lessThan(3L), 100), "First 100 to match <a value less than <3L>>", "Item 3 failed to match: <3L>");
    }

    @Test
    public void startsWithAllDouble_success() throws Exception {
        assertThat(DoubleStream.generate(() -> 10), StreamMatchers.startsWithAllDouble(equalTo(10d), 100));
    }

    @Test
    public void startsWithAllDouble_fail() throws Exception {
        Helper.testFailingMatcher(DoubleStream.iterate(0,i -> i + 1), StreamMatchers.startsWithAllDouble(Matchers.lessThan(3d), 100), "First 100 to match <a value less than <3.0>>", "Item 3 failed to match: <3.0>");
    }

    @Test
    public void startsWithAny_success() throws Exception {
        assertThat(Stream.iterate(0, i -> i + 1), StreamMatchers.startsWithAny(equalTo(10), 100));
    }

    @Test
    public void startsWithAny_fail() throws Exception {
        Helper.testFailingMatcher(Stream.iterate(0, i -> i + 1), StreamMatchers.startsWithAny(equalTo(-1), 10), "Any of first 10 to match <<-1>>", "None of these items matched: [<0>,<1>,<2>,<3>,<4>,<5>,<6>,<7>,<8>,<9>]");
    }

    @Test
    public void startsWithAnyInt_success() throws Exception {
        assertThat(IntStream.iterate(0, i -> i + 1), StreamMatchers.startsWithAnyInt(equalTo(10), 100));
    }

    @Test
    public void startsWithAnyInt_fail() throws Exception {
        Helper.testFailingMatcher(IntStream.iterate(0, i -> i + 1), StreamMatchers.startsWithAnyInt(equalTo(-1), 10), "Any of first 10 to match <<-1>>", "None of these items matched: [<0>,<1>,<2>,<3>,<4>,<5>,<6>,<7>,<8>,<9>]");
    }

    @Test
    public void startsWithAnyLong_success() throws Exception {
        assertThat(LongStream.iterate(0, i -> i + 1), StreamMatchers.startsWithAnyLong(equalTo(10L), 100));
    }

    @Test
    public void startsWithAnyLong_fail() throws Exception {
        Helper.testFailingMatcher(LongStream.iterate(0, i -> i + 1), StreamMatchers.startsWithAnyLong(equalTo(-1L), 10), "Any of first 10 to match <<-1L>>", "None of these items matched: [<0L>,<1L>,<2L>,<3L>,<4L>,<5L>,<6L>,<7L>,<8L>,<9L>]");
    }

    @Test
    public void startsWithAnyDouble_success() throws Exception {
        assertThat(DoubleStream.iterate(0, i -> i + 1), StreamMatchers.startsWithAnyDouble(equalTo(10d), 100));
    }

    @Test
    public void startsWithAnyDouble_fail() throws Exception {
        Helper.testFailingMatcher(DoubleStream.iterate(0, i -> i + 1), StreamMatchers.startsWithAnyDouble(equalTo(-1d), 10), "Any of first 10 to match <<-1.0>>", "None of these items matched: [<0.0>,<1.0>,<2.0>,<3.0>,<4.0>,<5.0>,<6.0>,<7.0>,<8.0>,<9.0>]");
    }

    @Test
    public void contains_returnsParameterizedMatcher() {
        usesStreamMatcher(Stream.of(10), StreamMatchers.contains(10));
    }

    @Test
    public void contains_acceptsMatchers() {
        usesStreamMatcher(
            Stream.of(10, 20, 30),
            StreamMatchers.contains(
                is(10),
                lessThanOrEqualTo(20),
                not(20)
            )
        );
    }

    private void usesStreamMatcher(Stream<Integer> stream, Matcher<Stream<Integer>> matcher) {
        assertThat(stream, matcher);
    }
}
