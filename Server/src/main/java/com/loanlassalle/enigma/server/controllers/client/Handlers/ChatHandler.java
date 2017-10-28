package com.loanlassalle.enigma.server.controllers.client.Handlers;

import com.loanlassalle.enigma.server.controllers.ConfigurationManager;
import com.loanlassalle.enigma.server.controllers.Protocol;
import com.loanlassalle.enigma.server.controllers.client.ClientHandler;
import com.loanlassalle.enigma.server.models.Player;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Gère la requête Protocol.CHAT
 *
 * @author Lassalle Loan, Jérémie Zanone
 * @since 13.04.2017
 */
public class ChatHandler implements IHandler {

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

    private ChatHandler() {
        configurationManager = ConfigurationManager.getInstance();
        logger = Logger.getLogger(ClientHandler.class.getName());
    }

    /**
     * Fournit l'unique instance de la classe (singleton)
     *
     * @return unique instance de la classe
     */
    public static ChatHandler getInstance(ClientHandler clientHandler) {
        SingletonHolder.instance.clientHandler = clientHandler;

        return SingletonHolder.instance;
    }

    /**
     * Envoie les messages instannés aux clients
     *
     * @throws IOException si la communication avec le client a échoué
     */
    public void handle() throws IOException {
        Player player = clientHandler.getPlayer();

        String result;

        if (clientHandler.isConnected()) {

            // Envoie le message instantanné
            result = Protocol.SENT;
            clientHandler.sendToChatRoom(player.getName(), clientHandler.receiveLine());
        } else {

            // Indique au client qu'il n'est pas connecté
            result = Protocol.NOT_SENT;
            clientHandler.sendProtocolLine(Protocol.CHAT, result);
        }

        logger.info(configurationManager.getString("clienHandler.result", result));
    }

    /**
     * Utilisé pour créer un singleton de la classe
     */
    private static class SingletonHolder {
        private final static ChatHandler instance = new ChatHandler();
    }
}
