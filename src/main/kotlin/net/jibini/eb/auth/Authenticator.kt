package net.jibini.eb.auth

import net.jibini.eb.impl.Classpath

/**
 * Any type which can take in credentials and validate them.
 * Implementing classes must be annotated with [Classpath].
 *
 * @author Zach Goethel
 */
interface Authenticator : (AuthDetails?) -> Boolean