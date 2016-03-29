package se.radley.security.social.mongo;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.*;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

public class MongoUsersConnectionRepository implements UsersConnectionRepository {

    private final MongoOperations mongo;
    private final ConnectionFactoryLocator connectionFactoryLocator;
    private final ConnectionSignUp connectionSignUp;
    private final TextEncryptor textEncryptors;

    public MongoUsersConnectionRepository(final MongoOperations mongo, final ConnectionFactoryLocator connectionFactoryLocator, final ConnectionSignUp connectionSignUp, final TextEncryptor textEncryptors) {
        this.mongo = mongo;
        this.connectionFactoryLocator = connectionFactoryLocator;
        this.connectionSignUp = connectionSignUp;
        this.textEncryptors = textEncryptors;
    }

    @Override
    public List<String> findUserIdsWithConnection(final Connection<?> connection) {
        ConnectionKey key = connection.getKey();
        Query query = query(where("providerId").is(key.getProviderId()).and("providerUserId").is(key.getProviderUserId()));
        query.fields().include("userId");
        List<String> localUserIds = mongo.find(query, MongoConnection.class).stream().map(MongoConnection::getUserId).collect(Collectors.toList());

        if (localUserIds.isEmpty() && connectionSignUp != null) {
            String newUserId = connectionSignUp.execute(connection);
            if (newUserId != null) {
                createConnectionRepository(newUserId).addConnection(connection);
                return Collections.singletonList(newUserId);
            }
        }
        return localUserIds;
    }

    @Override
    public Set<String> findUserIdsConnectedTo(final String providerId, final Set<String> providerUserIds) {
        Query query = query(where("providerId").is(providerId).and("providerUserId").in(providerUserIds));
        query.fields().include("userId");
        return mongo.find(query, MongoConnection.class).stream().map(MongoConnection::getUserId).collect(Collectors.toSet());
    }

    @Override
    public ConnectionRepository createConnectionRepository(final String userId) {
        Assert.notNull(userId, "userId must be defined");
        return new MongoConnectionRepository(userId, mongo, connectionFactoryLocator, textEncryptors);
    }
}
