package net.jibini.eb.impl

/**
 * Marks the annotated class as an object which can be found via
 * [classpath scanning][ClasspathAnnotationImpl].
 *
 * @author Zach Goethel
 */
@Target(AnnotationTarget.CLASS)
@Retention
annotation class Classpath