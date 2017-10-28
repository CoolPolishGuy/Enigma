package com.loanlassalle.enigma.manager;

import com.loanlassalle.enigma.manager.controllers.ConfigurationManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author Wojciech Myszkorowski
 * @since 13.04.2017
 */
public class MainManager extends Application {

    public static void main(String[] args) {
        ConfigurationManager.getInstance();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("com/loanlassalle/enigma/manager/resources/views/Login.fxml"));
        primaryStage.setScene(new Scene(root, 700, 400));
        primaryStage.show();
        //primaryStage.close();
    }
}
