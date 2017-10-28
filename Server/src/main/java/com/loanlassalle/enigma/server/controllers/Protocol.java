package com.loanlassalle.enigma.server.controllers;

/**
 * Définit les standards de communications entre un serveur et un client du jeu Enigma
 *
 * @author Tano Iannetta, Lassalle Loan, Jérémie Zanone
 * @since 13.04.2017
 */
public class Protocol {

    /**
     * Utilisé pour les échanges entre le serveur et le client
     */
    public static final String HELP = "/help";
    public static final String ROOMS = "/rooms";
    public static final String CONNECT = "/connect";
    public static final String CHAT = "/chat";
    public static final String RIDDLE = "/riddle";
    public static final String ANSWER = "/answer";
    public static final String ATTEMPT = "/attempt";
    public static final String ROUND = "/round";
    public static final String GAME = "/game";
    public static final String SCOREBOARD = "/scoreboard";
    public static final String DISCONNECT = "/disconnect";

    /**
     * Utilisé pour répondre au client
     */
    public static final String CONNECTED = "/connected";
    public static final String NOT_CONNECTED = "/notConnected";
    public static final String DISCONNECTED = "/disconnected";
    public static final String NOT_DISCONNECTED = "/notDisconnected";
    public static final String SENT = "/sent";
    public static final String NOT_SENT = "/noSent";
    public static final String DELIVERED = "/delivered";
    public static final String NOT_DELIVERED = "/notDelivered";
    public static final String RIDDLE_READY = "/getReady";
    public static final String RIDDLE_UNDERWAY = "/riddleUnderway";
    public static final String END_ROUND = "/endRound";
    public static final String END_GAME = "/endGame";
    public static final String CORRECT = "/correct";
    public static final String INCORRECT = "/incorrect";
    public static final String ERROR = "/error";

    /**
     * Utilisé pour fournir les commandes disponibles
     */
    public static final String[] SUPPORTED_COMMANDS =
            new String[]{HELP, ROOMS, CONNECT, CHAT, RIDDLE, ANSWER, DISCONNECT};
}
