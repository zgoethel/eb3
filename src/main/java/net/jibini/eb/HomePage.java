package net.jibini.eb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * The landing page of the EasyButton server, which can either send the user to marketing material or directly to the
 * login (depending on configuration).
 *
 * @author Zach Goethel
 */
@Controller
public class HomePage
{
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EasyButton easyButton;

    @GetMapping("/")
    public void homePage(
            HttpServletResponse response
    )
    {
        try
        {
            if (easyButton.config.getDefaultToLoginPage())
                response.sendRedirect("/login");
            else
                response.sendRedirect("/index.html");
        } catch (IOException ex)
        {
            log.error("Failed to respond with root page redirect", ex);
        }
    }
}
