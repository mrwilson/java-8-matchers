package uk.co.probablyfine.matchers;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.hamcrest.MatcherAssert.assertThat;
import static uk.co.probablyfine.matchers.Java8Matchers.where;
import static uk.co.probablyfine.matchers.Java8Matchers.whereNot;

class ApiInspectorTest {

    private final ApiInspector javaStringApi = new ApiInspector(String.class);
    private final Method equalsMethod;
    private final Method replaceMethod;
    private final Method replaceMethod2;
    private final Method incompatibleReplaceMethod;

    class MyString {
        public String replace(CharSequence target, CharSequence replacement) {
            return "placeholder";
        }

        public String replace(CharSequence target, String replacement) {
            return "placeholder";
        }

        public String replace(CharSequence target, Object replacement) {
            return "placeholder";
        }
    }

    public ApiInspectorTest() throws Exception {
        equalsMethod = MyString.class.getMethod("equals", Object.class);
        replaceMethod = MyString.class.getMethod("replace", CharSequence.class, CharSequence.class);
        replaceMethod2 = MyString.class.getMethod("replace", CharSequence.class, String.class);
        incompatibleReplaceMethod = MyString.class.getMethod("replace", CharSequence.class, Object.class);
    }

    @Test
    void identifiesCompatibleArguments() {
        assertThat(equalsMethod, where(javaStringApi::hasEquivalentTo));
        assertThat(replaceMethod, where(javaStringApi::hasEquivalentTo));
        assertThat(replaceMethod2, where(javaStringApi::hasEquivalentTo));
    }

    @Test
    void identifiesIncompatibleArguments() {
        assertThat(incompatibleReplaceMethod, whereNot(javaStringApi::hasEquivalentTo));
    }


}
