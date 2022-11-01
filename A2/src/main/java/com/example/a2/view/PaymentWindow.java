package com.example.a2.view;

import java.util.ArrayList;
import java.util.Map;

import com.example.a2.HelloApplication;
import com.example.a2.Sys;
import com.example.a2.VendingMachine;
import com.example.a2.products.Product;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import javax.swing.event.ChangeListener;

enum Method {
    CASH,
    CARD
}

public class PaymentWindow implements Window {

    private Scene scene;
    private Pane pane;
    private int width = 500;
    private int height = 700;

    private HelloApplication app;
    private ControlHandler controlHandler;
    private Sys system;
    private VendingMachine vendingMachine;
    private Button confirmTransactionButton;
    private VBox box;

    private Method method;
    private Text totalText;
    private TextField inputMoney;
    private Button continueShopping;
    private Text cashMsg;
    private Text cardMsg;
    private ComboBox methodBox;
    private String methods[] = { "Cash", "Card" };
    private TextField cardHolder;
    private PasswordField cardNumber;
    private ScrollPane scrollPane;
    private Button cancelButton;
    private Button saveInfoButton;
    private Button quitButton;
    private Text saveMsg;

    private double inputTotal = 0;

    public PaymentWindow(HelloApplication app, Sys system, ControlHandler controlHandler) {
        this.controlHandler = controlHandler;

        this.app = app;
        this.pane = new Pane();
        this.scene = new Scene(pane, width, height);
        this.system = system;
        this.vendingMachine = system.getVendingMachine();

        // confirm transaction button
        cfgConfirmTransactionButton();
        pane.getChildren().add(confirmTransactionButton);

        // back to shopping
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

        // cancel Transaction
        cfgCancelButton();
        pane.getChildren().add(cancelButton);
    }

    @Override
    public Scene getScene() {
        return scene;
    }

