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

    public AdminWindow(Sys system) {
        pane = new Pane();
        scene = new Scene(pane, width, height);
        this.system = system;
        this.controlHandler = new ControlHandler(system);
    
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
    
        controlHandler.handleUpdateProduct(submitChange, idField, changeField, combobox);

        // test backend logic
        // system.getVendingMachine().updateProduct(100, "testing", "Name");

        pane.getChildren().addAll(updateText, idField, changeField, combobox, submitChange);
    }

    @Override
    public Scene getScene() {
        return this.scene;
    }

    @Override
    public void draw() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        
    }
    
}
