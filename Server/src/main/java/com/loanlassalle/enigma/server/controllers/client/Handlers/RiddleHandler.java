package com.loanlassalle.enigma.server.controllers.client.Handlers;

import com.loanlassalle.enigma.server.controllers.ConfigurationManager;
import com.loanlassalle.enigma.server.controllers.Protocol;
import com.loanlassalle.enigma.server.controllers.client.ClientHandler;
import com.loanlassalle.enigma.server.controllers.client.game.RiddleTimer;
import com.loanlassalle.enigma.server.models.Player;
import com.loanlassalle.enigma.server.models.Room;

import java.util.logging.Logger;

/**
 * Gère la requête Protocol.RIDDLE
 *
 * @author Lassalle Loan, Jérémie Zanone
 * @since 13.04.2017
 */
public class RiddleHandler implements IHandler {

    /**
     * Utilisé pour accéder aux fichiers de propriétés
     */
    private final ConfigurationManager configurationManager;

    /**
     * Utilisé pour journaliser les actions effectuées
     */
    private final Logger logger;

    /**
     * Utilisé pour réceptionner le client
     */
    private ClientHandler clientHandler;

    private RiddleHandler() {
        configurationManager = ConfigurationManager.getInstance();
        logger = Logger.getLogger(ClientHandler.class.getName());
    }

    /**
     * Fournit l'unique instance de la classe (singleton)
     *
     * @return unique instance de la classe
     */
    public static RiddleHandler getInstance(ClientHandler clientHandler) {
        SingletonHolder.instance.clientHandler = clientHandler;

        return SingletonHolder.instance;
    }

    /**
     * Envoie des énigmes et des réponses
     */
    public void handle() {
        Room room = clientHandler.getRoom();
        Player player = clientHandler.getPlayer();

        String result;

        if (clientHandler.isConnected() && room.isReady()) {
            if (room.isRiddleUnderway()) {

                // Le joueur n'obtient pas l'énigme si une énigme est en cours
                result = Protocol.RIDDLE_UNDERWAY;
                clientHandler.sendProtocolLine(Protocol.RIDDLE, result);
            } else if (room.getCurrentIdRiddle() == 0
                    && room.getPlayerList().size() ==
                    configurationManager.getIntegerProperty("game.playerMin")) {

                // Indique aux joueurs de se préparer
                clientHandler.sendToAllRoom(Protocol.RIDDLE, Protocol.RIDDLE_READY);

                result = Protocol.DELIVERED;

                // Envoie une énigme et sa réponse à intervalle de temps régulier
                new RiddleTimer(clientHandler).start();
            } else {
                // Indique au joueur de se préparer
                clientHandler.sendProtocolLine(Protocol.RIDDLE, Protocol.RIDDLE_READY);

                result = Protocol.DELIVERED;
            }
        } else {

            // Le premier joueur qui se connecte sera prêt pour jouer
            if (clientHandler.isConnected()) {
                player.setReady(true);
            }

            // Indique au joueur que la partie n'est pas prête
            result = Protocol.NOT_DELIVERED;
            clientHandler.sendProtocolLine(Protocol.RIDDLE, result);
        }

        logger.info(configurationManager.getString("clienHandler.result", result));
    }

    /**
     * Utilisé pour créer un singleton de la classe
     */
    private static class SingletonHolder {
        private final static RiddleHandler instance = new RiddleHandler();
    }
}
