package net.jibini.eb.auth.page;

import net.jibini.eb.EasyButton;
import net.jibini.eb.auth.AuthDetails;
import net.jibini.eb.auth.Authenticator;
import net.jibini.eb.auth.LoginRequest;
import net.jibini.eb.impl.ClasspathAnnotationImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * Page for handling user authentication and API authentication requests.
 * Requires a primary {@link Authenticator} to be configured.
 *
 * Secured pages can redirect to this page, providing the return URL to which
 * the user will be sent upon successful authentication. The user will be
 * returned to the login page with an error message upon unsuccessful login.
 *
 * All pages should share this common login and authentication system in
 * order to maintain consistent and central account control.
 *
 * @author Zach Goethel
 */
@Controller
public class LoginPage
{
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    // Required to access the related configuration
    @Autowired
    private EasyButton easyButton;

    /**
     * Cached primary authentication object, loaded and created from the
     * classpath as configured in the primary configuration.
     */
    private Authenticator auth = null;

    /**
     * Resorts to the configuration's primary authenticator instance to validate
     * the provided credentials. If no authenticator has yet been created, the
     * classpath will be searched for the configured authenticator.
     *
     * A successful validation using this method indicates that the provided
     * account is valid; it does not imply that the given account should be
     * allowed to access anything and everything. Also validate that this account
     * is permitted to access the secured or restricted data.
     *
     * @param details User authentication details to validate.
     * @return Whether the provided authentication details are valid.
     */
    public boolean validate(AuthDetails details)
    {
        if (auth == null)
            auth = ClasspathAnnotationImpl.findAndCreate(easyButton.config.getPrimaryAuthenticator());

        return auth.invoke(details);
    }

    /**
     * Determines whether the current session is valid. If invalid, this call
     * will redirect the user to the login page in order to log in and be
     * redirected back to their desired page.
     *
     * A successful validation using this method indicates that the provided
     * account is valid; it does not imply that the given account should be
     * allowed to access anything and everything. Also validate that this account
     * is permitted to access the secured or restricted data.
     *
     * @param session Session to be validated.
     * @param response Vector by which to redirect the user.
     * @return Whether the provided session is valid.
     */
    public AuthDetails validate(HttpSession session, HttpServletRequest request, HttpServletResponse response)
    {
        AuthDetails details = (AuthDetails)session.getAttribute("auth-details");

        if (!validate(details))
        {
            try
            {
                String redirect = request.getRequestURI();
                if (request.getQueryString() != null)
                    redirect += "?" + request.getQueryString();

                redirect = URLEncoder.encode(redirect, Charset.defaultCharset());

                response.sendRedirect("/login?redirect=" + redirect);
            } catch(IOException ex)
            {
                log.error("Failed to redirect the user to the login page", ex);
            }

            return null;
        }

        return details;
    }

    @GetMapping("/login")
    public String loginPage(
            HttpServletResponse response,
            HttpSession session,

            Model model,

            @RequestParam(defaultValue = "/s") String redirect,
            @RequestParam(defaultValue = "") String error
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

        model.addAttribute("redirect", redirect);
        model.addAttribute("error", error);

        return "login";
    }

    @PostMapping("/login")
    public void loginAuthenticate(
            HttpServletResponse response,
            HttpSession session,

            @ModelAttribute LoginRequest loginRequest
    )
    {
        AuthDetails details = new AuthDetails(loginRequest.getUsername().toLowerCase(), loginRequest.getPassword());
        session.setAttribute("auth-details", details);

        if (!validate(details))
            try
            {
                String error = URLEncoder.encode("Failed to login; please try again.", Charset.defaultCharset());
                String redirect = URLEncoder.encode(loginRequest.getSuccessRedirect(), Charset.defaultCharset());

                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.sendRedirect("/login?error=" + error + "&redirect=" + redirect);

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
