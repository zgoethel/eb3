package net.jibini.eb.auth

/**
 * Represents a form submission by the user in attempt to
 * authenticate
 *
 * @author Zach Goethel
 */
class LoginRequest(
    /**
     * The user's entered username.
     */
    val username: String,

    /**
     * The user's entered password.
     */
    val password: String
)