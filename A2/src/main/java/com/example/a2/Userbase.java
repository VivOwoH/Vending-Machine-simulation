package com.example.a2;
import java.util.ArrayList;

public class Userbase {
    private ArrayList<User> users = new ArrayList<User>();
    private User owner;
    private DBManage database;

    public Userbase(DBManage database, String username, String password, int userID){
        this.database = database;
        owner = new User(username, password, userID);
        owner.setRole(new Owner());
        updateUserList();
    }

    public User getUserByID(int userID) {
        for (User user : users) {
            if (user.getID() == userID) {
                return user;
            }
        }
        System.out.println("Cannot find user.");
        return null;
    }

    public User getUserByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        System.out.println("Cannot find user.");
        return null;
    }

    public void updateUserList() {
        this.users.clear();
        this.users = database.getUsers();
    }

    public ArrayList<User> getUserList() {
        return users;
    }
}