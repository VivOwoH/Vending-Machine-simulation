package com.example.a2.view;

import com.example.a2.HelloApplication;
import com.example.a2.Sys;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

public class RegistrationWindow extends LoginWindow {

    public RegistrationWindow(HelloApplication app, Sys sys) {
        super(app, sys);
        titleTxt.setText("Sign up");
    }

    @Override
    public void cfgRedirectButton() {
        // login button
        redirectButton = new Button("Login");
        redirectButton.setTranslateX(180);
        redirectButton.setTranslateY(230);
        pane.getChildren().add(redirectButton);

        // action event
        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                LoginWindow login = new LoginWindow(app, sys);
                app.setScene(login.getScene());
            }
        };

        // when button is pressed
        redirectButton.setOnAction(event);
    }

    @Override
    public void cfgInput() {
        // action event
        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                String username = captureUsername.getText();
                String password = capturePassword.getText();

                String result = sys.getDatabase().getUserPassword(username);

                // if user exists, try and match the password
                if (result != null) {
                    text.setText("This username has been used");
                } else {// is user does not exist create it
                    sys.getDatabase().addUser(username, password, "User");
                    String displayText = "User added with username " + username;
                    text.setText(displayText);
                }
            }
        };

        // press enter in either text box
        captureUsername.setOnAction(event);
        capturePassword.setOnAction(event);
    }
}
