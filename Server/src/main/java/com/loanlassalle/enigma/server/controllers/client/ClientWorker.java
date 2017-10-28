package com.loanlassalle.enigma.server.controllers.client;

import com.loanlassalle.enigma.server.controllers.ConfigurationManager;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gère un client du serveur du jeu Enigma
 *
 * @author Lassalle Loan, Jérémie Zanone
 * @since 13.04.2017
 */
public class ClientWorker implements Runnable {

    /**
     * Utilisé pour accéder aux fichiers de propriétés
     */
    private static final ConfigurationManager CONFIGURATION_MANAGER;

    /**
     * Utilisé pour journaliser les actions effectuées
     */
    private static final Logger LOGGER;

    /**
     * Utilisé pour conserver une liste des client connectés au serveur
     */
    private static final List<ClientWorker> CLIENT_WORKER_LIST;

    static {
        CONFIGURATION_MANAGER = ConfigurationManager.getInstance();
        LOGGER = Logger.getLogger(ClientWorker.class.getName());
        CLIENT_WORKER_LIST = new CopyOnWriteArrayList<>();
    }

    /**
     * Utilisé pour récupérer les flux d'entrées/sorties
     */
    private Socket clientSocket;

    /**
     * Utilisé pour récupérer des informations en entrées
     */
    private InputStream inputStream;

    /**
     * Utilisé pour transmettre des informations en sorties
     */
    private OutputStream outputStream;

    /**
     * Utilisé pour gérer les requêtes du client
     */
    private ClientHandler clientHandler;

    public ClientWorker(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        inputStream = clientSocket.getInputStream();
        outputStream = clientSocket.getOutputStream();
        clientHandler = new ClientHandler(this);
    }

    /**
     * Fournit la liste des clients connectés au serveur
     *
     * @return la liste des clients connectés au serveur
     */
    public static List<ClientWorker> getClientWorkerList() {
        return CLIENT_WORKER_LIST;
    }

    /**
     * Fournit l'interface réseau du client
     *
     * @return l'interface réseau du client
     */
    public Socket getClientSocket() {
        return clientSocket;
    }

    /**
     * Définit l'interface réseau du client
     *
     * @param clientSocket l'interface réseau du client
     */
    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    /**
     * Fournit le flux d'entrées correspondant à l'interface réseau du client
     *
     * @return le flux d'entrées correspondant à l'interface réseau du client
     */
    InputStream getInputStream() {
        return inputStream;
    }

    /**
     * Fournit le flux de sorties correspondant à l'interface réseau du client
     *
     * @return le flux de sorties correspondant à l'interface réseau du client
     */
    OutputStream getOutputStream() {
        return outputStream;
    }

    /**
     * Fournit le gestionnaire des requêtes du client
     *
     * @return le gestionnaire des requêtes du client
     */
    ClientHandler getClientHandler() {
        return clientHandler;
    }

    /**
     * Indique si le client est connecté
     *
     * @return true si le client est connecté, false sinon
     */
    public boolean isConnected() {
        return clientSocket != null && !clientSocket.isClosed();
    }

    /**
     * Démarre le thread pour récupérer les requêtes du client
     */
    @Override
    public void run() {
        try {
            clientHandler.handle();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            try {
                disconnect();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    /**
     * Ferme la connexion avec le client
     *
     * @throws IOException si la fermeture de la connexion a échoué
     */
    public void disconnect() throws IOException {
        LOGGER.info(CONFIGURATION_MANAGER.getString("server.cleaningUpResources"));

        for (ClientWorker clientWorker : CLIENT_WORKER_LIST) {
            if (clientWorker == this) {
                CLIENT_WORKER_LIST.remove(clientWorker);
                break;
            }
        }

        // Ferme les flux d'entrées/sorties et l'interface réseau
        close(inputStream);
        close(outputStream);
        close(clientSocket);
    }

    /**
     * Ferme le closeable
     *
     * @throws IOException si la fermeture du closeable a échoué
     */
    private void close(Closeable closeable) throws IOException {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
                throw e;
            }
        }
    }
}
