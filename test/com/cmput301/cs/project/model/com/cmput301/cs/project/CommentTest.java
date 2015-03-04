package com.cmput301.cs.project;

import com.cmput301.cs.project.model.Comment;
import com.cmput301.cs.project.model.User;
import org.junit.Test;

import static org.junit.Assert.*;

public class CommentTest {

    @Test
    public void testCreateComment(){
        new Comment("My comment", new User("Name"));
    }

    @Test (expected = IllegalArgumentException.class)
    public void testCreateNullComment() {
        new Comment(null, new User("Name"));
    }
    @Test (expected = IllegalArgumentException.class)
    public void testCreateEmptyComment() {
        new Comment("", new User("Name"));
    }

    @Test (expected = IllegalArgumentException.class)
    public void testCreateNullUser() {
        new Comment("a comment", null);
    }

    @Test
    public void testGetApprover() throws Exception {
        User approver = new User("The approver");

        Comment comment = new Comment("A valid comment", approver);

        assertEquals(approver, comment.getApprover());
    }

    @Test
    public void testGetText() throws Exception {
        Comment comment = new Comment("A valid comment", new User("My user"));

        assertEquals("A valid comment",comment.getText());
    }
}