package com.cmput301.cs.project.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class UserTest {

    @Test
    public void testCreateUser() {
        User user = new User("name");
    }

    @Test (expected = IllegalArgumentException.class)
    public void testEmptyUserName() {
        User user = new User("");
    }

    @Test (expected = IllegalArgumentException.class)
    public void testNullUserName(){
        User user = new User(null);
    }

    @Test
    public void testGetUserName() throws Exception {
        User user = new User("name");
        assertEquals("name", user.getUserName());
    }
}