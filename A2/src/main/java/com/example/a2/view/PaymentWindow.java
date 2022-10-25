package com.example.a2.view;

import java.util.Map;

import com.example.a2.HelloApplication;
import com.example.a2.Sys;
import com.example.a2.VendingMachine;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

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

    private Method method;
    private Text totalText;
    private TextField inputMoney;

    public PaymentWindow(HelloApplication app, Sys system, ControlHandler controlHandler) {
        this.controlHandler = controlHandler;

        this.app = app;
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

        cfgConfirmTransactionButton();
        pane.getChildren().add(confirmTransactionButton);

        if (method == Method.CASH) {
            inputMoney = new TextField();
            // inputMoney.translateX
            pane.getChildren().add(inputMoney);

        } else {

        }

    }

    /**
     * Checkout by adding whatever in the cart into transaction
     * Each unique item is 1 transaction
     */
    public void cfgConfirmTransactionButton() {
        confirmTransactionButton = new Button("Confirm Transaction");
        confirmTransactionButton.setTranslateX(415);
        confirmTransactionButton.setTranslateY(400);
        confirmTransactionButton.setStyle(
                "-fx-background-color: #e6cc00;");

        confirmTransactionButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String currentUserName = system.getCurrentUser().getUsername();
                System.out.println("user: " + currentUserName + " Pressed with ID: " +
                        system.getDatabase().getUserID(currentUserName));

                for (Map.Entry<Integer, Integer> entry : system.getVendingMachine().getCart().entrySet()) {
                    int prodID = entry.getKey();
                    int qty = entry.getValue();
                    boolean success = controlHandler.confirmTransactionButtonHandle(
                            system.getDatabase().getUserID(currentUserName),
                            prodID, qty, system.getDatabase());

                    if (success) {
                        System.out.println("Transaction added");
                        // stock already updated when user add to cart, just need to commit to database
                        int stock = system.getVendingMachine().findProductByID(prodID).getQty();
                        system.getVendingMachine().updateProduct(prodID, Integer.toString(stock), "Quantity");
                        // auto logout user
                        system.setCurrentUser(null);
                        app.setScene(app.getloginWindow().scene);
                    }
                }
                system.getVendingMachine().updateProductInventory(); // refresh inventory
            }
        });
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

    }

}
