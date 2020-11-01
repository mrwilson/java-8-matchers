package uk.co.probablyfine.matchers;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

import static java.lang.reflect.Modifier.isPublic;
import static java.lang.reflect.Modifier.isStatic;

final class ApiHelper {

    public static boolean isMatcherMethod(Method method) {
        int modifiers = method.getModifiers();
        return isStatic(modifiers) && isPublic(modifiers) && org.hamcrest.Matcher.class.isAssignableFrom(method.getReturnType());
    }

    public static boolean isDeprecated(AnnotatedElement element) {
        return element.getAnnotation(Deprecated.class) != null;
    }

    public static String describe(Method method) {
        return method.getReturnType().getSimpleName() + " " + method.getName() + "(" + (method.getParameterCount() > 0 ? ".." : "") + ")";
    }

    private ApiHelper() {
    }
}
