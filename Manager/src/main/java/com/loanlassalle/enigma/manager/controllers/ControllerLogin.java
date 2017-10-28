package com.loanlassalle.enigma.manager.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Cette classe permet de se gerer la connection à la gestion d'énimges
 *
 * @author Wojciech Myszkorowski
 * @since 13.04.2017
 */
public class ControllerLogin {
    private final String LOGIN = "admin";
    private final String MDP = "admin";
    @FXML
    public TextField UsernameField;
    public TextField PasswordField;
    public Label statusField;
    public Button LoginButton;

    /**
     * permet d'appuyer sur la toucher enter pour se connecter
     *
     * @throws Exception
     */
    public void onEnter() throws Exception {
        checkLogin();
    }

    /**
     * Verifie que le login soit correct sinon affiche un message d'erreur
     *
     * @throws Exception
     */
    private void checkLogin() throws Exception {
        if (UsernameField.getText().equals(LOGIN) && PasswordField.getText().equals(MDP)) {
            statusField.setText("Login correct");
            Stage primaryStage = new Stage();
            Parent root = FXMLLoader.load(getClass().getClassLoader()
                    .getResource("com/loanlassalle/enigma/manager/resources/views/Admin.fxml"));
            primaryStage.setScene(new Scene(root, 786, 538));
            primaryStage.show();
            ((Stage) LoginButton.getScene().getWindow()).close();
        } else {
            statusField.setText("Login incorrect");
        }
    }
}




