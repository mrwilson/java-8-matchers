package uk.co.probablyfine.matchers;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static uk.co.probablyfine.matchers.Java8Matchers.where;
import static uk.co.probablyfine.matchers.Java8Matchers.whereNot;

class ApiInspectorTest {

    @Nested
    class IdentifiesEquivalentMethods {

        private final ApiInspector javaStringApi = new ApiInspector(String.class);
        private final Method equalsMethod;
        private final Method replaceMethod;
        private final Method replaceMethod2;
        private final Method incompatibleReplaceMethod;

        public IdentifiesEquivalentMethods() throws Exception {
            equalsMethod = MyString.class.getMethod("equals", Object.class);
            replaceMethod = MyString.class.getMethod("replace", CharSequence.class, CharSequence.class);
            replaceMethod2 = MyString.class.getMethod("replace", CharSequence.class, String.class);
            incompatibleReplaceMethod = MyString.class.getMethod("replace", CharSequence.class, Object.class);
        }

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

        @Test
        void compatibleArguments() {
            assertThat(equalsMethod, where(javaStringApi::hasEquivalentTo));
            assertThat(replaceMethod, where(javaStringApi::hasEquivalentTo));
            assertThat(replaceMethod2, where(javaStringApi::hasEquivalentTo));
        }

        @Test
        void incompatibleArguments() {
            assertThat(incompatibleReplaceMethod, whereNot(javaStringApi::hasEquivalentTo));
        }

    }

    @Nested
    class FindUndeprecatedVariantsOfDeprecatedMethodsTest {

        private final ApiInspector api = new ApiInspector(ApiWithDeprecatedMethods.class);
        private final Method emptyStringMethod;
        private final Method deprecatedEmptyMethod;
        private final Method deprecatedEmptyVariantMethod;

        public FindUndeprecatedVariantsOfDeprecatedMethodsTest() throws Exception {
            emptyStringMethod = ApiWithDeprecatedMethods.class.getMethod("emptyString");
            deprecatedEmptyMethod = ApiWithDeprecatedMethods.class.getMethod("empty");
            deprecatedEmptyVariantMethod = ApiWithDeprecatedMethods.class.getMethod("emptyVariant");
        }

        class ApiWithDeprecatedMethods {
            public boolean emptyString() {
                return false;
            }
            @Deprecated
            public boolean empty() {
                return emptyString();
            }
            @Deprecated
            public boolean emptyVariant() {
                return emptyString();
            }
        }

        @Test
        void findsDeprecatedMethods() {
            assertThat(api.getDeprecated().collect(toSet()), containsInAnyOrder(deprecatedEmptyMethod, deprecatedEmptyVariantMethod));
        }

        @Test
        void findsVariantsOfMethod() {
            Set<Method> relatedToDeprecatedEmpty = api.findRelatedOf(deprecatedEmptyMethod).collect(toSet());
            assertThat(relatedToDeprecatedEmpty, containsInAnyOrder(emptyStringMethod, deprecatedEmptyVariantMethod));
        }

    }





}
