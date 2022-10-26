package com.example.a2.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.example.a2.DBManage;
import com.example.a2.HelloApplication;
import com.example.a2.Sys;
import com.example.a2.VendingMachine;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.Scene;

public class ControlHandler {
    private VendingMachine vendingMachine;
    private Sys system;

    public ControlHandler(Sys sys) {
        system = sys;
        vendingMachine = sys.getVendingMachine();
    }

    public void productBtnHandle(HomeWindow home, HashMap<Integer, Button> buttons, TextField itemCode) {
        for (Button b : buttons.values()) {
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

                    //clear cancel transaction text
                    home.clearCancelText();
                    home.clearCannotCheckoutText();
                }
            });
        }
    }

    public boolean confirmTransactionButtonHandle(int userID, int prodID, int quantity, DBManage database) {
        try {
            // adds transaction into database
            database.addTransaction(prodID, true, userID, quantity);
            // success
            return true;
        } catch (Exception e) {
            // fail
            return false;
        }
    }

    public void adminWindowHandler(Button b) {
        b.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                AdminWindow adminWindow = system.getAdminWinodw();
                system.setScene(adminWindow.getScene());
            }
        });
    }

    public void updateProductHandler(AdminWindow admin, Button submitButton, TextField pid, TextField val, ComboBox field) {
        submitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int productId = Integer.parseInt(pid.getText());
                String newValue = val.getText();

                vendingMachine.updateProduct(productId, newValue, field.getValue().toString());

                system.getHomeWindow().cfgProductPane();
            }
        });
    }

    public void cancelTransactionHandle() {
        vendingMachine.clearCart();
    }

    public void checkoutHandle(Button checkoutButton) {
        checkoutButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (vendingMachine.getCart().size() == 0) {
                    system.getHomeWindow().dontLetCheckout();
                    return;
                }

                PaymentWindow paymentWindow = system.getPaymentWindow();
                system.setScene(paymentWindow.getScene());
                paymentWindow.draw();
            }
        });
    }

    public void toHomeWindowHandle(Button b) {
        b.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                HomeWindow home = system.getHomeWindow();
                system.setScene(home.getScene());
            }
        });
    }

    public void cashHandle(TextField in, Text show) {
        in.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                double input = Double.parseDouble(in.getText());

                if (!vendingMachine.checkInput(input)) { 
                    show.setText("Invalid input."); 
                    return;
                }

                double inputTotal = system.getPaymentWindow().addInputCash(input);

                double total = vendingMachine.getTotalCost();
                ArrayList<HashMap<Double, Integer>> out = vendingMachine.makeCashPurchase(total, inputTotal);

                if (out == null) {
                    show.setText(String.format("Input: %.2f\nRemaining due: %.2f",inputTotal,total-inputTotal));
                    return;
                }

                show.setText(String.format("Input: %.2f\nChange: %.2f", inputTotal, inputTotal - total));
            }
        });
    }

    public void methodBoxHandle(ComboBox c) {
        c.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                system.getPaymentWindow().draw();
            }
        });
    }
}
