package com.example.a2;

public class User {
    private Role role = null; //role is set as owner by the system or set as something else by owner -> modify call.
    private String username;
    private String password;
    private int userID;

    public User(String username, String password, int userID){
        this.username = username;
        this.password = password;
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public User setRole(Role role){
        this.role = role;
        return this;
    }

    public Role getRole() {
        return role;
    }

    public int getID() {
        return userID;
    }
}