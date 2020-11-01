package uk.co.probablyfine.matchers;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertAll;
import static uk.co.probablyfine.matchers.ApiHelper.describe;
import static uk.co.probablyfine.matchers.ApiHelper.isDeprecated;
import static uk.co.probablyfine.matchers.HamcrestApiMatchers.existsInHamcrest;
import static uk.co.probablyfine.matchers.Java8Matchers.where;

class OptionalMatchersTest {

    @Test
    void notPresent_success() {
        assertThat(Optional.empty(), OptionalMatchers.notPresent());
    }

    @Test
    void notPresent_failure() {
        assertThat(Optional.of(1), not(OptionalMatchers.notPresent()));
    }

    @Test
    void notPresent_failureMessage() {
        Helper.testFailingMatcher(Optional.of(1), OptionalMatchers.notPresent(), "An Optional with no value","<Optional[1]>");
    }

    @Test
    void present_success() {
        assertThat(Optional.of("Hi!"), OptionalMatchers.present("Hi!"));
    }

    @Test
    void present_failureNonEmpty() {
        assertThat(Optional.of("Hi!"), not(OptionalMatchers.present("Yay")));
    }

    @Test
    void present_failureEmpty() {
        assertThat(Optional.empty(), not(OptionalMatchers.present("Woot")));
    }


    @Test
    void present_failureMessages() {
        Helper.testFailingMatcher(Optional.of(1), OptionalMatchers.present(2), "Optional[2]","<Optional[1]>");
    }

    @Test
    void presentMatcher_success() {
        assertThat(Optional.of(4), OptionalMatchers.present(Matchers.greaterThan(3)));
    }

    @Test
    void presentMatcher_success_typechecksWhenOptionalsArgIsStrictSubtype() {
        Optional<List<String>> optionalToMatch = Optional.of(Arrays.asList("a"));
        Matcher<Iterable<? super String>> matcherOfStrictSuperType = hasItem("a");
        assertThat(optionalToMatch, OptionalMatchers.present(matcherOfStrictSuperType));
    }

    @Test
    void presentMatcher_failureDiffering() {
        assertThat(Optional.of(100), not(OptionalMatchers.present(Matchers.lessThanOrEqualTo(19))));
    }

    @Test
    void presentMatcher_failureEmpty() {
        assertThat(Optional.empty(), not(OptionalMatchers.present(Matchers.lessThanOrEqualTo(19))));
    }

    @Test
    void presentMatcher_failureMessage() {
        Helper.testFailingMatcher(Optional.of(2), OptionalMatchers.present(Matchers.equalTo(4)), "Optional with an item that matches <4>","<Optional[2]>");
    }

    @Test
    void notPresentInt_success() {
        assertThat(OptionalInt.empty(), OptionalMatchers.notPresentInt());
    }

    @Test
    void notPresentInt_failure() {
        assertThat(OptionalInt.of(0), not(OptionalMatchers.notPresentInt()));
    }

    @Test
    void presentInt_success() {
        assertThat(OptionalInt.of(0), OptionalMatchers.presentInt(0));
    }

    @Test
    void presentInt_failureDiffering() {
        assertThat(OptionalInt.of(0), not(OptionalMatchers.presentInt(1)));
    }

    @Test
    void presentInt_failureEmpty() {
        assertThat(OptionalInt.empty(), not(OptionalMatchers.presentInt(1)));
    }

    @Test
    void presentIntMatcher_success() {
        assertThat(OptionalInt.of(0), OptionalMatchers.presentInt(Matchers.equalTo(0)));
    }

    @Test
    void presentIntMatcher_failureEmpty() {
        assertThat(OptionalInt.empty(), not(OptionalMatchers.presentInt(Matchers.equalTo(1))));
    }

    @Test
    void presentIntMatcher_failureDiffering() {
        assertThat(OptionalInt.of(0), not(OptionalMatchers.presentInt(Matchers.equalTo(1))));
    }

    @Test
    void noNonDeprecatedMatchersNameClashWithHamcrestMatchers() {
        assertAll(Stream.of(OptionalMatchers.class.getMethods())
                .filter(method -> !isDeprecated(method)).sorted(comparing(Method::getName))
                .map(method -> () -> assertThat(method, not(existsInHamcrest()))));
    }

    @Test
    void relatedMatchersOfDeprecatedMatchersAreAlsoDeprecated() {
        ApiInspector streamMatchersApi = new ApiInspector(OptionalMatchers.class, ApiHelper::isMatcherMethod);
        assertAll(streamMatchersApi.getDeprecated()
                .flatMap(streamMatchersApi::findRelatedOf).distinct()
                .map(relatedMethod -> () -> assertThat(describe(relatedMethod) + " should be deprecated", relatedMethod, where(ApiHelper::isDeprecated))));
    }

}
