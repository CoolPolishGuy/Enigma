package com.loanlassalle.enigma.server.controllers.client.Handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.loanlassalle.enigma.server.controllers.ConfigurationManager;
import com.loanlassalle.enigma.server.controllers.JsonObjectMapper;
import com.loanlassalle.enigma.server.controllers.Protocol;
import com.loanlassalle.enigma.server.controllers.client.ClientHandler;
import com.loanlassalle.enigma.server.controllers.client.ClientWorker;
import com.loanlassalle.enigma.server.models.Player;
import com.loanlassalle.enigma.server.models.Room;

/**
 * Gère la requête Protocol.DISCONNECT
 *
 * @author Lassalle Loan, Jérémie Zanone
 * @since 13.04.2017
 */
public class DisconnectHandler implements IHandler {

    /**
     * Utilisé pour accéder aux fichiers de propriétés
     */
    private final ConfigurationManager configurationManager;

    /**
     * Utilisé pour effectuer le mapping entre un objet et sa réprésentation JSON
     */
    private final JsonObjectMapper jsonObjectMapper;

    /**
     * Utilisé pour réceptionner le client
     */
    private ClientHandler clientHandler;

    private DisconnectHandler() {
        configurationManager = ConfigurationManager.getInstance();
        jsonObjectMapper = JsonObjectMapper.getInstance();
    }

    /**
     * Fournit l'unique instance de la classe (singleton)
     *
     * @return unique instance de la classe
     */
    public static DisconnectHandler getInstance(ClientHandler clientHandler) {
        SingletonHolder.instance.clientHandler = clientHandler;

        return SingletonHolder.instance;
    }

    /**
     * Déconnecte le client du serveur
     *
     * @throws JsonProcessingException si le mapping JSON a échoué
     */
    public void handle() throws JsonProcessingException {
        ClientWorker clientWorker = clientHandler.getClientWorker();
        Room room = clientHandler.getRoom();
        Player player = clientHandler.getPlayer();

        if (room != null && player != null) {

            // Supprime le joueur des joueurs du salon et des clients du serveurs
            room.getPlayerList().remove(player);
            ClientWorker.getClientWorkerList().remove(clientWorker);

            // Indique la déconnexion du client aux autres clients
            // et envoie le nouveau tableau des scores
            clientHandler.sendToChatAllPlayerReady(
                    configurationManager.getString("game.gameMaster"),
                    configurationManager.getString(
                            "clienHandler.playerLeavingRoom",
                            player.getName()));
            clientHandler.sendToAllRoom(Protocol.SCOREBOARD, jsonObjectMapper.toJson(room));

            // Supprime le salon si il ne possède plus aucun joueur
            if (room.isEmpty()) {
                Room.getRoomHashtable().remove(room.getIdRoom());
            }
        }
    }

    /**
     * Utilisé pour créer un singleton de la classe
     */
    private static class SingletonHolder {
        private final static DisconnectHandler instance = new DisconnectHandler();
    }
}
