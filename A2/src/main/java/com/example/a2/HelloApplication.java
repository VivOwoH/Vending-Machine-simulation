package com.example.a2;

import com.example.a2.view.LoginWindow;
import com.example.a2.view.PaymentWindow;
import com.example.a2.view.SellerWindow;
import com.example.a2.view.AdminWindow;
import com.example.a2.view.CashierWindow;
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
    private SellerWindow sellerWindow;
    private CashierWindow cashierWindow;
    private PaymentWindow paymentWindow;
    private ControlHandler controlHandler;
    private Stage stage;
    private Sys system;

    @Override
    public void start(Stage stage) throws IOException {
        system = new Sys(this); //use this for logic

        this.stage = stage;
        controlHandler = new ControlHandler(system);
        loginWindow = new LoginWindow(this, system);
        // adminWindow = new AdminWindow(system, controlHandler);
        sellerWindow = new SellerWindow(system, controlHandler);
        cashierWindow = new CashierWindow(system, controlHandler);
        homeWindow = new HomeWindow(this, system, controlHandler);
        paymentWindow = new PaymentWindow(this, system, controlHandler);

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


    public void makeAdminWindow() {
        adminWindow = new AdminWindow(system, controlHandler);
    }

    public void reset() {
        Sys system = new Sys(this);

        controlHandler = new ControlHandler(system);
        loginWindow = new LoginWindow(this, system);
        adminWindow = new AdminWindow(system, controlHandler);
        sellerWindow = new SellerWindow(system, controlHandler);
        cashierWindow = new CashierWindow(system, controlHandler);
        homeWindow = new HomeWindow(this, system, controlHandler);
        paymentWindow = new PaymentWindow(this, system, controlHandler);
    }

    public LoginWindow getloginWindow() { 
        reset();
        return loginWindow; 
    }
  
    public AdminWindow getAdminWindow() { return adminWindow; }
    public SellerWindow getSellerWindow() { return sellerWindow; }
    public CashierWindow getCashierWindow() { return cashierWindow; }
    public HomeWindow getHomeWindow() { 
        homeWindow.reconfiguration();
        return homeWindow; 
    }
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