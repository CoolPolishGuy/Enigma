package com.loanlassalle.enigma.server.controllers.client;

import com.loanlassalle.enigma.server.controllers.ConfigurationManager;
import com.loanlassalle.enigma.server.controllers.Protocol;
import com.loanlassalle.enigma.server.controllers.client.Handlers.*;
import com.loanlassalle.enigma.server.models.Player;
import com.loanlassalle.enigma.server.models.Room;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gère les requêtes d'un client
 *
 * @author Lassalle Loan, Jérémie Zanone
 * @since 13.04.2017
 */
public class ClientHandler {

    /**
     * Utilisé pour accéder aux fichiers de propriétés
     */
    private static final ConfigurationManager CONFIGURATION_MANAGER;

    /**
     * Utilisé pour journaliser les actions effectuées
     */
    private static final Logger LOGGER;

    static {
        CONFIGURATION_MANAGER = ConfigurationManager.getInstance();
        LOGGER = Logger.getLogger(ClientHandler.class.getName());
    }

    /**
     * Utilisé pour réceptionner le client
     */
    private ClientWorker clientWorker;

    /**
     * Utilisé pour récupérer des informations en entrées
     */
    private BufferedReader bufferedReader;

    /**
     * Utilisé pour transmettre des informations en sorties
     */
    private PrintWriter printWriter;

    /**
     * Utilisé pour définir le salon de jeu
     */
    private Room room;

    /**
     * Utilisé pour définir le joueur
     */
    private Player player;

    /**
     * Indique si le joueur est connecté
     */
    private boolean isConnected;

    public ClientHandler(ClientWorker clientWorker) {
        this.clientWorker = clientWorker;
        bufferedReader = new BufferedReader(
                new InputStreamReader(clientWorker.getInputStream(), StandardCharsets.UTF_8));
        printWriter = new PrintWriter(
                new OutputStreamWriter(clientWorker.getOutputStream(), StandardCharsets.UTF_8),
                true);
    }

    /**
     * Fournit le réceptionniste du client
     *
     * @return le réceptionniste du client
     */
    public ClientWorker getClientWorker() {
        return clientWorker;
    }

    /**
     * Définit le réceptionniste du client
     *
     * @param clientWorker le réceptionniste du client
     */
    public void setClientWorker(ClientWorker clientWorker) {
        this.clientWorker = clientWorker;
    }

    /**
     * Fournit le flux d'entrées correspondant à l'interface réseau du client
     *
     * @return le flux d'entrées correspondant à l'interface réseau du client
     */
    public BufferedReader getBufferedReader() {
        return bufferedReader;
    }

    /**
     * Fournit le flux de sorties correspondant à l'interface réseau du client
     *
     * @return le flux de sorties correspondant à l'interface réseau du client
     */
    public PrintWriter getPrintWriter() {
        return printWriter;
    }

    /**
     * Fournit le salon du client
     *
     * @return le salon du client
     */
    public Room getRoom() {
        return room;
    }

    /**
     * Définit le salon du client
     *
     * @param room le salon du client
     */
    public void setRoom(Room room) {
        this.room = room;
    }

    /**
     * Fournit le joueur correspondant au client
     *
     * @return le joueur correspondant au client
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Définit le joueur correspondant au client
     *
     * @param player le joueur correspondant au client
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Indique l'état de connexion du client
     *
     * @return état de connexion du client
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * Définit l'état de connexion du client
     *
     * @param connected état de connexion du client
     */
    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    /**
     * Envoie un message instantanné à tous les joueurs du même salon que l'expéditeur
     * et qui sont prêt à jouer
     *
     * @param playerName nom de l'expédditeur
     * @param line       ligne à envoyer
     */
    public void sendToChatAllPlayerReady(String playerName, String line) {
        sendToAllPlayerReady(Protocol.CHAT,
                CONFIGURATION_MANAGER.getString("clienHandler.pseudoChat", playerName, line));
    }

    /**
     * Envoie une ligne avec le protocole choisit à tous les joueurs du même salon que l'expéditeur
     * et qui sont prêt à jouer
     *
     * @param protocolValue valeur du protocole
     * @param line          ligne à envoyer
     */
    public void sendToAllPlayerReady(String protocolValue, String line) {
        final List<ClientWorker> clientWorkerList = ClientWorker.getClientWorkerList();

        for (ClientWorker currentClientWorker : clientWorkerList) {
            ClientHandler currentClientHandler = currentClientWorker.getClientHandler();
            Room currentRoom = currentClientHandler.getRoom();
            Player currentPlayer = currentClientHandler.getPlayer();

            // Envoie de la ligne à tous les joueurs du même salon que l'expéditeur
            // et qui sont prêt à jouer
            if (currentRoom != null && currentRoom.equals(room) && currentPlayer.isReady()) {
                currentClientHandler.sendProtocolLine(protocolValue, line);
            }
        }
    }

