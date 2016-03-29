package se.radley.security.social;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.stereotype.Component;
import se.radley.user.User;
import se.radley.user.UserService;

import java.util.Optional;

@Component
public class SocialUserDetailsServiceImpl implements SocialUserDetailsService, UserDetailsService {

    private final UserService userService;

    @Autowired
    public SocialUserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public SocialUserDetails loadUserByUserId(final String email) throws UsernameNotFoundException, DataAccessException {
        Optional<User> userOptional = userService.findByEmail(email);
        return userOptional.orElseThrow(() -> new UsernameNotFoundException(email));
    }

    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        return loadUserByUserId(email);
    }

}
