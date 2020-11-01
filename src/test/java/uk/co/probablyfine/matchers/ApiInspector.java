package uk.co.probablyfine.matchers;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;

final class ApiInspector {

    private final Map<String, List<Method>> apiMethodsByName;

    public ApiInspector(Class<?> apiClass) {
        this(apiClass, publicMethod -> true);
    }

    public ApiInspector(Class<?> apiClass, Predicate<? super Method> partOfApi) {
        this.apiMethodsByName = Stream.of(apiClass.getMethods())
            .filter(partOfApi)
            .collect(groupingBy(Method::getName));
    }

    public boolean hasEquivalentTo(Method method) {
        return apiMethodsByName.getOrDefault(method.getName(), emptyList()).stream()
                .anyMatch(hamcrestMethod -> acceptsArgumentsOfTypes(hamcrestMethod, method.getParameterTypes()));
    }

    public Stream<Method> allMethods() {
        return apiMethodsByName.values().stream().flatMap(List::stream);
    }

    public Stream<Method> getDeprecated() {
        return allMethods().filter(ApiHelper::isDeprecated);
    }

    public Stream<Method> findRelatedOf(Method method) {
        return allMethods()
                .filter(apiMethod -> apiMethod.getName().startsWith(method.getName()))
                .filter(related -> !related.equals(method));
    }

    private static boolean acceptsArgumentsOfTypes(Method method, Class<?> ... argTypes) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length != argTypes.length) {
            return false;
        } else {
            for (int i = 0; i < argTypes.length; i++) {
                if (!parameterTypes[i].isAssignableFrom(argTypes[i])) {
                    return false;
                }
            }
        }
        return true;
    }




}
