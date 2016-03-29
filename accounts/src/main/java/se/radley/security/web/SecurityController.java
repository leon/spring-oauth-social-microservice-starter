package se.radley.security.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import se.radley.user.User;

@Controller
public class SecurityController {

    /**
     * View that we get redirected to when one of the clients need to login, or when we get signed out.
     * @return Signin View
     */
    @RequestMapping("/signin")
    public String signin() {
        return "security/signin";
    }
}
