package uk.co.probablyfine.matchers;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.lang.reflect.Method;

public class HamcrestApiMatchers {

    static Matcher<Method> existsInHamcrest() {
        return new TypeSafeDiagnosingMatcher<Method>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Matcher method with equivalent in Hamcrest Matchers API");
            }

            @Override
            protected boolean matchesSafely(Method method, Description mismatchDescription) {
                if (ApiHelper.isMatcherMethod(method) && HamcrestApiMatchers.hamcrestApi.hasEquivalentTo(method)) {
                    return true;
                }
                mismatchDescription.appendText(ApiHelper.describe(method) + " does not exist in Hamcrest Matchers API");
                return false;
            }
        };
    }

    static final ApiInspector hamcrestApi = new ApiInspector(org.hamcrest.Matchers.class, ApiHelper::isMatcherMethod);

}
