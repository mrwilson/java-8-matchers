package uk.co.probablyfine.matchers;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

import static java.lang.reflect.Modifier.isPublic;
import static java.lang.reflect.Modifier.isStatic;

final class ApiHelper {

    private static final ApiInspector hamcrestApi = new ApiInspector(org.hamcrest.Matchers.class, ApiHelper::isMatcherMethod);

    static Matcher<Method> existsInHamcrest() {
        return new TypeSafeDiagnosingMatcher<Method>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Matcher method with equivalent in Hamcrest Matchers API");
            }

            @Override
            protected boolean matchesSafely(Method method, Description mismatchDescription) {
                if (isMatcherMethod(method) && hamcrestApi.hasEquivalentTo(method)) {
                    return true;
                }
                mismatchDescription.appendText(describe(method) + " does not exist in Hamcrest Matchers API");
                return false;
            }
        };
    }

    static boolean isMatcherMethod(Method method) {
        int modifiers = method.getModifiers();
        return isStatic(modifiers) && isPublic(modifiers) && org.hamcrest.Matcher.class.isAssignableFrom(method.getReturnType());
    }

    static boolean isDeprecated(AnnotatedElement element) {
        return element.getAnnotation(Deprecated.class) != null;
    }

    private static String describe(Method method) {
        return method.getReturnType().getSimpleName() + " " + method.getName() + "(" + (method.getParameterCount() > 0 ? ".." : "") + ")";
    }


    private ApiHelper() {
    }
}
