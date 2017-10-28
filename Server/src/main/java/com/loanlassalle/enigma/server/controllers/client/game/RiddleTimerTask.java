package com.loanlassalle.enigma.server.controllers.client.game;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.loanlassalle.enigma.server.controllers.ConfigurationManager;
import com.loanlassalle.enigma.server.controllers.JsonObjectMapper;
import com.loanlassalle.enigma.server.controllers.Protocol;
import com.loanlassalle.enigma.server.controllers.client.ClientHandler;
import com.loanlassalle.enigma.server.models.Player;
import com.loanlassalle.enigma.server.models.Room;

import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gère les tâches à effectuer pour le jeu
 *
 * @author Lassalle Loan, Jérémie Zanone
 * @since 13.04.2017
 */
class RiddleTimerTask extends TimerTask {

    /**
     * Utilisé pour accéder aux fichiers de propriétés
     */
    private static final ConfigurationManager CONFIGURATION_MANAGER;

    /**
     * Utilisé pour journaliser les actions effectuées
     */
    private static final Logger LOGGER;

    /**
     * Utilisé pour effectuer le mapping entre un objet et sa réprésentation JSON
     */
    private static final JsonObjectMapper JSON_OBJECT_MAPPER;

    static {
        CONFIGURATION_MANAGER = ConfigurationManager.getInstance();
        LOGGER = Logger.getLogger(ClientHandler.class.getName());
        JSON_OBJECT_MAPPER = JsonObjectMapper.getInstance();
    }

    private ClientHandler clientHandler;
    private Room room;
    private Player player;

    RiddleTimerTask(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
        room = clientHandler.getRoom();
        player = clientHandler.getPlayer();
    }

    /**
     * Forunit le client
     *
     * @return le client
     */
    public ClientHandler getClientHandler() {
        return clientHandler;
    }

    /**
     * Forunit le salon correspondant au client
     *
     * @return le salon correspondant au client
     */
    public Room getRoom() {
        return room;
    }

    /**
     * Forunit le joueur correspondant au client
     *
     * @return le joueur correspondant au client
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Exécute la tâche pour le jeu
     */
    @Override
    public void run() {
        if (room.getCurrentIdRiddle()
                .equals(CONFIGURATION_MANAGER.getIntegerProperty("game.questionMax"))) {

            // Indique que le jeu est terminé, prévient tous les joueurs prêt
            room.setRiddleUnderway(false);
            sendEndGame();

            // Définis un nouveau jeu
            room.setNewGame();
            sendNewScoreboard();
        } else {

            // Attend l'arrivé des autres joueurs
            waitThread(CONFIGURATION_MANAGER.getIntegerProperty("game.timeBeforeStart"));

            // Réinitialise les essais de tous les joueurs
            room.resetRound();
            sendAttempts();

            // Démarre l'énigme
            room.setRiddleUnderway(true);
            sendRiddle();

            // Passe à la prochaine énigme
            room.incrementCurrentIdRiddle();
            sendRound();

            // Attend que le temps de l'énigme soit écoulé
            waitThread(CONFIGURATION_MANAGER.getIntegerProperty("game.riddleMaximumTimeout"));

            // Indique que l'énigme est terminée
            room.setRiddleUnderway(false);

            // Envoie la réponse de l'égnime à tous les joueurs prêt
            sendAnswer();

            sendEndRound();
        }
    }

    /**
     * Fait patienter passivement le thread pendant un durée en millisecondes
     *
     * @param millis temps d'attente inactive en millisecondes
     */
    private void waitThread(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Indique le nombre d'essais de chaqque joueur
     */
    private void sendAttempts() {
        clientHandler.sendToAllPlayerReady(Protocol.ATTEMPT, String.valueOf(player.getAttempts()));
    }

    /**
     * Envoie l'énigme aux joueurs prêt
     */
    private void sendRiddle() {
        clientHandler.sendToAllPlayerReady(
                Protocol.RIDDLE,
                room.getRiddleHashMap(room.getCurrentIdRiddle()).getQuestion());
    }

    /**
     * Indique le numéro du tour à tous les joueurs prêt
     */
    private void sendRound() {
        clientHandler.sendToAllPlayerReady(
                Protocol.ROUND,
                String.valueOf(room.getCurrentIdRiddle()));
    }

    /**
     * Indique la fin du tour à tous les joueurs prêt
     */
    private void sendEndRound() {
        clientHandler.sendToAllPlayerReady(Protocol.ROUND, Protocol.END_ROUND);

        try {
            clientHandler.sendToAllRoom(JSON_OBJECT_MAPPER.toJson(room));
        } catch (JsonProcessingException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Indique la fin du tour à tous les joueurs prêt
     */
    private void sendEndGame() {
        clientHandler.sendToAllPlayerReady(Protocol.GAME, Protocol.END_GAME);
    }

    /**
     * Envoie à tous les joueurs le tableau des scores mis à jour
     */
    private void sendNewScoreboard() {
        try {
            clientHandler.sendToAllRoom(Protocol.SCOREBOARD, JSON_OBJECT_MAPPER.toJson(room));
        } catch (JsonProcessingException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Envoie la réponse de l'égnime à tous les joueurs prêt
     */
    private void sendAnswer() {
        String firstAnswer = room.getRiddleHashMap(room.getCurrentIdRiddle() - 1)
                .getAnswers(0);

        clientHandler.sendToChatAllPlayerReady(
                CONFIGURATION_MANAGER.getString("game.gameMaster"),
                CONFIGURATION_MANAGER.getString("game.answer", firstAnswer));
    }
}
