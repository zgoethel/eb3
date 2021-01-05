package net.jibini.eb.auth.impl

import net.jibini.eb.auth.AuthDetails
import net.jibini.eb.auth.Authenticator

import org.slf4j.LoggerFactory

/**
 * An authentication implementation which blindly accepts all authentication attempts.
 *
 * @author Zach Goethel
 */
@Authenticator
class DumbAuthenticator : (AuthDetails) -> Boolean
{
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun invoke(p1: AuthDetails): Boolean
    {
        log.warn("The dumb authenticator is enabled!  All authentication will succeed!")

        return true;
    }
}