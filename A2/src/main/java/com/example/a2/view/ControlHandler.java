package com.example.a2.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.example.a2.*;
import com.example.a2.products.Product;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class ControlHandler {
    private VendingMachine vendingMachine;
    private boolean purchaseCashFlag = false;
    private boolean purchaseCardFlag = false;
    private Sys system;

    private double cashGiven;
    private double change;

    public ControlHandler(Sys sys) {
        system = sys;
        vendingMachine = sys.getVendingMachine();
    }

    public void productBtnHandle(HomeWindow home, HashMap<Integer, Button> buttons, TextField itemCode) {
        for (Button b : buttons.values()) {
            b.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    // trigger idle
                    vendingMachine.triggerTimer();

                    // show product id
                    for (Entry<Integer, Button> entry : buttons.entrySet()) {
                        if (b.equals(entry.getValue())) {
                            itemCode.setText(entry.getKey().toString());
                        }
                    }

                    // clear cancel transaction text
                    home.clearCancelText();
                    home.clearCannotCheckoutText();
                }
            });
        }
    }

    public boolean confirmTransactionButtonHandle(int userID, int prodID, int quantity, DBManage database) {
        try {
            // adds transaction into database
            if (purchaseCashFlag) {
                database.addTransaction(prodID, true, userID, quantity, cashGiven, change);
                // success
                vendingMachine.cancelTimer();

                purchaseCashFlag = false;
                return true;
            }
            if (purchaseCardFlag) {
                database.addTransaction(prodID, true, userID, quantity, -1, -1);
                // success
                vendingMachine.cancelTimer();

                return true;
            }

            return false;
        } catch (Exception e) {
            System.out.println(e);
            System.out.println(e.getMessage());
            return false;
        }
    }

    public void adminWindowHandler(Button b, String role) {
        b.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Window window = null;
                switch (role) {
                    case "Owner":
                        window = system.getAdminWinodw();
                        System.out.println("Display owner window");
                        break;
                    case "Cashier":
                        window = system.getCashierWindow();
                        System.out.println("Display cashier window");
                        break;
                    case "Seller":
                        window = system.getSellerWindow();
                        System.out.println("Display seller window");
                        break;
                    default:
                        System.out.println("Something went wrong when switching admin window.");
                }
                system.setScene(window.getScene());
                vendingMachine.triggerTimer();
            }
        });
    }

    public void updateProductHandler(Window admin, Button submitButton, TextField pid, TextField val,
            ComboBox field) {
        submitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int productId = Integer.parseInt(pid.getText());
                String newValue = val.getText();

                vendingMachine.updateProduct(productId, newValue, field.getValue().toString());

                // system.getHomeWindow().cfgProductPane();
                HashMap<Integer, VBox> productBoxes = system.getHomeWindow().getProductBoxes();
                VBox box = productBoxes.get(productId);
                for (Node n : box.getChildren()) {
                    if (n instanceof Button) {
                        if (field.getValue() == "Category") {

                            ImageView view = new ImageView();
                            System.out.println(newValue);

                            if (newValue.equals("Drinks")) {
                                view.setImage(new Image(getClass().getResource("/drink.png").toString()));
                            } else if (newValue.equals("Chocolates")) {
                                view.setImage(new Image(getClass().getResource("/chocolate.png").toString()));
                            } else if (newValue.equals("Chips")) {
                                view.setImage(new Image(getClass().getResource("/chips.png").toString()));
                            } else if (newValue.equals("Candies")) {
                                view.setImage(new Image(getClass().getResource("/candy.png").toString()));
                            }

                            view.setFitHeight(50);
                            view.setFitWidth(50);
                            ((Button) n).setGraphic(view);
                        }

                    } else if (n instanceof Text) {
                        Product product = system.getVendingMachine().findProductByID(productId);
                        ((Text) n).setText(String.format("%s \n%.2f",
                                product.getName(), product.getCost()));
                    }
                }
            }
        });
    }
    /**
     * Function changes the amount of cash associated with some specific denomination
     * @param adminWindow - window input so that output text can be displayed
     * @param submitCashChange - button for submission
     * @param denomination - denomination input (should be in VendingMachine.denominations)
     * @param cashQty - quantity input (should be integer)
     */
    public void updateCashHandler(Window adminWindow, Button submitCashChange, TextField denomination, TextField cashQty, Text cashMsg) {
        submitCashChange.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){
                try{
                    int quantity = Integer.parseInt(cashQty.getText());
                    String denom = denomination.getText();

                    boolean in = false;
                    for(String test : VendingMachine.denominations){
                        if(denom.equalsIgnoreCase(test)){
                            in = true;
                        }
                    }
                    if(!in || quantity < 1 || quantity > 999){
                        throw new Exception();
                    }

                    system.getDatabase().updateCurrency(Double.parseDouble(denom), quantity);
                    cashMsg.setText(String.format("Denomination %s's quantity updated to %s", denom, quantity));
                }
                catch(Exception e){
                    cashMsg.setText("Incorrect input");
                }
            }
        });
    }


    public void updateRoleHandler(TextField userID, Button submitButton, ComboBox box, Text roleMsg) {
        submitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String role = box.getValue().toString();
                User currentUser = system.getCurrentUser();
                String username = userID.getText();
                int id = system.getUserbase().getUserByUsername(username).getID();

                if (currentUser.getRole().getClass() == Owner.class) {
                    Owner owner = (Owner) currentUser.getRole();
                    String msg = owner.modifyRole(system, id, role);
                    roleMsg.setText(msg);
                }
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

    // handle cash input
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
                    show.setText(String.format("Input: %.2f\nRemaining due: %.2f", inputTotal, total - inputTotal));
                    return;
                }

                ArrayList<HashMap<Double, Integer>> out = vendingMachine.makeCashPurchase(total, inputTotal);

                if (out.get(0).get(0.05) != 0) {
                    system.getPaymentWindow().addInputCash(-1 * input);
                    show.setText(String.format("Could not cover %.2f of change.", out.get(0).get(0.05) * 0.05));
                } else {
                    purchaseCashFlag = true;
                    show.setText(String.format("Input: %.2f\nChange: %.2f", inputTotal, inputTotal - total));
                    cashGiven = inputTotal;
                    change = inputTotal-total;
                }
            }
        });
    }

    // handle credit card input
    public void creditCardHandle(TextField cardName, TextField cardNum, Text show) {
        String cardHolder = cardName.getText();
        String cardNumberTemp = cardNum.getText();

        try {
            int cardNumber = Integer.parseInt(cardNumberTemp);

            // check if exist in system
            if (system.getDatabase().creditCardIsValid(cardHolder, cardNumber)) {
                purchaseCardFlag = true;
            } else {
                purchaseCardFlag = false;
                show.setVisible(true);
                show.setText("Invalid Details");
            }
        } catch (Exception e) {
            show.setVisible(true);
            show.setText("Invalid Details");
            purchaseCardFlag = false;
        }

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

                String type = reportType.getValue().toString();

                if(type.equals("Available change")) {
                    box.getChildren().clear();
                    Text header = new Text("Denomination | Quantity");
                    Text report = new Text(system.getCurrencyReport());
                    box.getChildren().addAll(header, report);
                }
                else if (type.equals("Accounts")) {
                    box.getChildren().clear();
                    Text header = new Text("Username | Role");
                    Text report = new Text(system.getUsersReport());
                    box.getChildren().addAll(header, report);
                } else if (type.equals("Transactions")) {
                    box.getChildren().clear();
                    Text header = new Text("DateTime | ProductID | Paid | Change | Method");
                    Text report = new Text(system.getTransactionHistory());
                    box.getChildren().addAll(header, report);
                } else if (type.equals("Cancelled transactions")) {
                    box.getChildren().clear();
                    Text header = new Text("DateTime | User | Reason");
                    Text report = new Text(system.getCancelledTransactions());
                    box.getChildren().addAll(header, report);
                }
            }
        });
    }
}
