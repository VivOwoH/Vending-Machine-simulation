package com.example.a2.view;

import com.example.a2.Sys;
import com.example.a2.VendingMachine;

import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;

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
    private Button submitChange;
    private String changeOptions[] = {"Name", "Code", "Category", "Quantity", "Price"};
    private Button home;
    private TextField denomination;
    private TextField cashQty;
    private Text cashText;
    private Button submitCashChange;
    private Text cashMsg;
    private Text reportTitle;
    private ScrollPane reportPane;
    private ComboBox reportType;
    private String reportOptionsCashier[] = {"Available change", "Transactions"};
    private String reportOptionsOwner[] = {"Available change", "Transactions", "Accounts", "Cancelled transactions"};

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
    
        controlHandler.updateProductHandler(this, submitChange, idField, changeField, combobox);

        pane.getChildren().addAll(updateText, idField, changeField, combobox, submitChange);

        //update notes and coins
        cashText = new Text("Update notes/coins");
        cashText.setTranslateX(10);
        cashText.setTranslateY(160);

        denomination = new TextField();
        denomination.setTranslateX(10);
        denomination.setTranslateY(170);
        denomination.setPromptText("Denomination");
        
        cashQty = new TextField();
        cashQty.setTranslateX(10);
        cashQty.setTranslateY(200);
        cashQty.setPromptText("Quantity");

        submitCashChange = new Button("Submit");
        submitCashChange.setTranslateX(10);
        submitCashChange.setTranslateY(230);

        controlHandler.updateCashHandler(this, submitCashChange, denomination, cashQty);

        cashMsg = new Text();
        cashMsg.setTranslateX(70);
        cashMsg.setTranslateY(240);

        pane.getChildren().addAll(cashText, denomination, cashQty, submitCashChange, cashMsg);

        //report
        reportTitle = new Text("Report");
        reportTitle.setTranslateX(10);
        reportTitle.setTranslateY(290);
        pane.getChildren().add(reportTitle);

        draw();
    }

    @Override
    public Scene getScene() {
        return this.scene;
    }

    @Override
    public void draw() {
        //report
        reportType = new ComboBox(FXCollections.observableArrayList(reportOptionsOwner)); //depends on role
        reportType.setTranslateX(10);
        reportType.setTranslateY(300);
        reportType.setPromptText("Report type");

        reportPane = new ScrollPane();
        reportPane.setPrefSize(300, 200);
        reportPane.relocate(10, 330);
        VBox box = new VBox();

        //TODO drawReport in controlHandler
        controlHandler.drawReport(reportType, box);

        reportPane.setContent(box);
        
        pane.getChildren().addAll(reportPane, reportType);
        
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        
    }

    public void setCashText(String s){
        this.cashMsg.setText(s);
    }
    
}
