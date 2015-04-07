package com.cmput301.cs.project.models;

import java.util.UUID;

import junit.framework.TestCase;

public class UserTest extends TestCase {

    public void testCreateUser() {
        new User("name");
    }

    public void testEmptyUserName() {
        try {
            new User("");
            fail();
        } catch (IllegalArgumentException e) {
            // Success
        }
    }

    public void testNullUserName() {
        try {
        new User((String) null);
        fail();
        } catch (IllegalArgumentException e) {
            // Success
        }
    }

    public void testGetUserName() throws Exception {
        User user = new User("name");
        assertEquals("name", user.getUserName());
    }

    public void testGetUserId() throws Exception {
        User user = new User("name");
        assertNotNull(user.getId());
    }

    public void testGetUserIdUnique() throws Exception {
        User user1 = new User("name");
        User user2 = new User("name");
        assertTrue(!user1.getId().equals(user2.getId()));
    }

    public void testConstructUserWithId() throws Exception {
        final UUID uuid = UUID.randomUUID();
        final String name = "name";
        final User user = new User(name, uuid.toString());

        assertEquals(uuid.toString(), user.getId());
        assertEquals(name, user.getUserName());

    }

    public void testEquality() {
        final UUID uuid = UUID.randomUUID();
        final String name = "name";

        final User user = new User(name, uuid.toString());
        final User carbonCopy = new User(name, uuid.toString());

        assertEquals(user, user);
        assertEquals(user, carbonCopy);
        assertEquals(user.hashCode(), carbonCopy.hashCode());
    }

    public void testInequality() {
        final UUID uuid = UUID.randomUUID();
        final UUID uuid2 = UUID.randomUUID();
        final String name = "name";
        final String name2 = "name2";

        final User user = new User(name, uuid.toString());
        final User almostCopy1 = new User(name2, uuid.toString());
        final User almostCopy2 = new User(name, uuid2.toString());
        final User almostCopy3 = new User(name2, uuid2.toString());

        assertTrue(!user.equals(almostCopy1));
        assertTrue(!(user.hashCode() == almostCopy1.hashCode()));

        assertTrue(!user.equals(almostCopy2));
        assertTrue(!(user.hashCode() == almostCopy2.hashCode()));

        assertTrue(!user.equals(almostCopy3));
        assertTrue(!(user.hashCode() == almostCopy3.hashCode()));
    }
}