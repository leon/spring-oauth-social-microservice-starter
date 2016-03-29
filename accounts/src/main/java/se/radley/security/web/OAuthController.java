package se.radley.security.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import se.radley.user.User;

@Controller
public class OAuthController {

    /*@RequestMapping(path = "/oauth2/authorize", method = RequestMethod.GET)
    public String authorize() {

    }*/

    /**
     * Return the currently signed in user
     * @param user the current user
     * @return
     */
    @RequestMapping("/api/user")
    @ResponseBody
    public User user(@AuthenticationPrincipal User user) {
        return user;
    }

}
