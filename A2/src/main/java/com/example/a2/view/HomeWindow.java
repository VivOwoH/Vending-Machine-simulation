package com.example.a2.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.example.a2.*;
import com.example.a2.products.Chips;
import com.example.a2.products.Candies;
import com.example.a2.products.Chips;
import com.example.a2.products.Chocolates;
import com.example.a2.products.Drinks;
import com.example.a2.products.Product;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class HomeWindow implements Window {
    private Pane pane;
    private Scene scene;
    private int width = 500;
    private int height = 700;

    private Background bg;
    private BackgroundImage bImg;

    private ScrollPane scrollPane;
    private Text text;
    private Text prodID;
    private TextField itemCode;
    private Text prodQty;
    private TextField itemQty;
    private Button checkout;
    private Text recentTxt;
    private Text allTxt;
    private Text historyTxt;
    private ComboBox comboBox;
    private Button cancelButton;
    private Text cancelled;
    private User currentUser;
    private VBox historyBox;
    private Text cannotCheckout;

    private Sys sys;
    private HelloApplication app;

    private ControlHandler controlHandler;
    private HashMap<Integer, Button> productButtons;
    private HashMap<Integer, VBox> productBoxes;

    private Button roleButton;

    public HomeWindow(HelloApplication app, Sys system, ControlHandler controlHandler) {
        this.sys = system;
        this.controlHandler = controlHandler;
        this.app = app;

        controlHandler = new ControlHandler(sys);
        pane = new Pane();
        scene = new Scene(pane, width, height);

        // System.out.println("Working Directory = " + System.getProperty("user.dir"));
        Image img = new Image(getClass().getResource("/background.png").toString());
        bImg = new BackgroundImage(img,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(width, height, true, true, true, true));
        bg = new Background(bImg);
        pane.setBackground(bg);

        cfgCategoryDropbox();
        pane.getChildren().add(comboBox);

        cfgPurchaseBox();
        pane.getChildren().add(prodID);
        pane.getChildren().add(itemCode);
        pane.getChildren().add(prodQty);
        pane.getChildren().add(itemQty);
        pane.getChildren().add(text);

        cfgProductPane(); // need to cfg everything in the scrollpane b4 adding to renderqueue
        pane.getChildren().add(scrollPane);
        controlHandler.productBtnHandle(this, productButtons, itemCode);

        // checkout button
        cfgCheckoutButton();
        pane.getChildren().add(checkout);

        // cancel Transaction
        cfgCancelButton();
        pane.getChildren().add(cancelButton);

        // admin button
        cfgRoleButton();
    }

    // whatever that needs to be updated upon scene change goes here
    public void reconfiguration() {
        this.cfgRoleButton();
    }

    public void cfgRoleButton() {
        // System.out.println(sys.getCurrentUser());
        
        // change to admin (null role = normal user)
        if (sys.getCurrentUser() != null && sys.getCurrentUser().getRole() != null) {
            if (sys.getCurrentUser().getRole().getClass() == Owner.class) {
                roleButton = new Button("Owner");
                controlHandler.adminWindowHandler(roleButton, "Owner");
            }
            else if (sys.getCurrentUser().getRole().getClass() == Seller.class) {
                roleButton = new Button("Seller");
                controlHandler.adminWindowHandler(roleButton, "Seller");
            }
            else if (sys.getCurrentUser().getRole().getClass() == Cashier.class) {
                roleButton = new Button("Cashier");
                controlHandler.adminWindowHandler(roleButton, "Cashier");
            }
            roleButton.setTranslateX(420);
            roleButton.setTranslateY(135);
            pane.getChildren().add(roleButton);
        }
    }

    public void cfgProductPane() {
        scrollPane = new ScrollPane();
        scrollPane.setPrefSize(380, 480);
        scrollPane.relocate(20, 60);

        historyTxt = new Text("Last 5 purchases");
        historyTxt.setFont(new Font(30));

        allTxt = new Text("Products");
        allTxt.setFont(new Font(30));
        VBox box = new VBox();
        box.getChildren().add(historyTxt);

        // this will be loaded with content when the user logs in
        historyBox = new VBox();
        box.getChildren().add(historyBox);

        // product display
        box.getChildren().add(allTxt);

        HBox currHBox = new HBox();
        currHBox.setSpacing(10);
        int hcount = 0;

        productButtons = new HashMap<>();
        productBoxes = new HashMap<>();

        for (Product product : sys.getVendingMachine().getProductInventroy()) {

            VBox productBox = new VBox();
            ImageView view = new ImageView();

            if (product instanceof Drinks) {
                view.setImage(new Image(getClass().getResource("/drink.png").toString()));
            } else if (product instanceof Chocolates) {
                view.setImage(new Image(getClass().getResource("/chocolate.png").toString()));
            } else if (product instanceof Chips) {
                view.setImage(new Image(getClass().getResource("/chips.png").toString()));
            } else if (product instanceof Candies) {
                view.setImage(new Image(getClass().getResource("/candy.png").toString()));
            }

            view.setFitHeight(50);
            view.setFitWidth(50);
            Button button = new Button();
            button.setGraphic(view);
            // button.setStyle("-fx-border-color: transparent;-fx-background-color:
            // transparent;");

            productBox.getChildren().add(button);
            Text productText = new Text(String.format("%s \n%.2f",
                    product.getName(), product.getCost()));
            // productText.setTextAlignment(TextAlignment.CENTER);
            productBox.getChildren().add(productText);
            productButtons.put(product.getCode(), button);
            productBoxes.put(product.getCode(), productBox);

            currHBox.getChildren().add(productBox);

            hcount++;

            if (hcount == 5) {
                box.getChildren().add(currHBox);
                currHBox = new HBox();
                currHBox.setSpacing(10);
                hcount = 0;
            }
        }
        box.getChildren().add(currHBox);

        scrollPane.setContent(box);
    }

    public void cfgCategoryDropbox() {
        // comboBox => select at most 1 from pre-defined options
        comboBox = new ComboBox();
        comboBox.setTranslateX(20);
        comboBox.setTranslateY(20);

        comboBox.getItems().add("All");
        for (String category : sys.getVendingMachine().getCategories()) {
            comboBox.getItems().add(category);
        }

        comboBox.getSelectionModel().selectFirst(); // placeholder = 1st option = default All

        comboBox.setOnAction((event) -> {
            sys.getVendingMachine().triggerTimer();
            String selectedCategory = (String) comboBox.getValue();

            // reset scrollpane content
            VBox box = new VBox();

            if (selectedCategory.equals("All")) {
                for (Product product : sys.getVendingMachine().getProductInventroy()) {
                    box.getChildren().add(new Text(String.format("%d %s %.2f",
                            product.getCode(), product.getName(), product.getCost())));
                }
            } else {
                for (Product product : sys.getVendingMachine().ShowProductCategorized(selectedCategory)) {
                    box.getChildren().add(new Text(String.format("%d %s %.2f",
                            product.getCode(), product.getName(), product.getCost())));
                }
            }

            scrollPane.setContent(box);

        });

    }

    public void cfgPurchaseBox() {
        // notification text
        text = new Text();

        // item code
        prodID = new Text("Enter Product ID:");
        itemCode = new TextField();

        prodID.setTranslateX(20);
        prodID.setTranslateY(555);
        itemCode.setTranslateX(20);
        itemCode.setTranslateY(560);

        // item quantity
        prodQty = new Text("Quantity:");
        itemQty = new TextField();

        prodQty.setTranslateX(200);
        prodQty.setTranslateY(555);
        itemQty.setTranslateX(200);
        itemQty.setTranslateY(560);

        // action event
        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                sys.getVendingMachine().triggerTimer();

                int id = Integer.parseInt(itemCode.getText());
                int qty = Integer.parseInt(itemQty.getText());

                text.setTranslateX(360);
                text.setTranslateY(575);

                String msg = sys.getVendingMachine().addToCart(id, qty);
                text.setText(msg);
            }
        };

        itemCode.setOnAction(event);
        itemQty.setOnAction(event);
    }

    public void cfgCancelButton() {
        cancelButton = new Button("Cancel");
        cancelButton.setTranslateX(415);
        cancelButton.setTranslateY(430);
        cancelButton.setMinWidth(70);

        cancelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                controlHandler.cancelTransactionHandle();
                confirmCancelled();
            }
        });
    }

    /**
     * Checkout by adding whatever in the cart into transaction
     * Each unique item is 1 transaction
     */
    public void cfgCheckoutButton() {
        checkout = new Button("Checkout");
        checkout.setTranslateX(415);
        checkout.setTranslateY(400);
        checkout.setStyle(
                "-fx-background-color: #e6cc00;");

        controlHandler.checkoutHandle(checkout);
    }

    // run this to set current user in home application after it has been loaded
    // alongside all component that requires user
    public void loadUserAfterLogin(User user) {
        currentUser = user;
        loadHistory();
    }

    public void loadHistory() {
        // load the history box

        historyBox.getChildren().clear();

        ArrayList<Transaction> lastFiveTransactions = sys.getDatabase().getLastFiveTransactionsByUserID(
                sys.getDatabase().getUserID(currentUser.getUsername()));

        for (Transaction transaction : lastFiveTransactions) {
            int prodID = transaction.getProdID();
            int quantity = transaction.getQuantity();
            Date date = transaction.getDate();
            String text = String.format("you bought %s amount of %s at %s",
                    prodID, quantity, date);
            Text historyEntry = new Text(text);
            historyEntry.setFont(new Font(10));
            historyBox.getChildren().add(historyEntry);
        }
    }

    public HashMap<Integer, VBox> getProductBoxes() {
        return productBoxes;
    }

    public void confirmCancelled() {
        // if (cannotCheckout != null) clearCannotCheckoutText();
        //
        // if (cancelled == null) {
        // cancelled = new Text("Cart cleared.");
        // cancelled.setTranslateX(415);
        // cancelled.setTranslateY(470);
        // pane.getChildren().add(cancelled);
        // return;
        // }
        //
        // cancelled.setVisible(true);
        sys.setCurrentUser(null);
        sys.setScene(app.getloginWindow().scene);
    }

    public void clearCancelText() {
        if (cancelled == null) {
            return;
        }
        cancelled.setVisible(false);
    }

    public void dontLetCheckout() {
        if (cancelled != null)
            clearCancelText();

        if (cannotCheckout == null) {
            cannotCheckout = new Text("Cart empty.");
            cannotCheckout.setTranslateX(415);
            cannotCheckout.setTranslateY(470);
            pane.getChildren().add(cannotCheckout);
            return;
        }

        cannotCheckout.setVisible(true);
    }

    public void clearCannotCheckoutText() {
        if (cannotCheckout == null) {
            return;
        }
        cannotCheckout.setVisible(false);
    }

    @Override
    public Scene getScene() {
        return scene;
    }

    @Override
    public void draw() {
        // TODO show recent

        // TODO show all products

    }

    @Override
    public void run() {
        // TODO get user if logged in

    }

}
