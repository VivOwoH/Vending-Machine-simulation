package com.example.a2;

import com.example.a2.view.LoginWindow;
import com.example.a2.view.HomeWindow;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    private LoginWindow loginWindow;
    private HomeWindow homeWindow;
    private Stage stage;

    @Override
    public void start(Stage stage) throws IOException {
        System system = new System(); //use this for logic

        this.stage = stage;
        loginWindow = new LoginWindow(this);
        homeWindow = new HomeWindow(system);

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

    public static void main(String[] args) {
        // DBManage.createDB();
        // DBManage.addUser("admin", "admin", "Owner");
        // DBManage.addProduct(10.4, "cola", "Drinks");
        // DBManage.addTransaction(1,1, true);

        // java.lang.System.out.println(system.getVendingMachine().getProductInventroy());

        launch();
    }
}