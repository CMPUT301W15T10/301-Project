package com.cmput301.cs.project;

import com.cmput301.cs.project.model.User;
import org.junit.Test;

import java.util.UUID;

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

    @Test
    public void testGetUserId() throws Exception {
        User user = new User("name");
        assertNotNull(user.getUserId());
    }

    @Test
    public void testGetUserIdUnique() throws Exception {
        User user1 = new User("name");
        User user2 = new User("name");
        assertNotEquals(user1.getUserId(), user2.getUserId());
    }


    @Test
    public void testConstructUserWithId() throws Exception {
        final UUID uuid = UUID.randomUUID();
        final String name = "name";
        final User user = new User(name, uuid.toString());

        assertEquals(uuid, user.getUserId());
        assertEquals(name, user.getUserName());

    }

}