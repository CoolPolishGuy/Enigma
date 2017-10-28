package com.loanlassalle.enigma.client;

import com.loanlassalle.enigma.client.controllers.Client;
import com.loanlassalle.enigma.client.controllers.ConfigurationManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main which start client and GUI
 */
public class MainClient extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        // launch GUI of frame room
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("com/loanlassalle/enigma/client/resources/views/FrameRooms.fxml"));

        primaryStage.setTitle(ConfigurationManager.getInstance().getProperty("title.titleGame"));

        primaryStage.setScene(new Scene(root, 1200, 800));
        primaryStage.show();

    }

    /**
     * When window is closed
     */
    @Override
    public void stop() {
        if (Client.getInstance() != null) {
            try {
                Client client = Client.getInstance();
                if (client.isConnected()) {
                    client.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
