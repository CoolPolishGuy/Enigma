package com.loanlassalle.enigma.client.views;

import com.loanlassalle.enigma.client.controllers.Client;
import com.loanlassalle.enigma.client.controllers.ConfigurationManager;
import com.loanlassalle.enigma.client.models.InfoRoom;
import com.loanlassalle.enigma.client.models.Player;
import com.loanlassalle.enigma.client.models.Room;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Optional;

/**
 * This class display a window who give to the player a list of available rooms
 * and allow the player to choose a nickname.
 *
 * @author Tano Iannetta
 */
public class ControllerFrameRooms {

    @FXML
    public TableView<InfoRoom> tableRooms;
    public TableColumn<Object, Object> columnRooms;
    public TableColumn<Object, Object> columnPlayers;
    public TableColumn<Object, Object> columnQuestions;
    public TableColumn<Object, Object> columnScore;
    public TextField textFieldLogin;
    private ConfigurationManager configurationManager = ConfigurationManager.getInstance();
    private Stage secondaryStage = null;
    private Client client = Client.getInstance();
    private Hashtable<Integer, Room> listRooms = null;

    /**
     * pop up alert box before closing
     * It allow user to confirm his choice of leaving the game
     * It is placed here, to allow to use only one main
     */
    private EventHandler<WindowEvent> confirmCloseEventHandler = event -> {
        Alert closeConfirmation = new Alert(
                Alert.AlertType.CONFIRMATION,
                configurationManager.getString("popup.confirmLeave")
        );

        Button exitButton = (Button) closeConfirmation.getDialogPane().lookupButton(
                ButtonType.OK
        );

        exitButton.setText(configurationManager.getString("button.leave"));
        closeConfirmation.setHeaderText(configurationManager.getString("button.confirm"));
        closeConfirmation.initModality(Modality.APPLICATION_MODAL);
        closeConfirmation.initOwner(secondaryStage);

        closeConfirmation.setX(secondaryStage.getX() + secondaryStage.getWidth() / 2);
        closeConfirmation.setY(secondaryStage.getY() + secondaryStage.getHeight() / 4);

        Optional<ButtonType> closeResponse = closeConfirmation.showAndWait();
        if (!ButtonType.OK.equals(closeResponse.get())) {
            event.consume();
        }

    };

    // initialize frame
    public void initialize() {

        // start client
        try {
            client.connect();


        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Enigma");
            alert.setHeaderText(configurationManager.getString("popup.noConnexion"));
            alert.setContentText(configurationManager.getString("popup.tryLater"));
            alert.showAndWait();
            e.printStackTrace();
            System.exit(1); //end application

        }
        columnRooms.setCellValueFactory(new PropertyValueFactory<>("roomName"));
        columnPlayers.setCellValueFactory(new PropertyValueFactory<>("nbrPlayers"));
        columnQuestions.setCellValueFactory(new PropertyValueFactory<>("currentQuestion"));
        columnScore.setCellValueFactory(new PropertyValueFactory<>("topScore"));

        displayRooms();
    }

    private void displayRooms() {

        try {
            listRooms = client.getRooms();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // display infos about rooms
        final ObservableList<InfoRoom> roomsInfo = FXCollections.observableArrayList();
        List<InfoRoom> listInfoRooms = new ArrayList<>();

        for (Room room : listRooms.values()) {
            InfoRoom r = new InfoRoom(room.getIdRoom(), room.getName(), room.getPlayerList().size(),
                    room.getCurrentIdRiddle(), room.getBestScore());
            listInfoRooms.add(r);
        }
        roomsInfo.removeAll();
        roomsInfo.addAll(listInfoRooms); // add in the table
        tableRooms.setItems(roomsInfo);

    }

    /**
     * Put the selected room in the attribute room of client
     */
    public void getSelectedRoom() {
        InfoRoom r = tableRooms.getSelectionModel().getSelectedItem();
        if (r != null) {
            Room selectedRoom = listRooms.get(Integer.valueOf(r.getRoomID()));
            client.setRoom(selectedRoom); // give the room to the client
        }
    }

    /**
     * Connect to the selected room if login available
     * otherwise, the user have to enter a new login
     *
     * @param actionEvent when button ENTER pressed
     */
    public void connectionRoom(ActionEvent actionEvent) {
        if (!textFieldLogin.getText().isEmpty() && client.getRoom() != null) {
            //check if login available
            Player p = new Player(textFieldLogin.getText());

            client.setPlayer(p); // keep player in the client
            try {
                if (client.connectionToRoom(client.getPlayer().getName(),
                        client.getRoom().getIdRoom())) {

                    // add current player to the list
                    client.getRoom().getPlayerList().add(client.getPlayer());
                    openGameFrame(actionEvent);

                } else {
                    // room full or invalide name
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle(configurationManager.getString("popup.error"));
                    alert.setHeaderText(configurationManager.getString(
                            "popup.roomFullOrBadLogin"));
                    alert.setContentText(configurationManager.getString(
                            "popup.chooseOther"));
                    alert.showAndWait();
                    // name empty or no room available
                    textFieldLogin.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");

                    // refresh list
                    displayRooms();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // name empty or no room available
            textFieldLogin.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
        }
    }

    /**
     * When ENTER key is pressed, connect to a room
     *
     * @param actionEvent ENTER key
     */
    @FXML
    public void onEnter(ActionEvent actionEvent) {
        connectionRoom(actionEvent);
    }

    /**
     * Disconnect Rooms frame to open the game frame
     *
     * @param event when the player is connected in a room
     */
    private void openGameFrame(ActionEvent event) {
        try {
            // create game window
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource(
                    "com/loanlassalle/enigma/client/resources/views/FrameGame.fxml"));
            secondaryStage = new Stage();
            secondaryStage.setTitle("Enigma");
            secondaryStage.setScene(new Scene(root, 1600, 1000));
            secondaryStage.setOnCloseRequest(confirmCloseEventHandler);
            secondaryStage.show();
            // get current windows with the event (button)
            ((Node) (event.getSource())).getScene().getWindow().hide();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Refresh the list of the available rooms
     */
    public void refresh() {
        displayRooms();
    }
}