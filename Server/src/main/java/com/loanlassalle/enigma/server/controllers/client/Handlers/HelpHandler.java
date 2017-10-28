package com.loanlassalle.enigma.server.controllers.client.Handlers;

import com.loanlassalle.enigma.server.controllers.ConfigurationManager;
import com.loanlassalle.enigma.server.controllers.Protocol;
import com.loanlassalle.enigma.server.controllers.client.ClientHandler;

import java.util.Arrays;

/**
 * Gère la requête Protocol.HELP
 *
 * @author Lassalle Loan, Jérémie Zanone
 * @since 13.04.2017
 */
public class HelpHandler implements IHandler {

    /**
     * Utilisé pour accéder aux fichiers de propriétés
     */
    private final ConfigurationManager configurationManager;

    /**
     * Utilisé pour réceptionner le client
     */
    private ClientHandler clientHandler;

    private HelpHandler() {
        configurationManager = ConfigurationManager.getInstance();
    }

    /**
     * Fournit l'unique instance de la classe (singleton)
     *
     * @return unique instance de la classe
     */
    public static HelpHandler getInstance(ClientHandler clientHandler) {
        SingletonHolder.instance.clientHandler = clientHandler;

        return SingletonHolder.instance;
    }

    /**
     * Envoie toutes les commandes disponibles
     */
    public void handle() {
        String result;

        result = configurationManager.getString(
                "clienHandler.commandsSupported",
                Arrays.toString(Protocol.SUPPORTED_COMMANDS));

        clientHandler.sendLine(Protocol.HELP);
        clientHandler.sendLine(result);
    }

    /**
     * Utilisé pour créer un singleton de la classe
     */
    private static class SingletonHolder {
        private final static HelpHandler instance = new HelpHandler();
    }
}
