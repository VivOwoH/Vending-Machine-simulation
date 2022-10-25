package com.example.a2;

import com.example.a2.view.LoginWindow;
import com.example.a2.view.PaymentWindow;
import com.example.a2.view.AdminWindow;
import com.example.a2.view.ControlHandler;
import com.example.a2.view.HomeWindow;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.beans.EventHandler;
import java.io.IOException;
import java.util.ArrayList;

public class HelloApplication extends Application {
    private LoginWindow loginWindow;
    private HomeWindow homeWindow;
    private AdminWindow adminWindow;
    private PaymentWindow paymentWindow;
    private ControlHandler controlHandler;
    private Stage stage;

    @Override
    public void start(Stage stage) throws IOException {
        Sys system = new Sys(this); //use this for logic

        this.stage = stage;
        controlHandler = new ControlHandler(system);
        loginWindow = new LoginWindow(this, system);
        adminWindow = new AdminWindow(system, controlHandler);
        homeWindow = new HomeWindow(this, system, controlHandler);
        paymentWindow = new PaymentWindow(system, controlHandler);

        stage.setTitle("Lite Snacks");
        stage.setScene(loginWindow.getScene());
        stage.setResizable(false);
        stage.show();

        stage.setOnCloseRequest(e -> System.exit(0));
    }

    public void setScene(Scene scene) {
        stage.setScene(scene);
        stage.show();
    }

    public LoginWindow getloginWindow() { return loginWindow; }
    public AdminWindow getAdminWindow() { return adminWindow; }
    public HomeWindow getHomeWindow() { return homeWindow; }
    public PaymentWindow getPaymentWindow() { return paymentWindow; }


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