package se.radley.user;

public enum Role {
    // Everyone is a user
    USER,
    ADMIN,

    // And then you have one or more roles
    MANAGER,
    SALES,
    PROJECT_MANAGER,
    DEVELOPER,
    SUPPORT;
}
