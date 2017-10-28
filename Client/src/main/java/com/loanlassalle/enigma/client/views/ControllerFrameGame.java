package com.loanlassalle.enigma.client.views;

import com.loanlassalle.enigma.client.controllers.Client;
import com.loanlassalle.enigma.client.controllers.ConfigurationManager;
import com.loanlassalle.enigma.client.controllers.JsonObjectMapper;
import com.loanlassalle.enigma.client.controllers.Protocol;
import com.loanlassalle.enigma.client.models.DisplayTimer;
import com.loanlassalle.enigma.client.models.InterfacePlayer;
import com.loanlassalle.enigma.client.models.Player;
import com.loanlassalle.enigma.client.models.Room;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Frame of the game
 *
 * @author Tano Iannetta
 */
public class ControllerFrameGame {

    private static final ConfigurationManager CONFIGURATION_MANAGER =
            ConfigurationManager.getInstance();
    private static final JsonObjectMapper JSON_OBJECT_MAPPER = JsonObjectMapper.getInstance();

    @FXML
    public TextArea textAreaChat; // chat area
    public TextField seizureChat; // seizure user chat
    public TableView<Player> scores;
    public TableColumn<Object, Object> columnScore;
    public TableColumn<Object, Object> columnName;
    public TextField textFieldAnswer;
    public Pane imageAreaStatesPlayers;
    public TextArea textAreaQuestion;
    public Label labelCorrect;
    public Label labelFalse;
    public Label labelTimer;
    public ProgressBar progressBar;
    // all images and all players names
    public ImageView iv1;
    public ImageView iv2;
    public ImageView iv3;
    public ImageView iv4;
    public ImageView iv5;
    public ImageView iv6;
    public ImageView iv7;
    public ImageView iv8;
    public ImageView iv9;
    public ImageView iv10;
    public Label namePlayer1;
    public Label namePlayer2;
    public Label namePlayer3;
    public Label namePlayer4;
    public Label namePlayer5;
    public Label namePlayer6;
    public Label namePlayer7;
    public Label namePlayer8;
    public Label namePlayer9;
    public Label namePlayer10;
    public Label labelAttempts;
    public Label labelRound;
    public Button buttonSendAnswer;

    private ConfigurationManager configurationManager = ConfigurationManager.getInstance();
    private DisplayTimer displayTimer = new DisplayTimer(Integer.valueOf(
            configurationManager.getProperty("game.riddleMaximumTimeout")) / 1000); // convert to second
    private List<InterfacePlayer> interfacePlayerList = new ArrayList<>();
    private Client client = Client.getInstance();
    private Task task;
    private boolean taskContinue = true;
    private Room roomAtBeginOfRound;
    private boolean gameStarted = false;

    /**
     * Initialize the frame with the scoreboard
     * start the timer and the task to listen to the server
     * ask the server for a riddle
     */
    public void initialize() {

        // information to display on scoreboard
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnScore.setCellValueFactory(new PropertyValueFactory<>("score"));

        taskGame(); // launch task in background to listen to the server

        try {
            client.send(Protocol.RIDDLE); // notify to the server that we are ready
        } catch (IOException e) {
            e.printStackTrace();
        }
        timerStart();

    }

    /**
     * Initialize a timer
     */
    private void timerStart() {

        // launch timer
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
            displayTimer.incrementSecond(progressBar);
            labelTimer.setText(displayTimer.toString());
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

    }

