package net.jibini.eb.auth;

import kotlin.jvm.functions.Function1;

import net.jibini.eb.EasyButton;
import net.jibini.eb.impl.ClasspathAnnotationImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * Page for handling user authentication and API authentication requests.
 *
 * @author Zach Goethel
 */
@Controller
public class LoginPage
{
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EasyButton easyButton;

    private Object auth = null;

    /**
     * Resorts to the configuration's primary authenticator instance to validate the provided credentials.  If no
     * authenticator has yet been created, the classpath will be searched for the configured authenticator.
     *
     * @param details User authentication details to validate.
     *
     * @return Whether the provided details are valid (true on successful validation).
     */
    @SuppressWarnings("unchecked")
    public boolean validate(AuthDetails details)
    {
        if (auth == null)
            auth = ClasspathAnnotationImpl.findAndCreate(Authenticator.class, easyButton.config.getPrimaryAuthenticator());

        return ((Function1<AuthDetails, Boolean>)auth).invoke(details);
    }

    @GetMapping("/login")
    public String loginPage(
            HttpServletResponse response,
            HttpSession session,

            @RequestParam(defaultValue = "/s") String redirect
    )
    {
        AuthDetails details = (AuthDetails)session.getAttribute("auth-details");

        if (details != null && validate(details))
            try
            {
                response.sendRedirect(redirect);
            } catch (IOException ex)
            {
                log.error("Failed to redirect successful pre-login", ex);
            }

        return "login";
    }

    @PostMapping("/login")
    public void loginAuthenticate(
            HttpServletResponse response,
            HttpSession session,

            @ModelAttribute("login-request") LoginRequest loginRequest
    )
    {
        AuthDetails details = new AuthDetails(loginRequest.getUsername().toLowerCase(), loginRequest.getPassword());
        session.setAttribute("auth-details", details);

        if (!validate(details))
            try
            {
                String error = URLEncoder.encode("Failed to login; please try again.", Charset.defaultCharset());

                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.sendRedirect("/login?error=" + error);

                return;
            } catch (IOException ex)
            {
                throw new RuntimeException("Failed to redirect back to login page for failed attempt", ex);
            }

        try
        {
            response.sendRedirect(loginRequest.getSuccessRedirect());
        } catch (IOException ex)
        {
            throw new RuntimeException("Failed to redirect to post-login page for successful attempt", ex);
        }
    }
}
