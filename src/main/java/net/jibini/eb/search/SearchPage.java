package net.jibini.eb.search;

import net.jibini.eb.auth.AuthDetails;
import net.jibini.eb.auth.LoginPage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class SearchPage
{
    @Autowired
    private LoginPage loginPage;

    @GetMapping("/s")
    public String searchPage(
        HttpServletRequest request,
        HttpServletResponse response,

        HttpSession session
    )
    {
        AuthDetails authDetails = loginPage.validate(session, request, response);
        if (authDetails == null)
            return "login";

        return "search";
    }
}
