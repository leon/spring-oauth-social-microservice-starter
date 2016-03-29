package se.radley.security.jwt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import se.radley.user.Role;
import se.radley.user.User;

import java.util.Set;

public class UserJwtToken {
    private final String iis = "https://accounts.radley.se";
    private final String email;
    private final String firstName;
    private final String lastName;
    private final Set<Role> roles;

    @JsonCreator
    public UserJwtToken(
        @JsonProperty("email") String email,
        @JsonProperty("firstName") String firstName,
        @JsonProperty("lastName") String lastName,
        @JsonProperty("roles") Set<Role> roles
    ) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roles = roles;
    }

    public UserJwtToken(User user) {
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.roles = user.getRoles();
    }

    //region Getters and Setters

    public String getIis() {
        return iis;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    //endregion
}
