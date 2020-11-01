package uk.co.probablyfine.matchers;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.stream.Stream;

import static java.lang.reflect.Modifier.isPublic;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.stream.Collectors.joining;

final class ApiHelper {

    public static boolean isMatcherMethod(Method method) {
        int modifiers = method.getModifiers();
        return isStatic(modifiers) && isPublic(modifiers) && org.hamcrest.Matcher.class.isAssignableFrom(method.getReturnType());
    }

    public static boolean isDeprecated(AnnotatedElement element) {
        return element.getAnnotation(Deprecated.class) != null;
    }

    public static String describe(Method method) {
        String parameters = Stream.of(method.getParameterTypes()).map(Class::getSimpleName).collect(joining(","));
        return method.getReturnType().getSimpleName() + " " + method.getName() + "(" + parameters + ")";
    }

    private ApiHelper() {
    }
}
