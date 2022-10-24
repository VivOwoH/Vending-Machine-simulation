package com.example.a2.view;

import java.util.HashMap;
import java.util.Map.Entry;

import com.example.a2.HelloApplication;
import com.example.a2.Sys;
import com.example.a2.VendingMachine;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class ControlHandler {
    private VendingMachine vendingMachine;

    public ControlHandler(Sys sys) {
        vendingMachine = sys.getVendingMachine();
    }
    
    public void productBtnHandle(HashMap<Integer, Button> buttons, TextField itemCode) {
        for (Button b : buttons.values()){
            b.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    //trigger idle
                    vendingMachine.triggerTimer();

                    //show product id 
                    for (Entry<Integer, Button> entry : buttons.entrySet()) {
                        if (b.equals(entry.getValue())) {
                            itemCode.setText(entry.getKey().toString());
                        }
                    }
                    
                }
            });
        }
    }

    public void adminWindowHandler(HelloApplication app, Button b) {
        b.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                AdminWindow adminWindow = app.getAdminWinodw();
                app.setScene(adminWindow.getScene());
            }
        });
    }

    public void updateProductHandler(AdminWindow homeWindow, Button submitButton, TextField pid, TextField val, ComboBox field) {
        submitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int productId = Integer.parseInt(pid.getText());
                String newValue = val.getText();

                vendingMachine.updateProduct(productId, newValue, field.getValue());

                //TODO update on homeWindow
                
            }
        });
    }
}
