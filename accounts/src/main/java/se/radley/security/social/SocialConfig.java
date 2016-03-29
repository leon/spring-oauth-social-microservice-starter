package se.radley.security.social;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UsersConnectionRepository;
import se.radley.security.social.mongo.MongoUsersConnectionRepository;

/**
 * Store the social connection data in mongodb instead of inmemory or jdbc
 */
@Configuration
public class SocialConfig extends SocialConfigurerAdapter {

    @Autowired
    MongoOperations mongoOperations;

    @Autowired
    ConnectionSignUp connectionSignUp;

    // Encryptor used to encrypt the accessToken, secret and refreshToken
    @Autowired
    TextEncryptor textEncryptor;

    @Override
    public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
        return new MongoUsersConnectionRepository(mongoOperations, connectionFactoryLocator, connectionSignUp, textEncryptor);
    }
}
