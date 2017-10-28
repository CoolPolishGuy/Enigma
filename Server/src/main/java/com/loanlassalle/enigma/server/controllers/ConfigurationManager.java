package com.loanlassalle.enigma.server.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.LogManager;

/**
 * Gère le chargement et l'accès à des propriétés contenues dans des fichiers
 *
 * @author Lassalle Loan, Jérémie Zanone
 * @since 13.04.2017
 */
public class ConfigurationManager {

    /**
     * Fichier de propriétés utilisé pour définir les paramètres du programme
     */
    private final String configurationPropertiesFile =
            "com/loanlassalle/enigma/server/resources/config.properties";

    /**
     * Nom de base des fichiers utilisés par le resourceBundle
     */
    private final String messageBundlePropertiesFile =
            "com/loanlassalle/enigma/server/resources/messagesBundles/messageBundle";

    /**
     * Fichier de propriétés utilisé pour définir les paramètres du système de log
     */
    private final String loggingPropertiesFile =
            "com/loanlassalle/enigma/server/resources/logging.properties";

    /**
     * Dossier de destination pour les fichiers de log
     */
    private final String loggingFileDestination =
            System.getProperty("user.home") + File.separator
                    + "Enigma" + File.separator
                    + "Logs Server" + File.separator;

    /**
     * Utilisé pour obtenir des propriétés contenues dans un fichier
     */
    private final Properties properties;

    /**
     * Utilisé pour obtenir des messages dans la langue correspondante au système courant
     */
    private final ResourceBundle resourceBundle;

    private ConfigurationManager() {
        properties = new Properties();
        resourceBundle = ResourceBundle.getBundle(messageBundlePropertiesFile, Locale.getDefault());

        // Création de l'arborescence de dossier pour contenir les logs
        File file = new File(loggingFileDestination);
        file.mkdirs();

        try {
            // Récupère les propriétés d'un fichier
            properties.load(ConfigurationManager.class.getClassLoader()
                    .getResourceAsStream(configurationPropertiesFile));

            // Définit les propriétés du logger
            LogManager.getLogManager().readConfiguration(getClass().getClassLoader()
                    .getResourceAsStream(loggingPropertiesFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fournit l'unique instance de la classe (singleton)
     *
     * @return unique instance de la classe
     */
    public static ConfigurationManager getInstance() {
        return SingletonHolder.instance;
    }

    /**
     * Founrit le properties gérant des propriétés d'un fichier
     *
     * @return properties gérant des propriétés d'un fichier
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * Founir la valeur d'une propriété contenue dans un fichier
     *
     * @param key clé de la propriété contenue dans le fichier de la proprities
     * @return chaîne de caractères correspondant à la clé de la propriété contenue dans le
     * fichier de la proprities
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Founir la valeur d'une propriété contenue dans un fichier
     *
     * @param key clé de la propriété contenue dans le fichier de la proprities
     * @return chaîne de caractères correspondant à la clé de la propriété contenue dans le
     * fichier de la proprities
     */
    public Integer getIntegerProperty(String key) {
        return Integer.valueOf(getProperty(key));
    }

    /**
     * Fournit le resourceBundle gérant l'affichage de messages dans la langue correspondante
     * au système courant
     *
     * @return resourceBundle gérant l'affichage de messages dans la langue correspondante
     * au système courant
     */
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    /**
     * Fournit la chaîne de caractères correspondant à la clé de la propriété contenue dans le
     * fichier du resourceBundle formaté avec les autres paramètres
     *
     * @param key  clé de la propriété contenue dans le fichier du resourceBundle
     * @param args autres paramètres à formater avec la chaîne de caractères récupérée
     * @return chaîne de caractères correspondant à la clé de la propriété contenue dans le
     * fichier du resourceBundle formaté avec les autres paramètres
     */
    public String getString(String key, Object... args) {
        return String.format(getString(key), args);
    }

    /**
     * Fournit la chaîne de caractères correspondant à la clé de la propriété contenue dans le
     * fichier du resourceBundle
     *
     * @param key clé de la propriété contenue dans le fichier du resourceBundle
     * @return chaîne de caractères correspondant à la clé de la propriété contenue dans le
     * fichier du resourceBundle
     */
    private String getString(String key) {

        // Récupère la chaîne de caractères et l'encode au format UTF-8
        return new String(resourceBundle.getString(key).getBytes(
                StandardCharsets.ISO_8859_1),
                StandardCharsets.UTF_8);
    }

    /**
     * Utilisé pour créer un singleton de la classe
     */
    private static class SingletonHolder {
        private final static ConfigurationManager instance = new ConfigurationManager();
    }
}
