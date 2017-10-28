package com.loanlassalle.enigma.client.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Permet d'effectuer le mapping entre un objet et sa réprésentation JSON
 *
 * @author Lassalle Loan, Jérémie Zanone
 * @since 13.04.2017
 */
public class JsonObjectMapper {

    /**
     * Utilisé pour effectuer le mapping entre un objet et sa réprésentation JSON
     */
    private final ObjectMapper objectMapper;

    private JsonObjectMapper() {
        objectMapper = new ObjectMapper();
    }

    /**
     * Fournit l'unique instance de la classe (singleton)
     *
     * @return unique instance de la classe
     */
    public static JsonObjectMapper getInstance() {
        return SingletonHolder.instance;
    }

    /**
     * Fournit l'objet de la représentation JSON
     *
     * @param json   réprésentation JSON
     * @param tClass classe de l'objet à créer
     * @param <T>    type de l'objet à créer
     * @return l'objet de la représentation JSON
     * @throws IOException si la création de l'objet a échoué
     */
    public <T> T parseJson(String json, Class<T> tClass) throws IOException {
        return objectMapper.readValue(json, tClass);
    }

    /**
     * Fournit la représentation JSON d'un objet
     *
     * @param o objet dont il faut créer la représentation JSON
     * @return la représentation JSON d'un objet
     * @throws JsonProcessingException si la réprésentation JSON a échoué
     */
    public String toJson(Object o) throws JsonProcessingException {
        return objectMapper.writeValueAsString(o);
    }

    /**
     * Utilisé pour créer un singleton de la classe
     */
    private static class SingletonHolder {
        private final static JsonObjectMapper instance = new JsonObjectMapper();
    }
}
