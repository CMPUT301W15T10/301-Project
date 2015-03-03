package com.cmput301.cs.project.model;

/**
 * Created by Blaine on 03/03/2015.
 */
public class Comment {
    private final String text;
    private final User approver;



    public Comment(String text, User approver) {
        this.text = text;
        this.approver = approver;
    }


    public User getApprover() {
        return approver;
    }

    public String getText() {
        return text;
    }
}