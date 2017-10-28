package com.loanlassalle.enigma.server.controllers.client.Handlers;

import com.loanlassalle.enigma.server.controllers.ConfigurationManager;
import com.loanlassalle.enigma.server.controllers.JsonObjectMapper;
import com.loanlassalle.enigma.server.controllers.Protocol;
import com.loanlassalle.enigma.server.controllers.client.ClientHandler;
import com.loanlassalle.enigma.server.models.Game;
import com.loanlassalle.enigma.server.models.Player;
import com.loanlassalle.enigma.server.models.Room;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Gère la requête Protocol.ANSWER
 *
 * @author Lassalle Loan, Jérémie Zanone
 * @since 13.04.2017
 */
public class AnswerHandler implements IHandler {

    /**
     * Utilisé pour accéder aux fichiers de propriétés
     */
    private final ConfigurationManager configurationManager;

    /**
     * Utilisé pour journaliser les actions effectuées
     */
    private final Logger logger;

    /**
     * Utilisé pour effectuer le mapping entre un objet et sa réprésentation JSON
     */
    private final JsonObjectMapper jsonObjectMapper;

    /**
     * Utilisé pour réceptionner le client
     */
    private ClientHandler clientHandler;

    private AnswerHandler() {
        configurationManager = ConfigurationManager.getInstance();
        logger = Logger.getLogger(ClientHandler.class.getName());
        jsonObjectMapper = JsonObjectMapper.getInstance();
    }

    /**
     * Fournit l'unique instance de la classe (singleton)
     *
     * @return unique instance de la classe
     */
    public static AnswerHandler getInstance(ClientHandler clientHandler) {
        SingletonHolder.instance.clientHandler = clientHandler;

        return SingletonHolder.instance;
    }

    /**
     * Indique au client l'état de la réponse
     *
     * @throws IOException si la communication avec le client a échoué
     */
    public void handle() throws IOException {
        Room room = clientHandler.getRoom();
        Player player = clientHandler.getPlayer();
        Game game = room.getGame();

        String result;

        if (clientHandler.isConnected()) {
            String answer = clientHandler.receiveLine();

            if (Game.correctAnswer(room
                    .getRiddleHashMap(room.getCurrentIdRiddle() - 1).getAnswers(), answer)) {

                // Envoie au joueur le statut de la réponse fournit
                result = Protocol.CORRECT;
                clientHandler.sendProtocolLine(Protocol.ANSWER, result);
                logger.info(configurationManager.getString("clienHandler.result", result));

                // Incrémente le score du joueur
                if (game.isFirstPlayer()) {
                    game.setFirstPlayer(false);
                    player.incrementScore(configurationManager
                            .getIntegerProperty("player.scoreIncrementFirstPlayer"));
                } else {
                    player.incrementScore(configurationManager
                            .getIntegerProperty("player.scoreIncrement"));
                }

                // Envoie au joueur le nombre d'essais restant
                player.decrementAttempts(1);
                result = String.valueOf(player.getAttempts());
                clientHandler.sendProtocolLine(Protocol.ATTEMPT, result);

                // Envoie à tous les joueurs le tableau des scores mis à jour
                clientHandler.sendToAllRoom(Protocol.SCOREBOARD, jsonObjectMapper.toJson(room));
            } else {

                // Envoie au joueur le statut de la réponse fournit
                result = Protocol.INCORRECT;
                clientHandler.sendProtocolLine(Protocol.ANSWER, result);
                logger.info(configurationManager.getString("clienHandler.result", result));

                // Envoie au joueur le nombre d'essais restant
                player.decrementAttempts(1);
                result = String.valueOf(player.getAttempts());
                clientHandler.sendProtocolLine(Protocol.ATTEMPT, result);
            }
        } else {

            // Indique au joueur que l'énigme n'est pas prête
            result = Protocol.NOT_DELIVERED;
            clientHandler.sendProtocolLine(Protocol.ANSWER, result);
        }

        logger.info(configurationManager.getString("clienHandler.result", result));
    }

    /**
     * Utilisé pour créer un singleton de la classe
     */
    private static class SingletonHolder {
        private final static AnswerHandler instance = new AnswerHandler();
    }
}
