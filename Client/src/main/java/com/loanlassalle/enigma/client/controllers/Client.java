package com.loanlassalle.enigma.client.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loanlassalle.enigma.client.models.Player;
import com.loanlassalle.enigma.client.models.Room;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Hashtable;
import java.util.logging.Logger;

/**
 * The client is the backend of the communication with the server, it use the TCP protocol
 * The main purposes are to manage the communication with the server and to receive a list of rooms
 * and to connect in one of them.
 * It is a singleton because we want only one client by user
 *
 * @author Tano Iannetta
 */
public class Client {

    /**
     * Configuration manager to use the messages bundles
     */
    private final ConfigurationManager configurationManager;
    /**
     * Logger for logs information
     */
    private final Logger logger;

    /**
     * Connection's interface of server
     */
    private Socket socket;

    /**
     * Input stream to read the data sent by of server
     */
    private BufferedReader in;

    /**
     * Output stream to write the data to send to server
     */
    private PrintWriter out;

    /**
     * Player of the client, define the player with which we will interact in the game phase
     */
    private Player player;

    /**
     * Room of the client
     */
    private Room room;

    /**
     * Initialize one client
     */
    private Client() {
        configurationManager = ConfigurationManager.getInstance();
        logger = Logger.getLogger(Client.class.getName());
    }

    /**
     * Return the instance of the client
     *
     * @return instance of the client
     */
    public static Client getInstance() {
        return SingletonHolder.instance;
    }

    /**
     * Connect to the Server
     */
    public void connect() throws IOException {

        connect(configurationManager.getProperty("server.address"),
                configurationManager.getIntegerProperty("server.port"));
    }

    /**
     * Connection to targeted server and creation of input and output stream
     *
     * @param server String address of targeted server
     * @param port   int port number of targeted server
     * @throws IOException with Socket, BufferedReader and PrintWriter
     */
    private void connect(String server, int port) throws IOException {
        // Opens socket
        socket = new Socket(server, port);

        // Opens input and output streams
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(),
                StandardCharsets.UTF_8));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),
                StandardCharsets.UTF_8));
    }

    /**
     * Disconnection of targeted server
     *
     * @throws IOException with closing of BufferedReader, PrintWriter and Socket
     */
    public void disconnect() throws IOException {
        // Informs the server to disconnect the connection
        send(Protocol.DISCONNECT);

        // Closes streams and socket
        in.close();
        out.close();
        socket.close();

        // Displays a message of disconnection
        logger.info("Disconnected");
    }

    /**
     * Indicates if TCP client is connected
     *
     * @return True if TCP client is connected, false otherwies.
     */
    public boolean isConnected() {
        return socket != null && !socket.isClosed();
    }

    /**
     * Get back message of targeted server
     *
     * @return message received
     * @throws IOException with BufferedReader
     */
    public String receive() throws IOException {
        return in.readLine();
    }

    /**
     * Sends a message to targeted server
     *
     * @param message String message to send to targeted server
     * @throws IOException with PrintWriter
     */
    public void send(String message) throws IOException {
        out.println(message);
        out.flush();
    }

    /**
     * Get the list of the rooms
     *
     * @return list of rooms
     */
    public Hashtable<Integer, Room> getRooms() throws IOException {
        send(Protocol.ROOMS);
        String message = receive();
        if (message.equals(Protocol.ROOMS)) {
            return new ObjectMapper().readValue(receive(),
                    new TypeReference<Hashtable<Integer, Room>>() {
                    });
        } else {
            return null;
        }

    }

    /**
     * Connect to a selected room
     *
     * @param pseudo player trying to connect
     * @param noRoom room id to connect to
     * @return True if connection succeed, false otherwise
     */
    public boolean connectionToRoom(String pseudo, int noRoom) throws IOException {
        send(Protocol.CONNECT);

        send(String.valueOf(noRoom));
        send(pseudo);

        String m = receive();
        if (Protocol.CONNECT.equals(m)) {
            m = receive();
            if (Protocol.CONNECTED.equals(m)) {
                return true;
            }

        } else if (Protocol.SCOREBOARD.equals(m)) {
            receive();
        }
        return false;
    }

    /**
     * Get the player of the client
     *
     * @return the player of the client
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Set the player of the client
     *
     * @param player player to set
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Get the room of the client
     *
     * @return room of the client
     */
    public Room getRoom() {
        return room;
    }

    /**
     * Set the room of the client
     *
     * @param room to set
     */
    public void setRoom(Room room) {
        this.room = room;
    }

    /**
     * Send the message in the chat using our protocol
     *
     * @param text to send
     * @throws IOException send
     */
    public void sendToChat(String text) throws IOException {
        send(Protocol.CHAT);
        send(text);
    }

    /**
     * Create an instance of one client
     */
    private static class SingletonHolder {
        private final static Client instance = new Client();
    }
}