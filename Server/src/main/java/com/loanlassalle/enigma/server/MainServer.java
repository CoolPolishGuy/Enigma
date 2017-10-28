package com.loanlassalle.enigma.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.loanlassalle.enigma.server.controllers.Server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Démarre le serveur utilisé pour le jeu Enigma
 *
 * @author Lassalle Loan, Jérémie Zanone
 * @since 13.04.2017
 */
public class MainServer {

    public static void main(String[] args) throws JsonProcessingException {

        // Démarre le serveur Enigma
        try {
            Server.getInstance().start();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
}
