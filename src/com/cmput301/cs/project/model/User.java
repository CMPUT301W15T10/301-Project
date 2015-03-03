package com.cmput301.cs.project.model;

public class User {
	
	private String user_name = "";
	private String user_id = "";
	
	public String getUserName() {
		return user_name;
	}

	public void setUserName(String login_name) {
		this.user_name = login_name;
	}
	
	public String getUserId() {
		return user_id;
	}

	public void setUserId(String login_id) {
		this.user_id = login_id;
	}
	
	public User() {
		setUserName(user_name);
		setUserId(user_id);
	}
}
