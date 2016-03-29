package se.radley.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import se.radley.user.event.UserCreatedEvent;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository repository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public UserService(UserRepository repository, ApplicationEventPublisher applicationEventPublisher) {
        this.repository = repository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public Optional<User> findById(String id) {
        return Optional.ofNullable(repository.findOne(id));
    }

    public Optional<User> findByEmail(String email) {
        return repository.findOneByEmail(email);
    }

    /**
     * Create user and publish event so that we can send them a welcome email and more.
     * @param email New userser email
     * @param firstName New user first name
     * @param lastName New user last name
     * @return the new user
     */
    public User create(String email, String firstName, String lastName) {
        User newUser = new User(email, firstName, lastName);
        repository.save(newUser);
        applicationEventPublisher.publishEvent(new UserCreatedEvent(newUser));
        return newUser;
    }

}
