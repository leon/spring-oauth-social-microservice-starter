package se.radley.security.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.JwtAccessTokenConverterConfigurer;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

/**
 * Configure Jwt so that is converts the user using our custom UserJwtTokenConverter
 */
@Configuration
public class JwtConfig implements JwtAccessTokenConverterConfigurer {

    @Autowired
    private UserJwtTokenConverter userJwtTokenConverter;

    @Value("${jwt.key}")
    private String jwtKey;

    @Override
    public void configure(JwtAccessTokenConverter converter) {
        DefaultAccessTokenConverter accessTokenConverter = new DefaultAccessTokenConverter();
        accessTokenConverter.setUserTokenConverter(userJwtTokenConverter);
        converter.setAccessTokenConverter(accessTokenConverter);
        converter.setSigningKey(jwtKey);
        converter.setVerifierKey(jwtKey);
    }
}
