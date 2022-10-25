package com.example.a2;

import com.example.a2.view.LoginWindow;
import com.example.a2.view.AdminWindow;
import com.example.a2.view.HomeWindow;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class HelloApplication extends Application {
    private LoginWindow loginWindow;
    private HomeWindow homeWindow;
    private AdminWindow adminWindow;
    private Stage stage;

    @Override
    public void start(Stage stage) throws IOException {
        Sys system = new Sys(); //use this for logic

        this.stage = stage;
        loginWindow = new LoginWindow(this, system);
        adminWindow = new AdminWindow(system);
        homeWindow = new HomeWindow(this, system);

        stage.setTitle("Lite Snacks");
        stage.setScene(loginWindow.getScene());
        stage.show();
    }

    public void setScene(Scene scene) {
        stage.setScene(scene);
        stage.show();
    }

    public HomeWindow getHomeWindow() {
        return this.homeWindow;
    }

    public LoginWindow getLoginWindow() {
        return this.loginWindow;
    }

    public AdminWindow getAdminWinodw() {
        return this.adminWindow;
    }

    public static void main(String[] args) {
//         DBManage.addUser("admin", "admin", "Owner");
//         DBManage.addProduct(10.4, "cola", "Drinks");
//         DBManage.addTransaction(1,true,2);
//
//         ArrayList<Transaction> transactions = DBManage.getLastFiveTransactionsByUserID(2);
//         for (Transaction transaction: transactions){
//             System.out.println("Date: " + transaction.getDate());
//             System.out.println("TransID: " + transaction.getTransID());
//         }

        // java.lang.System.out.println(system.getVendingMachine().getProductInventroy());

        launch();
    }
}