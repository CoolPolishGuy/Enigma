package com.loanlassalle.enigma.server.controllers.client.Handlers;

import com.loanlassalle.enigma.server.controllers.ConfigurationManager;
import com.loanlassalle.enigma.server.controllers.JsonObjectMapper;
import com.loanlassalle.enigma.server.controllers.Protocol;
import com.loanlassalle.enigma.server.controllers.client.ClientHandler;
import com.loanlassalle.enigma.server.controllers.client.ClientWorker;
import com.loanlassalle.enigma.server.models.Player;
import com.loanlassalle.enigma.server.models.Room;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Gère la requête Protocol.CONNECT
 *
 * @author Lassalle Loan, Jérémie Zanone
 * @since 13.04.2017
 */
public class ConnectHandler implements IHandler {

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

    private ConnectHandler() {
        configurationManager = ConfigurationManager.getInstance();
        logger = Logger.getLogger(ClientHandler.class.getName());
        jsonObjectMapper = JsonObjectMapper.getInstance();
    }

    /**
     * Fournit l'unique instance de la classe (singleton)
     *
     * @return unique instance de la classe
     */
    public static ConnectHandler getInstance(ClientHandler clientHandler) {
        SingletonHolder.instance.clientHandler = clientHandler;

        return SingletonHolder.instance;
    }

    /**
     * Ajoute le client aux client connectés du serveur et au salon choisi
     *
     * @throws IOException si la communication avec le client a échoué
     */
    public void handle() throws IOException {
        ClientWorker clientWorker = clientHandler.getClientWorker();
        Room room;
        Player player = null;

        String result;

        if (!clientHandler.isConnected()) {

            // Récupère le salon et le pseudonyme choisi par le client
            Room roomReceived = Room.getRoomHashtable(Integer.valueOf(clientHandler.receiveLine()));
            String pseudo = clientHandler.receiveLine();

            // Vérification de la longueur du pseudonyme
            if (pseudo.length() <= configurationManager.getIntegerProperty("player.nameSizeMax")) {
                clientHandler.setPlayer(new Player(pseudo));
                player = clientHandler.getPlayer();
            }

            // Si le salon n'est pas plein et que le pseudonyme du joueur est unique
            if (roomReceived != null
                    && !roomReceived.isFull()
                    && player != null
                    && !clientHandler.playerExist(roomReceived, player)) {

                // Ajoute le client aux clients connectés au serveur
                ClientWorker.getClientWorkerList().add(clientWorker);

                // Ajoute le joueur au salon
                clientHandler.setRoom(roomReceived);
                room = clientHandler.getRoom();
                room.getPlayerList().add(player);

                // Ajoute un salon si il n'y a aucun salon vide
                Room.addEmptyRoom();
                clientHandler.setConnected(true);

                // Indique au client qu'il est connecté
                result = Protocol.CONNECTED;
                clientHandler.sendProtocolLine(Protocol.CONNECT, result);

                // Indique l'arrivée d'un nouveau client
                clientHandler.sendToChatAllPlayerReady(
                        configurationManager.getString("game.gameMaster"),
                        configurationManager.getString("clienHandler.newPlayerEntered", player.getName()));

                clientHandler.sendToAllRoom(Protocol.SCOREBOARD, jsonObjectMapper.toJson(room));
            } else {

                // Indique au client qu'il n'est pas connecté
                result = Protocol.NOT_CONNECTED;
                clientHandler.sendProtocolLine(Protocol.CONNECT, result);
            }
        } else {

            // Indique au client qu'il est connecté
            result = Protocol.CONNECTED;
            clientHandler.sendProtocolLine(Protocol.CONNECT, result);
        }

        logger.info(configurationManager.getString("clienHandler.result", result));
    }

    /**
     * Utilisé pour créer un singleton de la classe
     */
    private static class SingletonHolder {
        private final static ConnectHandler instance = new ConnectHandler();
    }
}
