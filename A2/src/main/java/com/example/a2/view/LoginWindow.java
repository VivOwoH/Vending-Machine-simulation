package com.example.a2.view;

import com.example.a2.HelloApplication;
import com.example.a2.Sys;

import com.example.a2.User;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class LoginWindow implements Window {
    protected Pane pane;
    protected Scene scene;
    protected Text titleTxt;
    protected Text text;
    protected Text username;
    protected Text password;
    protected TextField captureUsername;
    protected PasswordField capturePassword;
    protected Button redirectButton;
    protected int width = 400;
    protected int height = 300;
    protected HelloApplication app;
    protected Sys sys;

    public LoginWindow(HelloApplication app, Sys sys) {
        this.app = app;
        this.sys = sys;
        
        pane = new Pane();
        scene = new Scene(pane, width, height);
        text = new Text();
        username = new Text("Username:");
        password = new Text("Password:");

        // declare shapes here
        username.setLayoutX(130);
        username.setLayoutY(95);
        pane.getChildren().add(username);

        captureUsername = new TextField();
        pane.getChildren().add(captureUsername);

        captureUsername.setLayoutY(100);
        captureUsername.setLayoutX(130);

        password.setLayoutX(130);
        password.setLayoutY(145);
        pane.getChildren().add(password);

        capturePassword = new PasswordField();
        pane.getChildren().add(capturePassword);

        capturePassword.setLayoutY(150);
        capturePassword.setLayoutX(130);

        titleTxt = new Text("Login in");
        titleTxt.setLayoutY(60);
        titleTxt.setLayoutX(130);
        pane.getChildren().add(titleTxt);

        text.setText("");
        text.setLayoutY(200);
        text.setLayoutX(130);
        pane.getChildren().add(text);

        cfgRedirectButton();
        cfgInput();
    }

    public void cfgRedirectButton() {
        // registration button
        redirectButton = new Button("Sign up");
        redirectButton.setTranslateX(180);
        redirectButton.setTranslateY(230);

        // action event
        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                RegistrationWindow registration = new RegistrationWindow(app, sys);
                app.setScene(registration.getScene());
            }
        };

        // when button is pressed
        redirectButton.setOnAction(event);

        pane.getChildren().add(redirectButton);
    }

    public void cfgInput() {
        // action event
        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                String username = captureUsername.getText();
                String password = capturePassword.getText();

                // Null username and password creates error, so we manually set an anon user
                // Password is base 64 encryption of string "Anonymous"
                if (username.equals("") && password.equals("")) {
                    
                    String result = sys.getDatabase().getUserPassword("Anonymous");

                    if (result == null) { // no anon user
                        sys.getDatabase().addUser("Anonymous", "QW5vbnltb3Vz", "User");
                        sys.getUserbase().updateUserList();
                    }
                    User currentUser = sys.getUserbase().getUserByID(sys.getDatabase().getUserID("Anonymous"));
                    app.getHomeWindow().loadUserAfterLogin(currentUser);
                    sys.setCurrentUser(currentUser);
                    app.setScene(app.getHomeWindow().getScene());
                    return;
                }

                String result = sys.getDatabase().getUserPassword(username);

                // if user exists, try and match the password
                if (result != null) {
                    if (password.equals(result)) {
                        text.setText("User matched");
                        User currentUser = sys.getUserbase().getUserByID(sys.getDatabase().getUserID(username));
                        // System.out.println(currentUser);
                        app.getHomeWindow().loadUserAfterLogin(currentUser);
                        sys.setCurrentUser(currentUser);
                        app.setScene(app.getHomeWindow().getScene());
                        app.makeAdminWindow();
                    } else {
                        text.setText("Wrong password");
                    }
                } else {
                    text.setText("Username not found");
                }
            }
        };

        // press enter in either text box
        captureUsername.setOnAction(event);
        capturePassword.setOnAction(event);
    }

    public Scene getScene() {
        return scene;
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
