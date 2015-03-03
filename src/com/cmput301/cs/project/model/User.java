package com.cmput301.cs.project.model;

import java.util.UUID;

public class User {

    private final String name;
    private final UUID userId;

    public User(String userName) {
        this.name = userName;
        this.userId = UUID.randomUUID();
    }

    public String getUserName() {
        return name;
    }

    public UUID getUserId() {
        return userId;
    }
}