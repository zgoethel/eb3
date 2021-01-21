package net.jibini.eb.epicor;

import kotlin.jvm.functions.Function1;

import net.jibini.eb.auth.AuthDetails;
import net.jibini.eb.auth.Authenticator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * An authenticator implementation which attempts to invoke the Epicor API v1
 * base URL. A failure to authenticate with the API indicates invalid
 * credentials. Authentication succeeds upon successful connection to the API.
 *
 * @author Zach Goethel
 */
@Authenticator
public class EpicorAuthenticator implements Function1<AuthDetails, Boolean>
{
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public Boolean invoke(AuthDetails authDetails)
    {
        try
        {
            // Attempt to invoke base API path
            EpicorCall baseCall = new EpicorCall("");
            baseCall.call(authDetails, new HashMap<>());
        } catch (EpicorException ex)
        {
            log.error("Failed to authenticate with Epicor backend", ex);

            return false;
        }

        // Call succeeded, thus authentication was valid
        return true;
    }
}