    @Override
    public void draw() {
        pane.getChildren().remove(totalText); // remove the previous print first
        totalText = new Text("Total: " + String.format("%.2f", vendingMachine.getTotalCost()));
        totalText.setFont(new Font(30));
        totalText.setTranslateX(10);
        totalText.setTranslateY(50);
        pane.getChildren().add(totalText);

        refreshCart();

        if (methodBox.getValue() == "Cash") {
            if (cardHolder != null) {
                cardHolder.setVisible(false);
            }
            if (cardNumber != null) {
                cardNumber.setVisible(false);
            }
            if (cardMsg != null) {
                cardMsg.setVisible(false);
            }
            if (saveMsg != null) {
                saveMsg.setVisible(false);
            }
            if (saveInfoButton != null) {
                saveInfoButton.setVisible(false);
            }

            inputMoney = new TextField();
            inputMoney.setTranslateX(10);
            inputMoney.setTranslateY(210);
            inputMoney.setPromptText("insert cash/coins");
            pane.getChildren().add(inputMoney);

            cashMsg = new Text();
            cashMsg.setTranslateX(15);
            cashMsg.setTranslateY(250);
            pane.getChildren().add(cashMsg);

            controlHandler.cashHandle(inputMoney, cashMsg);

        } else if (methodBox.getValue() == "Card") {
            if (inputMoney != null) {
                inputMoney.setVisible(false);
            }
            if (cashMsg != null) {
                cashMsg.setVisible(false);
            }

            cardHolder = new TextField();
            cardHolder.setTranslateX(10);
            cardHolder.setTranslateY(210);
            cardHolder.setPromptText("Cardholder name");
            pane.getChildren().add(cardHolder);

            cardNumber = new PasswordField();
            cardNumber.setTranslateX(10);
            cardNumber.setTranslateY(240);
            cardNumber.setPromptText("Card number");
            pane.getChildren().add(cardNumber);

            cardMsg = new Text();
            cardMsg.setTranslateX(15);
            cardMsg.setTranslateY(280);
            cardMsg.setVisible(false); // test
            pane.getChildren().add(cardMsg);

            saveInfoButton = new Button();
            saveInfoButton.setTranslateX(10);
            saveInfoButton.setTranslateY(320);
            saveInfoButton.setText("Save CC info & Quit");
            pane.getChildren().add(saveInfoButton);
            saveInfoButton.setVisible(false);

            quitButton = new Button();
            quitButton.setTranslateX(150);
            quitButton.setTranslateY(320);
            quitButton.setText("Quit without saving");
            pane.getChildren().add(quitButton);
            quitButton.setVisible(false);

            saveMsg = new Text();
            saveMsg.setTranslateX(10);
            saveMsg.setTranslateY(370);
            pane.getChildren().add(saveMsg);

            // check if current user is anonymous user, if so don't give same cc info button
            if (system.getCurrentUser().getUsername().equals("Anonymous")) {
                saveMsg.setText("Anonymous user cannot save CC info");
                saveMsg.setVisible(true);
            }

            saveInfoButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        int cardN = Integer.parseInt(cardNumber.getText());
                        system.getDatabase().saveCreditCardInfo(cardHolder.getText(), cardN,
                                system.getCurrentUser().getID());
                        system.setCurrentUser(null);
                        app.setScene(app.getloginWindow().scene);
                    } catch (Exception e) {
                        cardMsg.setText("Card number must only contain numbers");
                        cardMsg.setVisible(true);
                    }
                }
            });

            quitButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    // auto logout user after all products added
                    system.setCurrentUser(null);
                    app.setScene(app.getloginWindow().scene);
                }
            });

            // autofill card info if exists
            ArrayList<String> nameNumber = system.getDatabase().getCCInfo(system.getCurrentUser().getUsername());
            if (nameNumber.get(0) != null && nameNumber.get(1) != null) {
                String ccName = nameNumber.get(0);
                String ccNumber = nameNumber.get(1);

                cardHolder.setText(ccName);
                cardNumber.setText(ccNumber);
            }
        }

    }

    public void refreshCart() {
        scrollPane = new ScrollPane();
        scrollPane.setPrefSize(200, 100);
        scrollPane.relocate(10, 100);

        pane.getChildren().remove(box); // remove the previous print
        box = new VBox(10);

        // List all cart items
        for (Map.Entry<Integer, Integer> entry : system.getVendingMachine().getCart().entrySet()) {
            int prodID = entry.getKey();
            int qty = entry.getValue();
            Product product = system.getVendingMachine().findProductByID(prodID);
            Text text = new Text(String.format("%s %.2f %d %.2f", product.getName(), product.getCost(), qty,
                    product.getCost() * qty));
            box.getChildren().add(text);
        }
        // pane.getChildren().add(box);

        scrollPane.setContent(box);
        pane.getChildren().add(scrollPane);
    }

    public double addInputCash(double amount) {
        this.inputTotal += amount;
        return inputTotal;
    }

    /**
     * Checkout by adding whatever in the cart into transaction
     * Each unique item is 1 transaction
     */
    public void cfgConfirmTransactionButton() {
        confirmTransactionButton = new Button("Confirm Transaction");
        confirmTransactionButton.setTranslateX(215);
        confirmTransactionButton.setTranslateY(400);
        confirmTransactionButton.setStyle(
                "-fx-background-color: #e6cc00;");

        confirmTransactionButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                vendingMachine.cancelTimer();

                String currentUserName = system.getCurrentUser().getUsername();

                // im confused how u guys made this feature so im gonna manually update
                // cardholder and number value from here
                if (methodBox.getValue() == "Card") {
                    // if the card is right it will change the purchaseCardFlag field to be
                    // true, allowing purchases
                    controlHandler.creditCardHandle(cardHolder, cardNumber, cardMsg);
                }

                for (Map.Entry<Integer, Integer> entry : system.getVendingMachine().getCart().entrySet()) {
                    int prodID = entry.getKey();
                    int qty = entry.getValue();
                    boolean success = controlHandler.confirmTransactionButtonHandle(
                            system.getDatabase().getUserID(currentUserName),
                            prodID, qty, system.getDatabase());

                    if (success) {
                        system.getDatabase().updateSold(prodID, qty);
                        System.out.println("Transaction added");
                        // stock already updated when user add to cart, just need to commit to database
                        int stock = system.getVendingMachine().findProductByID(prodID).getQty();
                        system.getVendingMachine().updateProduct(prodID, Integer.toString(stock), "Quantity");

                        system.getVendingMachine().updateProductInventory(); // refresh inventory

                        if (methodBox.getValue() == "Card") {
                            continueShopping.setVisible(false);
                            methodBox.setVisible(false);
                            confirmTransactionButton.setVisible(false);

                            cardMsg.setText("Transaction added.");
                            cardMsg.setVisible(true);

                            quitButton.setVisible(true);
                            saveInfoButton.setVisible(true); // we can only save cc when transaction is successful
                        } else {
                            system.setCurrentUser(null);
                            app.setScene(app.getloginWindow().scene);
                        }

                    } else { // TODO: handle edge case
                        System.out.println("Either not enough money or machine can't cover the change."); // should be
                                                                                                          // javafx
                    }
                }
            }
        });
    }

    public void cfgCancelButton() {
        cancelButton = new Button("Cancel");
        cancelButton.setTranslateX(280);
        cancelButton.setTranslateY(20);
        cancelButton.setMinWidth(70);

        cancelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                controlHandler.cancelTransactionHandle();
                confirmCancelled();
            }
        });
    }

    public void confirmCancelled() {
        // we only put through cancelled transactions if not empty cart
        if (system.getVendingMachine().getCart().size() > 0) {
            system.getDatabase().addCancelledTransaction("user cancelled");
        }   
        system.getVendingMachine().clearCart();
        system.setCurrentUser(null);
        system.setScene(app.getloginWindow().scene);
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

    }

}
