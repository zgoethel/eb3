package net.jibini.eb.auth

/**
 * Marks the annotated class as an authentication object. Classes must
 * implement a Kotlin function type `(AuthDetails?) -> Boolean` (or
 * `Function1<AuthDetails, Boolean>` in Java).
 *
 * @author Zach Goethel
 */
@Target(AnnotationTarget.CLASS)
@Retention
annotation class Authenticator