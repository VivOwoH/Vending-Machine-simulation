package com.example.a2.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.example.a2.*;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;

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
            vendingMachine.cancelTimer();
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
                User currentUser = system.getCurrentUser();
                AdminWindow adminWindow = system.getAdminWinodw();
                system.setScene(adminWindow.getScene());

                vendingMachine.triggerTimer();
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
        vendingMachine.cancelTimer();
        vendingMachine.clearCart();
    }

    public void checkoutHandle(Button checkoutButton) {
        checkoutButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                vendingMachine.triggerTimer();

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
                vendingMachine.triggerTimer();

                double input = Double.parseDouble(in.getText());

                if (!vendingMachine.checkInput(input)) { 
                    show.setText("Invalid input."); 
                    return;
                }

                double inputTotal = system.getPaymentWindow().addInputCash(input);

                double total = vendingMachine.getTotalCost();

                if (inputTotal < total) {
                    show.setText(String.format("Input: %.2f\nRemaining due: %.2f",inputTotal,total-inputTotal));
                    return;
                }

                ArrayList<HashMap<Double, Integer>> out = vendingMachine.makeCashPurchase(total, inputTotal);

                if(out.get(0).get(0.05) != 0){
                    show.setText(String.format("Could not cover %.2f of change.", out.get(0).get(0.05) * 0.05));
                }
                else {
                    show.setText(String.format("Input: %.2f\nChange: %.2f", inputTotal, inputTotal - total));
                }
            }
        });
    }

    public void methodBoxHandle(ComboBox c) {
        c.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                vendingMachine.triggerTimer();
                system.getPaymentWindow().draw();
            }
        });
    }

    public void drawReport(ComboBox reportType, VBox box) {
        reportType.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //TODO add texts to box
            }
        });
    }
}
