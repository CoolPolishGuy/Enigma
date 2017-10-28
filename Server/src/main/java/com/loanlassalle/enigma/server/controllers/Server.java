package com.loanlassalle.enigma.server.controllers;

import com.loanlassalle.enigma.server.controllers.client.ClientWorker;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gère toute requêtes des clients du jeu Enigma
 *
 * @author Lassalle Loan, Jérémie Zanone
 * @since 13.04.2017
 */
public class Server {

    /**
     * Utilisé pour accéder aux fichiers de propriétés
     */
    private final ConfigurationManager configurationManager;

    /**
     * Utilisé pour journaliser les actions effectuées
     */
    private final Logger logger;

    /**
     * Utilisé pour réceptionner les clients
     */
    private ServerSocket serverSocket;

    /**
     * Utilisé pour connaître le port d'écoute du serveur
     */
    private Integer listenPort;

    /**
     * Utilisé pour stopper l'exécution du serveur
     */
    private boolean shouldRun;

    private Server() {
        configurationManager = ConfigurationManager.getInstance();
        logger = Logger.getLogger(Server.class.getName());

        listenPort = Integer.valueOf(configurationManager.getProperty("server.port"));
    }

    /**
     * Fournit l'unique instance de la classe (singleton)
     *
     * @return unique instance de la classe
     */
    public static Server getInstance() {
        return SingletonHolder.instance;
    }

    /**
     * Fournit l'interface réseau utilisée par le serveur
     *
     * @return l'interface réseau utilisée par le serveur
     */
    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    /**
     * Indique si le serveur est en cours d'exécution
     *
     * @return true si le serveur est en cours d'exécution, false sinon
     */
    public boolean isShouldRun() {
        return shouldRun;
    }

    /**
     * Définit l'état d'exécution du serveur
     *
     * @param shouldRun état d'exécution du serveur
     */
    public void setShouldRun(boolean shouldRun) {
        this.shouldRun = shouldRun;
    }

    /**
     * Démarre le serveur et réceptionne les client
     *
     * @throws IOException si la création de l'interface réseau a échoué
     */
    public void start() throws IOException {

        // Initialise la connexion avec l'interface réseau
        if (serverSocket == null || !serverSocket.isBound()) {
            try {
                bindOnKnownPort(listenPort);
            } catch (IOException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
                throw e;
            }
        }

        // Création d'un nouveau thread à chaque connexion d'un client
        new Thread(() -> {
            shouldRun = true;

            while (shouldRun) {
                logger.info(configurationManager.getString("server.numberClients",
                        ClientWorker.getClientWorkerList().size()));

                logger.info(configurationManager.getString("server.listeningClientConnection",
                        serverSocket.getLocalSocketAddress()));

                try {
                    Socket clientSocket = serverSocket.accept();
                    logger.info(configurationManager.getString("server.clientArrived"));
                    logger.info(configurationManager.getString("server.delegatingWork"));

                    // Création d'un réceptionniste pour le client
                    ClientWorker clientWorker = new ClientWorker(clientSocket);
                    new Thread(clientWorker).start();
                } catch (IOException e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    shouldRun = false;
                }
            }
        }).start();
    }

    /**
     * Etablissement de la connexion avec l'interface réseau
     *
     * @param port port d'écoute du serveur
     * @throws IOException si la connexion avec l'interface réseau a échoué
     */
    private void bindOnKnownPort(int port) throws IOException {
        serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(port));
    }

    /**
     * Ferme l'interface réseau du serveur
     *
     * @throws IOException si la fermeture de l'interface réseau a échoué
     */
    private void close() throws IOException {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
                throw e;
            }
        }
    }

    /**
     * Stop l'exécution du serveur et le notifie à tous les clients
     *
     * @throws IOException si la fermeture de l'interface réseau a échoué
     */
    public void stop() throws IOException {
        shouldRun = false;
        logger.info(configurationManager.getString("server.cleaningUpResources"));

        // Notifie tous les clients de se déconnecter
        for (ClientWorker clientWorker : ClientWorker.getClientWorkerList()) {
            clientWorker.disconnect();
        }

        close();
    }

    /**
     * Utilisé pour créer un singleton de la classe
     */
    private static class SingletonHolder {
        private final static Server instance = new Server();
    }
}
