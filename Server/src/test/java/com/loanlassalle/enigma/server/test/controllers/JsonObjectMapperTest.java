package com.loanlassalle.enigma.server.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loanlassalle.enigma.server.models.Player;
import com.loanlassalle.enigma.server.models.Room;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test de la classe JsonObjectMapper
 *
 * @author Lassalle Loan, Jérémie Zanone
 * @since 25.03.2017
 */
class JsonObjectMapperTest {

    private final JsonObjectMapper jsonObjectMapper = JsonObjectMapper.getInstance();

    /**
     * Test si la transformation du JSON d'un salon est correct
     */
    @Test
    void parseJson() throws IOException {
        new ObjectMapper().readTree(jsonObjectMapper.toJson(new Room()));
    }

    /**
     * Test si la transformation d'un joueur en format JSON est correct
     */
    @Test
    void toJson() throws IOException {
        assertNotNull(jsonObjectMapper.parseJson(jsonObjectMapper
                .toJson(new Player("Loan")), Player.class));
    }
}