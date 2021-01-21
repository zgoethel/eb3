package net.jibini.eb.auth

import java.util.*

/**
 * Represents a set of authentication credentials (username and
 * password).
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
{
    val basicAuth: String
        get() = "Basic ${Base64.getEncoder().encodeToString("$username:$password".toByteArray())}"
}