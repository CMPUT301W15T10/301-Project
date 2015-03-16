package com.cmput301.cs.project.test.models;



import com.cmput301.cs.project.activities.ClaimListActivity;
import com.cmput301.cs.project.model.Comment;
import com.cmput301.cs.project.model.User;

import android.test.ActivityInstrumentationTestCase2;

public class CommentTest extends ActivityInstrumentationTestCase2<ClaimListActivity> {

	public CommentTest() {
		super(ClaimListActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testCreateComment(){
        Comment comment = new Comment("My comment", new User("Name"));
        assertEquals("My comment", comment.getText());
    }

    public void testCreateNullComment() {
        Comment comment = new Comment(null, new User("Name"));
        assertEquals(null, comment.getText());
    }
    public void testCreateEmptyComment() {
        Comment comment = new Comment("", new User("Name"));
        assertEquals("something", "", comment.getText());
    }

    public void testCreateNullUser() {
        new Comment("a comment", null);
    }

    public void testGetApprover() throws Exception {
        User approver = new User("The approver");

        final Comment comment = new Comment("A valid comment", approver);

        assertEquals(approver, comment.getApprover());
    }

    public void testGetText() throws Exception {
        final Comment comment = new Comment("A valid comment", new User("My user"));

        assertEquals("A valid comment",comment.getText());
    }
    public void testEquality() {
        final String text = "comment";
        final User user = new User("name");
        final Comment comment = new Comment(text, user);
        final Comment carbonCopy = new Comment(text, user);

        assertEquals(carbonCopy, comment);
        assertEquals(carbonCopy.hashCode(), comment.hashCode());
    }

    public void testInequality() {
        final String text1 = "comment";
        final String text2 = "different comment";
        final User user1 = new User("user");
        final User user2 = new User("different user");

        final Comment comment = new Comment(text1, user1);
        final Comment almostCopy1 = new Comment(text1, user2);
        final Comment almostCopy2 = new Comment(text2, user1);
        final Comment almostCopy3 = new Comment(text2, user2);

        assertTrue(!almostCopy1.equals(comment));
        assertTrue(!(almostCopy1.hashCode() == comment.hashCode()));

        assertTrue(!almostCopy2.equals(comment));
        assertTrue(!(almostCopy2.hashCode() == comment.hashCode()));

        assertTrue(!almostCopy3.equals(comment));
        assertTrue(!(almostCopy3.hashCode() == comment.hashCode()));


    }

}
