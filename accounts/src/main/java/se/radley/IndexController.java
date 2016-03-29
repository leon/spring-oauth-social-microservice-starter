package se.radley;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import se.radley.user.User;

@Controller
public class IndexController {

    @RequestMapping("/")
    public String index(Model model, @AuthenticationPrincipal(errorOnInvalidType = true) User user) {
        model.addAttribute(user);
        return "index";
    }
}
