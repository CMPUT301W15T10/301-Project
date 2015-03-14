package com.cmput301.cs.project.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

/**
 * This class allows for the creation of and association of a username and user id to a user that will allow
 *  the user to log into the application and use it. 
 */

public class User implements Parcelable {

    private final String name;
    private final UUID userId;

    public User(String userName) {
        this(userName, UUID.randomUUID().toString());
    }

    public User(String userName, String userId) {
        if(userName == null || userName.isEmpty()) {
            throw new IllegalArgumentException();
        }

        this.name = userName;
        this.userId = UUID.fromString(userId);
    }

    public String getUserName() {
        return name;
    }

    public UUID getUserId() {
        return userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        final User user = (User) o;

        if(!user.userId.equals(userId)) return false;
        if(!user.name.equals(name)) return false;

        return true;

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + userId.hashCode();

        return result;
    }

    protected User(Parcel in) {
        name = in.readString();
        userId = (UUID) in.readValue(UUID.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeValue(userId);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}