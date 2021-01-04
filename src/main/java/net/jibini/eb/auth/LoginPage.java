package net.jibini.eb.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginPage
{
    @GetMapping("/login")
    public String loginPage(
            @RequestParam(defaultValue = "/a") String redirect
    )
    {

        return "login";
    }

    @PostMapping("/login")
    public void loginPost(
            @ModelAttribute("login-request") LoginRequest request
    )
    {

    }
}
