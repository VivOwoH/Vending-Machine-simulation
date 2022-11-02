package com.example.a2.view;

import com.example.a2.Sys;
import com.example.a2.VendingMachine;
import com.example.a2.Owner;
import com.example.a2.Cashier;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class AdminWindow implements Window{

    private Pane pane;
    private Scene scene;
    private int width = 500;
    private int height = 700;

    private Sys system;
    private ControlHandler controlHandler;

    private Text updateText;
    private TextField idField;
    private TextField changeField;
    private ComboBox combobox;
    private Text productMsg;
    private String changeOptions[] = {"Name", "Code", "Category", "Quantity", "Price"};
    private ComboBox roleComboBox;
    private Button submitRoleChange;
    private Button submitChange;
    private TextField userID;
    private Text roleMsg;
    private String roleOptions[] = {"Owner", "User", "Cashier", "Seller"};
    private Button home;
    private TextField denomination;
    private TextField cashQty;
    private Text cashText;
    private Text roleText;
    private Button submitCashChange;
    private Text cashMsg;
    private Text reportTitle;
    private ScrollPane reportPane;
    private ComboBox reportType;
    private String reportOptionsOwner[] = {"Available change", "Transactions", "Accounts", "Cancelled transactions", "Item details", "Item summary"};
    private String reportOptionsCashier[] = {"Available change", "Transactions"};
    private String reportOptionsSeller[] = {"Item details", "Item summary"};
    private Button refreshButton;
    private VBox box;
    private Text reportGeneratedText;

    public AdminWindow(Sys system, ControlHandler controlHandler) {
        pane = new Pane();
        scene = new Scene(pane, width, height);
        this.system = system;
        this.controlHandler = controlHandler;

        // nav home
        home = new Button("Back");
        home.setTranslateX(450);
        home.setTranslateY(10);
        pane.getChildren().add(home);
        controlHandler.toHomeWindowHandle(home);
    
        //product update
        updateText = new Text("Update Product Information");
        updateText.setTranslateX(10);
        updateText.setTranslateY(20);
        
        idField = new TextField();
        idField.setTranslateX(10);
        idField.setTranslateY(40);
        idField.setPromptText("Product ID");

        changeField = new TextField();
        changeField.setTranslateX(10);
        changeField.setTranslateY(70);
        changeField.setPromptText("Change value");
        
        combobox = new ComboBox(FXCollections.observableArrayList(changeOptions));
        combobox.setTranslateX(180);
        combobox.setTranslateY(70);
        combobox.setPromptText("update field");

        submitChange = new Button("Submit");
        submitChange.setTranslateX(10);
        submitChange.setTranslateY(100);

        productMsg = new Text();
        productMsg.setTranslateX(70);
        productMsg.setTranslateY(120);
    
        controlHandler.updateProductHandler(this, submitChange, idField, changeField, combobox, productMsg);

        pane.getChildren().addAll(updateText, idField, changeField, combobox, submitChange, productMsg);

        //update notes and coins
        cashText = new Text("Update notes/coins");
        cashText.setTranslateX(10);
        cashText.setTranslateY(160);

        denomination = new TextField();
        denomination.setTranslateX(10);
        denomination.setTranslateY(170);
        denomination.setPromptText("Denomination (only double value)");
        
        cashQty = new TextField();
        cashQty.setTranslateX(10);
        cashQty.setTranslateY(200);
        cashQty.setPromptText("Quantity");

        submitCashChange = new Button("Submit");
        submitCashChange.setTranslateX(10);
        submitCashChange.setTranslateY(230);

        cashMsg = new Text();
        cashMsg.setTranslateX(70);
        cashMsg.setTranslateY(240);

        controlHandler.updateCashHandler(this, submitCashChange, denomination, cashQty, cashMsg);

        pane.getChildren().addAll(cashText, denomination, cashQty, submitCashChange, cashMsg);

        //update roles
        roleText = new Text("Update user roles");
        roleText.setTranslateX(10);
        roleText.setTranslateY(550);

        userID = new TextField();
        userID.setTranslateX(10);
        userID.setTranslateY(570);
        userID.setPromptText("Username");

        roleComboBox = new ComboBox(FXCollections.observableArrayList(roleOptions));
        roleComboBox.setTranslateX(180);
        roleComboBox.setTranslateY(570);
        roleComboBox.setPromptText("Available roles");

        submitRoleChange = new Button("Submit");
        submitRoleChange.setTranslateX(10);
        submitRoleChange.setTranslateY(600);

        roleMsg = new Text();
        roleMsg.setTranslateX(10);
        roleMsg.setTranslateY(640);

        controlHandler.updateRoleHandler(userID, submitRoleChange, roleComboBox, roleMsg);

        pane.getChildren().addAll(roleText, userID, roleMsg, roleComboBox, submitRoleChange);


        //report
        reportTitle = new Text("Report");
        reportTitle.setTranslateX(10);
        reportTitle.setTranslateY(290);
        pane.getChildren().add(reportTitle);

        // report box
        box = new VBox();

        draw();
    }

    @Override
    public Scene getScene() {
        reportGeneratedText.setVisible(false);
        return this.scene;
    }

    @Override
    public void draw() {
        //report
        reportType = new ComboBox(FXCollections.observableArrayList(reportOptionsOwner));
        reportType.setTranslateX(10);
        reportType.setTranslateY(300);
        reportType.setPromptText("Report type");

        reportPane = new ScrollPane();
        reportPane.setPrefSize(300, 200);
        reportPane.relocate(10, 330);

        controlHandler.drawReport(reportType, box);

        reportPane.setContent(box);
        
        pane.getChildren().addAll(reportPane, reportType);


        cfgRefreshButton();
        pane.getChildren().add(refreshButton);
        
    }

    public void cfgRefreshButton() {
        refreshButton = new Button("Generate Report");
        refreshButton.setTranslateX(200);
        refreshButton.setTranslateY(300);

        reportGeneratedText = new Text("Report Generated!");
        reportGeneratedText.setVisible(false);
        reportGeneratedText.setTranslateX(320);
        reportGeneratedText.setTranslateY(315);
        pane.getChildren().add(reportGeneratedText);

        refreshButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                controlHandler.writeReportToFile();
                reportGeneratedText.setVisible(true);
            }
        });
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        
    }
}
