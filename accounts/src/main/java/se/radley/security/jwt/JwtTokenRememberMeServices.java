package se.radley.security.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.stereotype.Component;
import se.radley.user.User;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;

/*@Component("rememberMeServices")
public class JwtTokenRememberMeServices extends TokenBasedRememberMeServices {

    private final JwtService jwtService;

    @Autowired
    public JwtTokenRememberMeServices(@Value("${jwt.key}") String key, JwtService jwtService, UserDetailsService userDetailsService) {
        super(key, userDetailsService);

        this.jwtService = jwtService;

        // Always remember
        setAlwaysRemember(true);

        // Set validity to one year
        setTokenValiditySeconds(31536000);

        // Set cookie name
        setCookieName("token");
        setParameter("token");
    }

    @Override
    protected void onLoginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication successfulAuthentication) {

        User user = retrieveUser(successfulAuthentication);

        if (user == null) {
            logger.debug("Could not get user");
            return;
        }

        int tokenLifetime = getTokenValiditySeconds();
        long expiryTime = System.currentTimeMillis();
        // SEC-949
        expiryTime += 1000L * (tokenLifetime < 0 ? TWO_WEEKS_S : tokenLifetime);

        String jwt = jwtService.encode(new UserJwtToken(user));

        Cookie cookie = new Cookie(getCookieName(), jwt);
        cookie.setMaxAge((int) expiryTime);
        cookie.setPath("/");
        response.addCookie(cookie);

        logger.debug("Added jwt cookie for user '" + user.getEmail() + "', expiry: '" + new Date(expiryTime) + "'");
    }

    @Override
    protected UserDetails processAutoLoginCookie(String[] cookieTokens, HttpServletRequest request, HttpServletResponse response) throws RememberMeAuthenticationException, UsernameNotFoundException {
        if (cookieTokens.length != 1) {
            throw new InvalidCookieException("Cookie token did not contain jwt, but contained '" + Arrays.asList(cookieTokens) + "'");
        }

        UserJwtToken token = jwtService.decode(cookieTokens[0]);

        return getUserDetailsService().loadUserByUsername(token.getEmail());
    }

    @Override
    protected String[] decodeCookie(String cookieValue) throws InvalidCookieException {
        String[] tokens = new String[1];
        tokens[0] = cookieValue;
        return tokens;
    }

    protected User retrieveUser(Authentication authentication) {
        if (authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }
}
*/
