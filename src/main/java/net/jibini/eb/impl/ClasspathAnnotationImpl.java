package net.jibini.eb.impl;

import io.github.classgraph.ClassGraph;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Implementation utilities for annotation and classpath scanning.
 *
 * @author Zach Goethel
 */
public class ClasspathAnnotationImpl
{
    public static Class<?> find(Class<?> annotation, String classSimpleName)
    {
        return new ClassGraph()
                // Find all classes annotated with the given type
                .enableClassInfo()
                .enableAnnotationInfo()
                .scan(4)
                .getClassesWithAnnotation(annotation.getName())

                // Find the first class with the correct name
                .stream()
                .filter((classInfo) -> classInfo.getSimpleName().equals(classSimpleName))
                .findFirst()
                .orElseThrow()

                // Load the class into the classpath; allows creation
                .loadClass();
    }

    public static Object findAndCreate(Class<?> annotation, String classSimpleName)
    {
        try
        {
            return find(annotation, classSimpleName)
                    .getConstructor()
                    .newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex)
        {
            throw new RuntimeException("Failed create an instance of the specified annotated item", ex);
        }
    }

    /**
     * Cache classpath entities as space permits.
     */
    private static final Map<String, Object> cache = new WeakHashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> T findAndCreate(String classSimpleName)
    {
        return (T)cache.computeIfAbsent(classSimpleName, (key) -> findAndCreate(Classpath.class, key));
    }
}
