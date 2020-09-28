package uk.co.probablyfine.matchers;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.lang.reflect.Modifier.isPublic;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;

final class ApiHelper {

    static final Map<String, List<Method>> HAMCREST_MATCHER_METHODS_BY_NAME = Stream.of(org.hamcrest.Matchers.class.getMethods())
            .filter(ApiHelper::isMatcherMethod)
            .collect(groupingBy(Method::getName));

    static boolean isMatcherMethod(Method method) {
        int modifiers = method.getModifiers();
        return isStatic(modifiers) && isPublic(modifiers) && org.hamcrest.Matcher.class.isAssignableFrom(method.getReturnType());
    }

    static Matcher<Method> existsInHamcrest() {
        return new TypeSafeDiagnosingMatcher<Method>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Matcher method with equivalent in Hamcrest Matchers API");
            }

            @Override
            protected boolean matchesSafely(Method method, Description mismatchDescription) {
                boolean matches = true;
                if (!isMatcherMethod(method)) {
                    mismatchDescription.appendText(describe(method) + " is not a Matcher method");
                    matches = false;
                }
                List<Method> existingMethods = HAMCREST_MATCHER_METHODS_BY_NAME.getOrDefault(method.getName(), emptyList());
                if (existingMethods.isEmpty()) {
                    mismatchDescription.appendText((!matches ? ", and " : describe(method) + " ") + "does not exist in Hamcrest Matchers API");
                    matches = false;
                }
                return matches;
            }
        };
    }

    private static String describe(Method method) {
        return method.getReturnType().getSimpleName() + " " + method.getName() + "(" + (method.getParameterCount() > 0 ? ".." : "") + ")";
    }

    private ApiHelper() {
    }
}
