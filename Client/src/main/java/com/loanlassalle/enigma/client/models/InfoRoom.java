package com.loanlassalle.enigma.client.models;

import com.loanlassalle.enigma.client.controllers.ConfigurationManager;

/**
 * This class allow us to display informations about a room and the game in it. It is used
 * when we display the list of available rooms
 *
 * @author Tano Iannetta
 */
public class InfoRoom {

    private static final ConfigurationManager CONFIGURATION_MANAGER;

    static {
        CONFIGURATION_MANAGER = ConfigurationManager.getInstance();
    }

    // only for display, so strings are fine
    private String roomName;
    private String roomID;
    private String nbrPlayers;
    private String questionMax;
    private String playerMax;
    private String topScore;
    private String currentQuestion;

    public InfoRoom(Integer roomID, String roomName, int nbrPlayers, int currentQuestion, int topScore) {
        this.roomID = String.valueOf(roomID);
        this.roomName = roomName;
        this.nbrPlayers = String.valueOf(nbrPlayers);
        this.topScore = String.valueOf(topScore);
        this.currentQuestion = String.valueOf(currentQuestion);

        this.questionMax = CONFIGURATION_MANAGER.getProperty("game.questionMax");
        this.playerMax = CONFIGURATION_MANAGER.getProperty("game.playerMax");

        this.nbrPlayers += "/" + playerMax;
        this.currentQuestion += "/" + questionMax;
    }

    public String getQuestionMax() {
        return questionMax;
    }

    public void setQuestionMax(String questionMax) {
        this.questionMax = questionMax;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getNbrPlayers() {
        return nbrPlayers;
    }

    public void setNbrPlayers(String nbrPlayers) {
        this.nbrPlayers = nbrPlayers;
    }

    public String getPlayerMax() {
        return playerMax;
    }

    public void setPlayerMax(String playerMax) {
        this.playerMax = playerMax;
    }

    public String getTopScore() {
        return topScore;
    }

    public void setTopScore(String topScore) {
        this.topScore = topScore;
    }

    public String getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(String currentQuestion) {
        this.currentQuestion = currentQuestion;
    }
}
