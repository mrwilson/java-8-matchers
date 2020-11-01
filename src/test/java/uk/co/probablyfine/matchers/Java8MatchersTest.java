package uk.co.probablyfine.matchers;


import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertAll;
import static uk.co.probablyfine.matchers.HamcrestApiMatchers.existsInHamcrest;

public class Java8MatchersTest {

    final A entity = new A();

    @Test
    public void propertiesWhichAreTrue() {
        assertThat(entity, Java8Matchers.where(A::isCool));
        assertThat(entity, Java8Matchers.where(a -> a.isCool()));
    }

    @Test
    public void propertiesWhichAreFalse() {
        assertThat(entity, Java8Matchers.whereNot(A::isBoring));
        assertThat(entity, Java8Matchers.whereNot(a -> a.isBoring()));
    }

    @Test
    public void matchProperties() {
        assertThat(entity, Java8Matchers.where(A::name, is("A")));
        assertThat(entity, Java8Matchers.where(a -> a.name(), is("A")));
    }

    @Test
    public void failMatchingProperties() {
        Helper.testFailingMatcher(entity, Java8Matchers.where(A::name, is("X")), "with a name (a String) which is \"X\"", "had the name (a String) \"A\"");
        Helper.testFailingMatcher(entity, Java8Matchers.where(a -> a.name(), is("X")), "with a String which is \"X\"", "had the String \"A\"");
        Helper.testFailingMatcher(entity, Java8Matchers.where(A::age, not(is(42))), "with an age (an int) which not is <42>", "had the age (an int) <42>");
        Helper.testFailingMatcher(entity, Java8Matchers.where(a -> a.age(), is(1337)), "with an Integer which is <1337>", "had the Integer <42>");
        Helper.testFailingMatcher(entity, Java8Matchers.where("age", a -> a.age(), is(1337)), "with an age which is <1337>", "had the age <42>");
    }


    static class A {
        boolean isCool() {
            return true;
        }
        boolean isBoring() {
            return false;
        }
        String name() {
            return "A";
        }
        int age() {
            return 42;
        }
    }


    @Test
    void noMatchersNameClashWithHamcrestMatchers() {
        assertAll(Stream.of(Java8Matchers.class.getMethods()).sorted(comparing(Method::getName))
                .map(method -> () -> assertThat(method, not(existsInHamcrest()))));
    }

}
