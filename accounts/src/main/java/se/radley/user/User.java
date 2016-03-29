package se.radley.user;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.validator.constraints.Email;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.social.security.SocialUserDetails;

import java.util.*;
import java.util.stream.Collectors;

@Document
public class User implements SocialUserDetails {

    @Id
    private String id;

    @Indexed
    @Email
    private String email;

    private String firstName;
    private String lastName;
    private Set<Role> roles = new LinkedHashSet<>();
    private String phone;
    private String mobile;
    private boolean active;

    @Transient
    @JsonIgnore
    private String password;

    //region Constructors


    public User() {}

    /*public User(String email, Collection<? extends GrantedAuthority> authorities) {
        this.email = email;
        this.authorities.addAll(authorities);
    }*/

    public User(String email, String firstName, String lastName) {
        this.email = email;

        // Fake password
        this.password = KeyGenerators.string().generateKey();

        this.firstName = firstName;
        this.lastName = lastName;

        this.roles.add(Role.USER);

        this.active = true;
    }

    //endregion


    //region Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    //endregion

    //region UserDetails
    @Override
    @JsonIgnore
    public String getUserId() {
        return id;
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r.name())).collect(Collectors.toSet());
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return email;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return active;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return active;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return active;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return active;
    }
    //endregion
}