    /**
     * Update the number of images depending on the players
     *
     * @param playersCurrent list of player of the current game
     * @param oldList        the list we had before receiving the new one
     */
    private synchronized void updateImages(List<Player> playersCurrent, List<Player> oldList) {

        try {
            // all images
            ImageView imagesPlayers[] = {iv1, iv2, iv3, iv4, iv5, iv6, iv7, iv8, iv9, iv10};
            // all label for names
            Label namePlayers[] = {namePlayer1, namePlayer2, namePlayer3, namePlayer4,
                    namePlayer5, namePlayer6, namePlayer7, namePlayer8, namePlayer9, namePlayer10};

            boolean alreadyPresent = false;
            for (int i = 0, j = 0; i < playersCurrent.size(); i++, j++) {

                // associate the first free image to a player
                // if the image is visible that means it belong to another player
                if (!imagesPlayers[j].isVisible()) {

                    for (InterfacePlayer anInterfacePlayerList : interfacePlayerList) {
                        if (playersCurrent.get(i).getName().equals(
                                anInterfacePlayerList.getPlayer().getName())) {
                            alreadyPresent = true;
                            break;
                        }
                    }
                    if (alreadyPresent) {
                        j--; // keep the same empty spot
                        alreadyPresent = false;
                        continue;
                    }
                    // associate one interface with a player
                    InterfacePlayer interfacePlayer = new InterfacePlayer(playersCurrent.get(i),
                            namePlayers[j], imagesPlayers[j]);
                    interfacePlayer.getImageView().setVisible(true);
                    interfacePlayer.getLabel().setVisible(true);
                    interfacePlayerList.add(i, interfacePlayer);
                }
            }

            // update images state if someone won points
            // we check that we receive a scoreboard because someone won points
            // and not for a connection or disconnection
            if (playersCurrent.size() == oldList.size() && gameStarted) {

                updateImagesStateWin(playersCurrent, oldList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Process delta between new scoreBoard and the old one to update the state of the images
     * and display the win image
     *
     * @param newList the new list of player received
     * @param oldList the list of players, especially the scores we had before receiving the new one
     */
    private void updateImagesStateWin(List<Player> newList, List<Player> oldList) {

        try {
            for (int i = 0; i < newList.size(); i++) {
                if (newList.get(i).getScore() > oldList.get(i).getScore()) // the player won point
                {
                    interfacePlayerList.get(i).getImageView().setImage(
                            new Image("com/loanlassalle/enigma/client/resources/views/win.jpg"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Process delta between the new scoreboard and the one at the begin of the round to update
     * the state of the images
     * and display the loosing image
     *
     * @param newList the new list of player received
     * @param oldList the list of players at the begin of a round
     */
    private void updateImagesStateLose(List<Player> newList, List<Player> oldList) {
        try {
            for (int i = 0; i < newList.size(); i++) {

                // check if player was present at the beginning of the round
                if (oldList.contains(newList.get(i))) {
                    // the player didn't won points or lost some
                    if (newList.get(i).getScore() <= oldList.get(i).getScore()) {
                        interfacePlayerList.get(i).getImageView().setImage(
                                new Image("com/loanlassalle/enigma/client/resources/views/drunk.jpg"));
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Free the spot (image and label) if a player is missing in the new room received
     * the check is based on the name of the player, which is sufficient because a name is unique
     *
     * @param newRoom the new room received
     * @param oldRoom the room we had before receiving the new one
     */
    private void clearDisplaySpots(Room newRoom, Room oldRoom) {

        for (int i = 0; i < oldRoom.getPlayerList().size(); i++) {
            // if player not in the new list
            if (!newRoom.getPlayerList().contains(oldRoom.getPlayerList().get(i))) {
                for (int k = 0; k < interfacePlayerList.size(); k++) {
                    // locate his interface
                    if (interfacePlayerList.get(k).getPlayer().getName().equals(
                            oldRoom.getPlayerList().get(i).getName())) {
                        interfacePlayerList.get(k).getLabel().setVisible(false);
                        interfacePlayerList.get(k).getImageView().setVisible(false);
                        interfacePlayerList.remove(interfacePlayerList.get(k));
                        break;
                    }
                }
            }
        }
    }

    /**
     * Update scoreboard
     */
    private synchronized void updateScore() {

        // display information about scores
        final ObservableList<Player> scoresPlayers = FXCollections.observableArrayList();

        List<Player> players = client.getRoom().getPlayerList();

        // clear and update existing display
        scoresPlayers.removeAll();
        scores.refresh(); // will refresh display when score is updated
        scoresPlayers.addAll(players);
        scores.setItems(scoresPlayers);
    }

    /**
     * Send message to chat
     */
    public void onEnter() {
        sendMessageToChat();
    }

    /**
     * Send message to chat
     */
    public void sendMessageToChat() {
        if (!seizureChat.getText().isEmpty()) {
            try {
                client.sendToChat(seizureChat.getText());
            } catch (IOException e) {
                e.printStackTrace();
            }

            textAreaChat.appendText(seizureChat.getText()); // display local
            textAreaChat.appendText("\n");
        }
        seizureChat.setText("");
    }

    /**
     * Summit answer
     */
    public void onEnterAnswer() {
        if (!buttonSendAnswer.isDisable()) {
            sendAnswer();
        }
    }

    /**
     * Summit answer
     */
    public void sendAnswer() {

        try {
            client.send(Protocol.ANSWER);
            client.send(textFieldAnswer.getText());
            textFieldAnswer.setText("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Task to listen to the answers from the server
     * Server may send messages by it's own so we listen to the server in background
     */
    private void taskGame() {

        task = new Task() {
            @Override
            protected Object call() throws Exception {

                String message = "";
                while (taskContinue) {
                    message = client.receive();
                    switch (message) {
                        case Protocol.CHAT:
                            synchronized (this) {
                                message = client.receive();
                                textAreaChat.appendText(message);
                                textAreaChat.appendText("\n");
                            }
                            break;
                        case Protocol.SCOREBOARD:
                            // update the score board
                            synchronized (this) {
                                message = client.receive(); // get new room
                                Room newRoom = JSON_OBJECT_MAPPER.parseJson(message, Room.class);
                                Room oldRoom = client.getRoom();
                                client.setRoom(newRoom);
                                updateScore();

                                // delay the update of the GUI to be thread safe
                                Platform.runLater(() -> {
                                    // if a player disconnected, we free his display spot
                                    if (newRoom.getPlayerList().size() <
                                            oldRoom.getPlayerList().size()) {
                                        clearDisplaySpots(newRoom, oldRoom);
                                    }
                                    updateImages(client.getRoom().getPlayerList(),
                                            oldRoom.getPlayerList());

                                });
                                // init attempts
                                Platform.runLater(() -> labelAttempts.setText(String.valueOf
                                        (client.getPlayer().getAttempts())));
                            }
                            break;

                        case Protocol.RIDDLE:

                            // delay gui to be thread safe
                            Platform.runLater(() -> {
                                // clear labels and unlock them
                                labelCorrect.setVisible(false);
                                labelFalse.setVisible(false);
                                labelAttempts.setTextFill(Color.BLACK);
                                // reset images
                                for (int i = 0; i < client.getRoom().getPlayerList().size(); i++) {
                                    interfacePlayerList.get(i).getImageView().setImage(
                                            new Image("com/loanlassalle/enigma/client/resources/views/Ndrunk.jpg"));
                                }
                            });

                            message = client.receive();
                            switch (message) {
                                case Protocol.NOT_DELIVERED:
                                    textAreaQuestion.setText(CONFIGURATION_MANAGER.getString(
                                            "game.waitPlayer"));
                                    // lock filed
                                    textFieldAnswer.setEditable(false);
                                    break;
                                case Protocol.RIDDLE_UNDERWAY:
                                    textAreaQuestion.setText(CONFIGURATION_MANAGER.getString(
                                            "game.riddleUnderway"));
                                    break;
                                case Protocol.RIDDLE_READY:
                                    textAreaQuestion.setText(CONFIGURATION_MANAGER.getString(
                                            "game.riddleReady"));

                                    break;
                                default:
                                    // we received a riddle
                                    textFieldAnswer.setEditable(true);
                                    buttonSendAnswer.setDisable(false);
                                    textAreaQuestion.setText(message); // display question

                                    displayTimer.reset(progressBar);
                                    // display timer when the riddle start
                                    labelTimer.setVisible(true);
                                    // display bar when the riddle start
                                    progressBar.setVisible(true);
                                    break;
                            }
                            break;
                        case Protocol.ANSWER:
                            message = client.receive();

                            if (message.equals(Protocol.CORRECT)) {
                                labelCorrect.setVisible(true);
                                labelFalse.setVisible(false);
                                textFieldAnswer.setEditable(false);
                                buttonSendAnswer.setDisable(true);

                            } else if (message.equals(Protocol.INCORRECT)) {
                                labelFalse.setVisible(true);
                                labelCorrect.setVisible(false);
                            }
                            break;
                        case Protocol.NOT_DELIVERED:
                            textAreaQuestion.setText(configurationManager.getString(
                                    "game.waitRiddle"));
                            break;
                        case Protocol.ATTEMPT:
                            // attempts left
                            message = client.receive();

                            // delay gui to be threads safe
                            final String attempts = message;
                            Platform.runLater(() -> {
                                labelAttempts.setText(attempts);

                                if (Integer.valueOf(attempts) == 0) {
                                    textFieldAnswer.setEditable(false);
                                    labelAttempts.setTextFill(Color.RED);
                                    buttonSendAnswer.setDisable(true);
                                } else if (Integer.valueOf(attempts) == 1) {
                                    labelAttempts.setTextFill(Color.ORANGE);
                                }
                            });
                            break;
                        case Protocol.ROUND:

                            final String count = client.receive();
                            // delay the update of the GUI tu be thread safe
                            if (count.equals(Protocol.END_ROUND)) {
                                //get new room
                                Room newRoom = null;
                                try {
                                    String room = client.receive();
                                    newRoom = JSON_OBJECT_MAPPER.parseJson(room, Room.class);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                client.setRoom(newRoom);

                                Platform.runLater(() -> {
                                    displayTimer.reset(progressBar);
                                    updateImagesStateLose(client.getRoom().getPlayerList(),
                                            roomAtBeginOfRound.getPlayerList());
                                });

                            } else {
                                if (Integer.valueOf(count) >= 1) {
                                    gameStarted = true;
                                }
                                Platform.runLater(() -> labelRound.setText(count + "/" +
                                        configurationManager.getProperty("game.questionMax")));
                                //backup scoreboard at the beginning of the round
                                roomAtBeginOfRound = client.getRoom();
                            }
                            break;
                        case Protocol.GAME:
                            message = client.receive();
                            break;
                    }

                }

                return message;
            }
        };
        new Thread(task).start();
    }

    /**
     * back in rooms frame
     *
     * @param event click on quit button
     */
    public void leave(ActionEvent event) {
        try {
            taskContinue = false;
            task.cancel(); // stop background task

            client.disconnect();

            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource(
                    "com/loanlassalle/enigma/client/resources/views/FrameRooms.fxml"));
            if (root == null) {
                System.exit(-1);
            }

            Stage primaryStage = new Stage();
            primaryStage.setTitle(configurationManager.getProperty("title.titleGame"));
            primaryStage.setScene(new Scene(root, 1200, 800));
            primaryStage.show();
            // get current windows with the event (button)
            ((Node) (event.getSource())).getScene().getWindow().hide();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
