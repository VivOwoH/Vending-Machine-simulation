package com.example.a2;

import com.example.a2.view.AdminWindow;
import com.example.a2.view.HomeWindow;
import com.example.a2.view.PaymentWindow;

import javafx.scene.Scene;

public class Sys{

    private VendingMachine vendingMachine;
    private DBManage database;
    private HelloApplication app;
    private User currentUser;

    public Sys(HelloApplication app) {
        this.app = app;
        this.database = new DBManage("database.sqlite");
        database.createDB();
        database.loadCreditConfig();
        this.vendingMachine = new VendingMachine(database);

    }

    /*
        Method trys to add a new user to the database. Returns true if successful, false otherwise.
     */
    public boolean addUser(User user){
        return false;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public User getCurrentUser(){
        return this.currentUser;
    }

    public VendingMachine getVendingMachine() {
        return this.vendingMachine;
    }

    public DBManage getDatabase(){
        return database;
    }

    public String getUsers() {
        return database.getUsers();
    }

    public void setScene(Scene scene) {
        app.setScene(scene);
    }

    public PaymentWindow getPaymentWindow() { return app.getPaymentWindow(); }

    public AdminWindow getAdminWinodw() {return app.getAdminWindow();}

    public HomeWindow getHomeWindow() { return app.getHomeWindow(); }

}