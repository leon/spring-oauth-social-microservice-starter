package se.radley.security.jwt;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.stereotype.Component;
import se.radley.user.User;
import se.radley.user.UserService;

import java.util.Map;
import java.util.Optional;

/**
 * This service encodes and decodes the extra information that is available in the JWT token
 */
@Component
public class UserJwtTokenConverter implements UserAuthenticationConverter {

    private final UserService userService;
    private final ObjectMapper mapper;

    @Autowired
    public UserJwtTokenConverter(UserService userService, ObjectMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    @Override
    public Map<String, ?> convertUserAuthentication(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        UserJwtToken token = new UserJwtToken(user);
        return mapper.convertValue(token, new TypeReference<Map<String, ?>>() {});
    }

    @Override
    public Authentication extractAuthentication(Map<String, ?> map) {
        if (map.containsKey("email")) {
            UserJwtToken token = mapper.convertValue(map, UserJwtToken.class);
            Optional<User> userOptional = userService.findByEmail(token.getEmail());
            if (userOptional.isPresent()) {
                return new UsernamePasswordAuthenticationToken(userOptional.get(), "N/A", userOptional.get().getAuthorities());
            }
        }
        return null;
    }
}
