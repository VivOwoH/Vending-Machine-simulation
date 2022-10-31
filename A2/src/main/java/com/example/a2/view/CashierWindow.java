package com.example.a2.view;

import com.example.a2.Sys;

import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class CashierWindow implements Window {
    private Pane pane;
    private Scene scene;
    private int width = 500;
    private int height = 700;

    private Sys system;
    private ControlHandler controlHandler;

    private Button home;
    private TextField denomination;
    private TextField cashQty;
    private Text cashText;
    private Button submitCashChange;
    private Text cashMsg;
    private Text reportTitle;
    private ScrollPane reportPane;
    private ComboBox reportType;
    private String reportOptionsCashier[] = { "Available change", "Transactions" };
    private String reportOptionsOwner[] = { "Available change", "Transactions", "Accounts", "Cancelled transactions" };

    public CashierWindow(Sys system, ControlHandler controlHandler) {
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
        // report
        reportType = new ComboBox(FXCollections.observableArrayList(reportOptionsOwner)); // depends on role
        reportType.setTranslateX(10);
        reportType.setTranslateY(300);
        reportType.setPromptText("Report type");

        reportPane = new ScrollPane();
        reportPane.setPrefSize(300, 200);
        reportPane.relocate(10, 330);
        VBox box = new VBox();

        // TODO drawReport in controlHandler
        controlHandler.drawReport(reportType, box);

        reportPane.setContent(box);

        pane.getChildren().addAll(reportPane, reportType);

    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

    }
}
