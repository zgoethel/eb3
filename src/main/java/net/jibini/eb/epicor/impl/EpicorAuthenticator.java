package net.jibini.eb.epicor.impl;

import net.jibini.eb.auth.AuthDetails;
import net.jibini.eb.auth.Authenticator;
import net.jibini.eb.epicor.EpicorCall;
import net.jibini.eb.epicor.EpicorException;
import net.jibini.eb.impl.Classpath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * An authenticator implementation which attempts to invoke the Epicor API v1
 * base URL. A failure to authenticate with the API indicates invalid
 * credentials. Authentication succeeds upon successful connection to the API.
 *
 * Any user with a valid Epicor account and the matching credentials will be
 * able to successfully validate their account. Each account's access to the
 * Epicor backend is still limited by the Epicor account access control.
 * EasyButton web panel operations will only have access to what the user
 * account has access to; there is no privileged link to Epicor, so actions
 * may have to be validated to ensure the user has required permissions.
 *
 * Placing this barrier for Epicor account validation on a per-session basis
 * reduces the potential for an account escalation attack against the backend.
 *
 * @author Zach Goethel
 */
@Classpath
public class EpicorAuthenticator implements Authenticator
{
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public Boolean invoke(AuthDetails authDetails)
    {
        if (authDetails == null)
            return false;

        try
        {
            // Attempt to invoke base API path
            EpicorCall baseCall = new EpicorCall("");
            baseCall.call(authDetails, new HashMap<>());
        } catch (EpicorException ex)
        {
            log.error("Failed to authenticate with Epicor backend", ex);

            if (ex.getMessage().contains("401"))
                return false;
            else
                throw ex;
        }

        // Call succeeded, thus authentication was valid
        return true;
    }
}
