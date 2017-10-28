package com.loanlassalle.enigma.server.controllers.client.Handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.loanlassalle.enigma.server.controllers.ConfigurationManager;
import com.loanlassalle.enigma.server.controllers.JsonObjectMapper;
import com.loanlassalle.enigma.server.controllers.Protocol;
import com.loanlassalle.enigma.server.controllers.client.ClientHandler;
import com.loanlassalle.enigma.server.models.Room;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gère la requête Protocol.ROOM
 *
 * @author Lassalle Loan, Jérémie Zanone
 * @since 13.04.2017
 */
public class RoomHandler implements IHandler {

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

    private RoomHandler() {
        configurationManager = ConfigurationManager.getInstance();
        logger = Logger.getLogger(ClientHandler.class.getName());
        jsonObjectMapper = JsonObjectMapper.getInstance();
    }

    /**
     * Fournit l'unique instance de la classe (singleton)
     *
     * @return unique instance de la classe
     */
    public static RoomHandler getInstance(ClientHandler clientHandler) {
        SingletonHolder.instance.clientHandler = clientHandler;

        return SingletonHolder.instance;
    }

    /**
     * Envoie les salons disponibles
     */
    public void handle() {
        String result;

        if (!clientHandler.isConnected()) {
            try {
                // Envoie au client la liste des salons
                result = jsonObjectMapper.toJson(Room.getRoomHashtable());
                clientHandler.sendProtocolLine(Protocol.ROOMS, result);
            } catch (JsonProcessingException e) {

                // Indique au client qu'une erreur s'est produite
                result = Protocol.NOT_CONNECTED;
                clientHandler.sendProtocolLine(Protocol.ROOMS, result);

                logger.log(Level.INFO, e.getMessage(), e);
            }
        } else {

            // Indique au client qu'il est connecté
            result = Protocol.CONNECTED;
            clientHandler.sendProtocolLine(Protocol.ROOMS, result);
        }

        logger.info(configurationManager.getString("clienHandler.result", result));
    }

    /**
     * Utilisé pour créer un singleton de la classe
     */
    private static class SingletonHolder {
        private final static RoomHandler instance = new RoomHandler();
    }
}
