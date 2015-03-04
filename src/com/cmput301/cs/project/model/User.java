package com.cmput301.cs.project.model;

import java.util.UUID;

public class User {

    private final String name;
    private final UUID userId;

    public User(String userName) {
        if(userName == null || userName.isEmpty()) {
            throw new IllegalArgumentException();
        }

        this.name = userName;
        this.userId = UUID.randomUUID();
    }

    public User(String userName, String userId) {
        this.name = userName;
        this.userId = UUID.fromString(userId);
    }

    public String getUserName() {
        return name;
    }

    public UUID getUserId() {
        return userId;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        final User user = (User) o;

        return user.userId == userId;

    }
}