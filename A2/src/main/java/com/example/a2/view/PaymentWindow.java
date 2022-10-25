package com.example.a2.view;

import com.example.a2.Sys;
import com.example.a2.VendingMachine;

import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

enum Method {
    CASH,
    CARD
}

public class PaymentWindow implements Window{

    private Scene scene;
    private Pane pane;
    private int width = 500;
    private int height = 700;

    private ControlHandler controlHandler;
    private Sys system;
    private VendingMachine vendingMachine;

    private Method method;
    private Text totalText;
    private TextField inputMoney;
    private Button continueShopping;
    private ComboBox methodBox;
    private String methods[] = {"Cash", "Card"};

    public PaymentWindow(Sys system, ControlHandler controlHandler) {
        this.controlHandler = controlHandler;

        this.pane = new Pane();
        this.scene = new Scene(pane, width, height);
        this.system = system;
        this.vendingMachine = system.getVendingMachine();

        //back to shopping
        continueShopping = new Button("Continue shopping");
        continueShopping.setTranslateX(370);
        continueShopping.setTranslateY(20);
        continueShopping.setStyle(
                "-fx-background-color: #e6cc00;");
        pane.getChildren().add(continueShopping);
        controlHandler.toHomeWindowHandle(continueShopping);

        // mode pick
        methodBox = new ComboBox(FXCollections.observableArrayList(methods));
        methodBox.setTranslateX(10);
        methodBox.setTranslateY(70);
        methodBox.setPromptText("Select payment method");
        pane.getChildren().add(methodBox);
        controlHandler.methodBoxHandle(methodBox);
    }

    @Override
    public Scene getScene() {
        return scene;
    }

    @Override
    public void draw() {
        totalText = new Text("Total: " + Double.toString(vendingMachine.getTotalCost()));
        totalText.setFont(new Font(30));
        totalText.setTranslateX(10);
        totalText.setTranslateY(50);
        pane.getChildren().add(totalText);

        if (methodBox.getValue() == "Cash") {
            inputMoney = new TextField();
            inputMoney.setTranslateX(10);
            inputMoney.setTranslateY(100);
            inputMoney.setPromptText("insert cash/coins");
            pane.getChildren().add(inputMoney);

        } else {
            inputMoney.setVisible(false);
        }
        
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        
    }
    
}
