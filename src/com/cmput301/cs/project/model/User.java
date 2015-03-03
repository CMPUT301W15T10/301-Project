package com.cmput301_project.model;

import java.util.UUID;

public class User {

    private final String name;
    private final UUID userId;

    public String getUserName() {
        return name;
    }

    public UUID getUserId() {
        return userId;
    }


    public User(String userName) {
        this.name = userName;
        this.userId = UUID.randomUUID();
    }
}