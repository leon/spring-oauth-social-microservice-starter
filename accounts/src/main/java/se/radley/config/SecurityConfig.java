package se.radley.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.ManagementServerProperties;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.repository.query.spi.EvaluationContextExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.social.security.SpringSocialConfigurer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import se.radley.security.SecurityEvaluationContextExtension;
import se.radley.security.jwt.UserJwtTokenConverter;

import java.security.Principal;

@Configuration
@SessionAttributes("authorizationRequest")
@EnableGlobalMethodSecurity(securedEnabled = true)
class SecurityConfig {

    @Bean
    public TextEncryptor textEncryptor(@Value("${application.secret}") CharSequence secret) {
        return Encryptors.delux(secret, secret);
    }

    /**
     * Extend SpEL with security concerns
     * @return
     */
    @Bean
    public EvaluationContextExtension securityExtension() {
        return new SecurityEvaluationContextExtension();
    }

    /**
     * The Resource Server only listens to /api/ calls
     */
    @Configuration
    @EnableResourceServer
    // @Order(ManagementServerProperties.ACCESS_OVERRIDE_ORDER - 10)
    // By default the ResourceServer will be placed in front of the Management apis and will thus take precedence
    protected static class ResourceServerConfig extends ResourceServerConfigurerAdapter {

        @Override
        public void configure(final HttpSecurity http) throws Exception {
            // @formatter:off
            http
                // Only listen to /api/** calls
                .requestMatchers().antMatchers("/api/**").and()
                .authorizeRequests()
                    .antMatchers("/api/user").hasRole("USER")
                    .anyRequest().authenticated();
            // @formatter:on
        }
    }

    /**
     * We place the web forms login and social login at the bottom, so that the resource server and authorization server has precedence
     */
    @Configuration
    @Order(ManagementServerProperties.ACCESS_OVERRIDE_ORDER - 9)
    protected static class LoginConfig extends WebSecurityConfigurerAdapter {

        /**
         * MongoDB backed userDetailsService
         */
        @Autowired
        private UserDetailsService userDetailsService;

        /**
         * Default password encrypter, in this case BCrypt
         * @return
         */
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        public DaoAuthenticationProvider daoAuthenticationProvider() {
            DaoAuthenticationProvider dao = new DaoAuthenticationProvider();
            dao.setUserDetailsService(userDetailsService);
            dao.setPasswordEncoder(passwordEncoder());
            return dao;
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.authenticationProvider(daoAuthenticationProvider());
        }

        // Expose the configured daoAuthenticationManager so that the ResourceServer and AuthorizationServer can use it also
        @Override
        @Bean
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // @formatter:off
            http
                .requestMatchers().antMatchers(
                "/",
                "/manage/**",
                "/signin",
                "/auth/**",
                "/password/**",
                "/oauth/authorize",
                "/oauth/confirm_access"
            ).and()
                .authorizeRequests()
                .antMatchers("/signup", "/auth/**", "/password/**").permitAll()
                .antMatchers("/manage/**").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/signin")
                .loginProcessingUrl("/signin/authenticate")
                .permitAll()
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/signout")).permitAll()
                .and()
                .apply(new SpringSocialConfigurer().postLoginUrl("/"));
            // @formatter:on
        }
    }

    @Configuration
    @EnableAuthorizationServer
    protected static class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

        /**
         * The global dao authentication manager
         */
        @Autowired
        private AuthenticationManager authenticationManager;

        @Autowired
        private UserJwtTokenConverter userJwtTokenConverter;

        @Value("${jwt.key}")
        private String jwtKey;

        @Bean
        public JwtAccessTokenConverter jwtAccessTokenConverter() {
            JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
            converter.setSigningKey(jwtKey);
            converter.setVerifierKey(jwtKey);

            //Customize how our jwt looks like
            DefaultAccessTokenConverter tokenConverter = new DefaultAccessTokenConverter();
            tokenConverter.setUserTokenConverter(userJwtTokenConverter);
            converter.setAccessTokenConverter(tokenConverter);

            return converter;
        }


        /**
         * Configure the oauth server to allow all clients to access tokens as long as they are authenticated
         * against this authorization server.
         * @param oauthServer
         * @throws Exception
         */
        @Override
        public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
            oauthServer
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()");
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            endpoints.authenticationManager(authenticationManager).accessTokenConverter(jwtAccessTokenConverter());
        }

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients.inMemory()
                /**
                 * Service to Service Clients
                 *
                 * Here we specify all the microservices that are going to be able to communicate within this cluster
                 * of microservices.
                 *
                 * Since these microservices shouldn't be able to login, we only allow them the client_credentials, refresh_token
                 * grant types.
                 *
                 * The JWT token that comes out if this will be a client_credentials JWT token, and only contain the
                 * id of the client and the assigned scopes that it has permission to perform.
                 *
                 * By specifying them separately we can give them different scopes which we use to determine permissions
                 * between the different microservices.
                 *
                 * Example: The comments-service should be able to ask the accounts-service about users. Therefore
                 * we give it the scope of accounts-user-read
                 */
                .withClient("accounts-service")
                    .secret("DrKkZnUhuGewHRJGba3OVj0O7BSkD3uh5qJ6xnYbHc5Wt4NjhmsKXrkYHLrJZbd3")
                    .authorizedGrantTypes("client_credentials", "refresh_token")
                    .autoApprove(true)
                    .and()

                .withClient("comments-service")
                    .secret("LraHqYEosk6WwZ01NiCz5MWfItVD8RbzQ7xMr105AUkmbEDuF2vA7OxVBw6oVexa")
                    .authorizedGrantTypes("client_credentials", "refresh_token")
                    .scopes("accounts-user-read")
                    .autoApprove(true)
                    .and()

                /**
                 * User to Service Clients
                 *
                 * Here we specify all the UI's that should be able to login to the authorization server.
                 * It could be a single page application (SPA) or a mobile application or ...
                 *
                 * When the user logs in to the authorization server a custom JWT token will be created that will
                 * contain the users id, email, firstname, lastname, roles and any other information you find valuable
                 * to forward to all the microservices.
                 *
                 * Keep in mind though that for every request this will be serialized and sent over the wire. So it's
                 * a matter of keeping it to the minimum, whilst having enough data to not have to make to many roundtrips
                 * to the authorization server.
                 */
                .withClient("front")
                    .secret("pshgpMItylY04IEOglLzy38R51ZoAh8qc4Iayv3665CImEyD6wBJ7bJqpNdCRxU0")
                    .authorizedGrantTypes("authorization_code", "refresh_token", "password")
                    .scopes("openid")
                    .autoApprove(true);
        }
    }
}
