package net.jibini.eb.auth

/**
 * Represents a set of authentication credentials (username and password).
 *
 * @author Zach Goethel
 */
class AuthDetails(
    /**
     * The user's lower-cased username.
     */
    val username: String,

    /**
     * The user's authentication password.
     */
    val password: String
)