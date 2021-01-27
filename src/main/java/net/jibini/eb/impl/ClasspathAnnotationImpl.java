package net.jibini.eb.impl;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Implementation utilities for annotation and classpath scanning.
 *
 * The classpath is not scanned until the first call to {@link #find}. An
 * instance of each classpath object is lazily created and cached weakly upon
 * the first request for that object. Future requests for that object will
 * return the cached instance (if it has not been garbage collected).
 *
 * Classpath scan results are cached upon the first call to {@link #find}. If
 * any additional classes are added to the classpath, they will not be found.
 *
 * @author Zach Goethel
 */
public class ClasspathAnnotationImpl
{
    /**
     * Cache the classpath scan results for future use (calculate once, use
     * multiple times).
     */
    private static ClassInfoList cachedResults = null;

    public static Class<?> find(Class<?> annotation, String classSimpleName)
    {
        // Lazy load of threaded classpath scan results
        if (cachedResults == null)
            cachedResults = new ClassGraph()
                // Find all classes annotated with the given type
                .enableClassInfo()
                .enableAnnotationInfo()
                .scan(8)
                .getClassesWithAnnotation(annotation.getName());

        return cachedResults
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
        // Lazy creation/caching of each classpath object
        return (T)cache.computeIfAbsent(classSimpleName, (key) -> findAndCreate(Classpath.class, key));
    }
}
