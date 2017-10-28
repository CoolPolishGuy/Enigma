package com.loanlassalle.enigma.server.controllers.database;

import com.loanlassalle.enigma.server.models.Riddle;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test de la classe DatabaseAccess
 *
 * @author Lassalle Loan, Jérémie Zanone
 * @since 25.03.2017
 */
class DatabaseAccessTest {

    private final DatabaseAccess databaseAccess = DatabaseAccess.getInstance();

    /**
     * Test si l'on récupère l'énigme avec le même ID
     */
    @Test
    void get() {
        Riddle riddle = databaseAccess.get(Riddle.class, 1);

        assertNotNull(riddle);
        assertEquals((Integer) 1, riddle.getIdRiddle());
    }

    /**
     * Test si l'énigme récupérée possède une question et une réponse non null
     */
    @Test
    void get1() {
        Riddle riddle = databaseAccess.get(Riddle.class, 1);

        assertNotNull(riddle);
        assertNotNull(riddle.getQuestion());
        assertNotNull(riddle.getAnswer1());
    }

    /**
     * Test si l'on récupère toutes les énigmes
     */
    @Test
    void get2() {

        // Récupère toutes les énigmes
        List<Riddle> riddleList = databaseAccess.get(Riddle.class);

        assertNotNull(riddleList);
        assertNotEquals(0, riddleList.size());

        // Récupère la dernière énigme
        Riddle riddle = databaseAccess.get(Riddle.class, riddleList.size());

        assertNotNull(riddle);

        // Récupère l'énigme après la dernière énigme (non existante)
        riddle = databaseAccess.get(Riddle.class, riddleList.size() + 1);
        assertNull(riddle);
    }
}