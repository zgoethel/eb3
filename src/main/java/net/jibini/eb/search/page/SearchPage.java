package net.jibini.eb.search.page;

import net.jibini.eb.auth.AuthDetails;
import net.jibini.eb.auth.page.LoginPage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * The primary configurable search page. This is the primary page from which the
 * user will access and search for data. Each search function is flexible and
 * supports a wide variety of backend data sources.
 *
 * Search fields are aligned vertically on the left side of the page. Search
 * results are populated in a table in the center of the page. Additional fields
 * and elements may be added by extensions.
 *
 * No data on this page should be more than three clicks away.
 *
 * @author Zach Goethel
 */
@Controller
public class SearchPage
{
    // Required to authenticate the current session
    @Autowired
    private LoginPage loginPage;

    @GetMapping("/s")
    public String searchPage(
        HttpServletRequest request,
        HttpServletResponse response,

        HttpSession session
    )
    {
        // Authenticate the current session
        AuthDetails authDetails = loginPage.validate(session, request, response);
        if (authDetails == null)
            return "login";

        return "search";
    }
}
