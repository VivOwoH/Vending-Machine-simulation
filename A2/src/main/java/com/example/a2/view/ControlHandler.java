package com.example.a2.view;

import java.util.HashMap;

import com.example.a2.Sys;
import com.example.a2.VendingMachine;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

public class ControlHandler {
    private VendingMachine vendingMachine;

    public ControlHandler(Sys sys) {
        vendingMachine = sys.getVendingMachine();
    }
    
    public void productBtnHnadle(HashMap<String, Button> buttons) {
        for (Button b : buttons.values()){
            b.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    //trigger idle
                    vendingMachine.triggerTimer();

                    //add product to cart
                    
                }
            });
        }
    }
}
