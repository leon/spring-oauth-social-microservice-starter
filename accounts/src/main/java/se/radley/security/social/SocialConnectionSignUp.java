package se.radley.security.social;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UserProfile;
import org.springframework.stereotype.Service;
import se.radley.user.User;
import se.radley.user.UserService;

import java.util.Optional;

@Service
public class SocialConnectionSignUp implements ConnectionSignUp {

    private static final Logger log = LoggerFactory.getLogger(SocialConnectionSignUp.class);

    private final UserService userService;

    @Autowired
    public SocialConnectionSignUp(UserService userService) {
        this.userService = userService;
    }

    public String execute(Connection<?> connection) {
        UserProfile profile = connection.fetchUserProfile();

        // If the user already exists we return the user id (email)
        Optional<User> user = userService.findByEmail(profile.getEmail());
        if (user.isPresent()) {
            log.info("Connecting existing account to " + connection.getKey().getProviderId());
            return user.get().getEmail();
        }

        // The user doesn't exist so we need to create them
        User newUser = userService.create(profile.getEmail(), profile.getFirstName(), profile.getLastName());

		log.info("Creating new user {} from {}", newUser.getEmail(), connection.getKey().getProviderId());

        return newUser.getEmail();
    }

}