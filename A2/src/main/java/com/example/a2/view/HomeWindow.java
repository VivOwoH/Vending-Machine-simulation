package com.example.a2.view;

import java.util.HashMap;
import java.util.Map;

import com.example.a2.HelloApplication;
import com.example.a2.Sys;
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
    private ComboBox comboBox;
    private Button cancelButton;
    private Text cancelled;

    private Sys sys;
    private HelloApplication app;

    private ControlHandler controlHandler;
    private HashMap<Integer, Button> productButtons;

    private Button adminButton;

    public HomeWindow(HelloApplication app, Sys system) {
        this.sys = system;
        controlHandler = new ControlHandler(sys);
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
        cancelButton = new Button("Cancel");
        cancelButton.setTranslateX(415);
        cancelButton.setTranslateY(430);
        cancelButton.setMinWidth(70);
        pane.getChildren().add(cancelButton);
        controlHandler.cancelTransactionHandle(this, cancelButton);

        // change to admin
        adminButton = new Button("Admin");
        adminButton.setTranslateX(420);
        adminButton.setTranslateY(135);
        pane.getChildren().add(adminButton);
        controlHandler.adminWindowHandler(app, adminButton);
    }

    public void cfgProductPane() {
        scrollPane = new ScrollPane();
        scrollPane.setPrefSize(380, 480);
        scrollPane.relocate(20, 60);

        allTxt = new Text("Products");
        allTxt.setFont(new Font(30));
        VBox box = new VBox();
        box.getChildren().add(allTxt);

        HBox currHBox = new HBox();
        currHBox.setSpacing(10);
        int hcount = 0;

        productButtons = new HashMap<>();

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

        checkout.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String currentUserName = sys.getCurrentUser().getUsername();
                System.out.println("user: " + currentUserName + " Pressed with ID: " +
                        sys.getDatabase().getUserID(currentUserName));

                for (Map.Entry<Integer, Integer> entry : sys.getVendingMachine().getCart().entrySet()) {
                    int prodID = entry.getKey();
                    int qty = entry.getValue();
                    boolean success = controlHandler.checkoutButtonHandle(sys.getDatabase().getUserID(currentUserName),
                            prodID, qty, sys.getDatabase());

                    if (success) {
                        text.setText("Transaction added");
                        // stock already updated when user add to cart, just need to commit to database
                        int stock = sys.getVendingMachine().findProductByID(prodID).getQty();
                        sys.getVendingMachine().updateProduct(prodID, Integer.toString(stock), "Quantity");
                    }
                }
                sys.getVendingMachine().updateProductInventory(); // refresh inventory
            }
        });
    }

    public void confirmCancelled() {
        if (cancelled == null) {
            cancelled = new Text("Cart cleared.");
            cancelled.setTranslateX(415);
            cancelled.setTranslateY(470);
            pane.getChildren().add(cancelled);
            return;
        }

        cancelled.setVisible(true);
    }

    public void clearCancelText() {
        cancelled.setVisible(false);

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