    /**
     * Envoie une ligne avec le protocole choisit à tous les joueurs du même salon
     *
     * @param protocolValue valeur du protocole
     * @param line          ligne à envoyer
     */
    public void sendToAllRoom(String protocolValue, String line) {
        final List<ClientWorker> clientWorkerList = ClientWorker.getClientWorkerList();

        for (ClientWorker currentClientWorker : clientWorkerList) {
            ClientHandler currentClientHandler = currentClientWorker.getClientHandler();
            Room currentRoom = currentClientHandler.getRoom();

            if (currentRoom != null && currentRoom.equals(room)) {
                currentClientHandler.sendProtocolLine(protocolValue, line);
            }
        }
    }

    /**
     * Envoie une ligne à tous les joueurs du même salon
     *
     * @param line ligne à envoyer
     */
    public void sendToAllRoom(String line) {
        final List<ClientWorker> clientWorkerList = ClientWorker.getClientWorkerList();

        for (ClientWorker currentClientWorker : clientWorkerList) {
            ClientHandler currentClientHandler = currentClientWorker.getClientHandler();
            Room currentRoom = currentClientHandler.getRoom();

            if (currentRoom != null && currentRoom.equals(room)) {
                currentClientHandler.sendLine(line);
            }
        }
    }

    /**
     * Gestionnaire des requêtes du client
     *
     * @throws IOException si la lecture du flux d'entrées ou l'écriture du flux de sorties ont
     *                     échoués
     */
    void handle() throws IOException {
        String command;
        boolean done = false;

        while (!done && (command = receiveLine()) != null) {
            LOGGER.info(CONFIGURATION_MANAGER.getString("clienHandler.command", command));

            switch (command) {
                case Protocol.HELP:
                    HelpHandler.getInstance(this).handle();
                    break;
                case Protocol.ROOMS:
                    RoomHandler.getInstance(this).handle();
                    break;
                case Protocol.CONNECT:
                    ConnectHandler.getInstance(this).handle();
                    break;
                case Protocol.CHAT:
                    ChatHandler.getInstance(this).handle();
                    break;
                case Protocol.RIDDLE:
                    RiddleHandler.getInstance(this).handle();
                    break;
                case Protocol.ANSWER:
                    AnswerHandler.getInstance(this).handle();
                    break;
                case Protocol.DISCONNECT:
                    DisconnectHandler.getInstance(this).handle();
                    done = true;
                    break;
                default:
                    String result = Protocol.ERROR;
                    sendProtocolLine(command, result);
                    LOGGER.info(CONFIGURATION_MANAGER.getString("clienHandler.result", result));
                    break;
            }
        }
    }

    /**
     * Indique si le joueur existe déjà dans le salon
     *
     * @param room   salon ciblé
     * @param player joueur dont il faut vérifier l'unicité
     * @return true si le joueur existe déjà, false sinon
     */
    public boolean playerExist(Room room, Player player) {
        return room.getPlayerList().contains(player);
    }

    /**
     * Envoie une ligne au client
     *
     * @param line ligne à transmettre au client
     */
    public void sendLine(String line) {
        printWriter.println(line);
    }

    /**
     * Envoie une ligne avec le protocole choisit
     *
     * @param protocolValue valeur du protocole
     * @param line          ligne à envoyer
     */
    public void sendProtocolLine(String protocolValue, String line) {
        sendLine(protocolValue);
        sendLine(line);
    }

    /**
     * Envoie un message instantanné aux autres joueurs du salon
     *
     * @param playerName nom de l'expédditeur
     * @param line       ligne à envoyer
     */
    public void sendToChatRoom(String playerName, String line) {
        sendToOthersRoom(
                Protocol.CHAT,
                CONFIGURATION_MANAGER.getString("clienHandler.pseudoChat", playerName, line));
    }

    /**
     * Envoie une ligne avec le protocole choisit aux autres joueurs du même salon que l'expéditeur
     *
     * @param protocolValue valeur du protocole
     * @param line          ligne à envoyer
     */
    private void sendToOthersRoom(String protocolValue, String line) {
        final List<ClientWorker> clientWorkerList = ClientWorker.getClientWorkerList();

        for (ClientWorker currentClientWorker : clientWorkerList) {
            ClientHandler currentClientHandler = currentClientWorker.getClientHandler();
            Room currentRoom = currentClientWorker.getClientHandler().getRoom();

            // Envoie de la ligne aux autres joueurs du même salon que l'expéditeur
            // et qui ont bien un salon définit
            if (currentClientWorker != clientWorker
                    && currentRoom != null
                    && currentRoom.equals(room)) {
                currentClientHandler.sendProtocolLine(protocolValue, line);
            }
        }
    }

    /**
     * Récupére la ligne envoyée par le client
     *
     * @return la ligne envoyée par le client
     * @throws IOException si la récupération de la ligne a échoué
     */
    public String receiveLine() throws IOException {
        try {
            return bufferedReader.readLine();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw e;
        }
    }
}
