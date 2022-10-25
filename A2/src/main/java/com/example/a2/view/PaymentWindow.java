package com.example.a2.view;

import com.example.a2.Sys;
import com.example.a2.VendingMachine;

import javafx.scene.Scene;
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

    public PaymentWindow(Sys system, ControlHandler controlHandler) {
        this.controlHandler = controlHandler;

        this.pane = new Pane();
        this.scene = new Scene(pane, width, height);
        this.system = system;
        this.vendingMachine = system.getVendingMachine();
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
        
        if (method == Method.CASH) {
            inputMoney = new TextField();
            // inputMoney.translateX
            pane.getChildren().add(inputMoney);

        } else {

        }
        
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        
    }
    
}
