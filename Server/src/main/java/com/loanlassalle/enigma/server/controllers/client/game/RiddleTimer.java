package com.loanlassalle.enigma.server.controllers.client.game;

import com.loanlassalle.enigma.server.controllers.ConfigurationManager;
import com.loanlassalle.enigma.server.controllers.client.ClientHandler;

import java.util.Timer;

/**
 * Exécute des tâches à intervalle de temps régulier pour le jeu
 *
 * @author Lassalle Loan, Jérémie Zanone
 * @since 13.04.2017
 */
public class RiddleTimer {

    private RiddleTimerTask riddleTimerTask;
    private Timer timer;

    public RiddleTimer(ClientHandler clientHandler) {
        riddleTimerTask = new RiddleTimerTask(clientHandler);
        timer = new Timer(true);
    }

    /**
     * Exécute la tâche à effectuer
     */
    public void start() {
        timer.scheduleAtFixedRate(
                riddleTimerTask,
                0,
                Long.valueOf(ConfigurationManager.getInstance()
                        .getProperty("game.riddleMaximumTimeout")));
    }
}
