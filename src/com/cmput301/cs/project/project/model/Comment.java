package com.cmput301.cs.project.project.model;

/**
 * Created by Blaine on 03/03/2015.
 */
public class Comment {
    private final String text;
    private final User approver;



    public Comment(String text, User approver) {
        if(text == null || text.isEmpty()){
            throw new IllegalArgumentException();
        }

        if(approver == null) {
            throw new IllegalArgumentException();
        }

        this.text = text;
        this.approver = approver;
    }


    public User getApprover() {
        return approver;
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof Comment)) return false;

        final Comment comment = (Comment) o;

        return comment.text.equals(text) && comment.approver.equals(approver);
    }

    @Override
    public int hashCode() {
        int result = approver.hashCode();
        result = 31 * result + text.hashCode();

        return result;
    }

}