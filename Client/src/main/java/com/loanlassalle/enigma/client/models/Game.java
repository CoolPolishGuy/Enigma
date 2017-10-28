package com.loanlassalle.enigma.client.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.loanlassalle.enigma.client.controllers.ConfigurationManager;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Modélise un jeu
 *
 * @author Tano Iannetta, Lassalle Loan, Wojciech Myszkorowski, Jérémie Zanone
 * @since 13.04.2017
 */
public class Game implements Serializable {

    /**
     * Utilisé pour accéder aux fichiers de propriétés
     */
    private static final ConfigurationManager CONFIGURATION_MANAGER;

    static {
        CONFIGURATION_MANAGER = ConfigurationManager.getInstance();
    }

    /**
     * Utilisé pour identifier l'énigme en cours
     */
    private Integer currentIdRiddle;

    /**
     * Utilisé pour indiquer si une énigme est en cours
     */
    private boolean riddleUnderway;

    @JsonIgnore
    private boolean isFirstPlayer;

    /**
     * Utilisé pour récupérer les énigmes et leurs réponses
     */
    @JsonIgnore
    private HashMap<Integer, RiddleWrapper> riddleHashMap;

    public Game() {
        this(0, false, new HashMap<>());
    }

    private Game(Integer currentIdRiddle,
                 boolean riddleUnderway,
                 HashMap<Integer, RiddleWrapper> riddleHashMap) {
        this.currentIdRiddle = currentIdRiddle;
        this.riddleUnderway = riddleUnderway;
        this.riddleHashMap = riddleHashMap;
        isFirstPlayer = true;
    }

    /**
     * Fournit l'identifiant de l'énigme courante
     *
     * @return l'identifiant de l'énigme courante
     */
    public Integer getCurrentIdRiddle() {
        return currentIdRiddle;
    }

    /**
     * Incrémente l'identifiant de l'énigme courante
     */
    void incrementCurrentIdRiddle() {
        ++currentIdRiddle;
    }

    /**
     * Indique si une énigme est en cours
     *
     * @return true si une énigme est en cours, false sinon
     */
    public boolean isRiddleUnderway() {
        return riddleUnderway;
    }

    /**
     * Définit si une énigme est en cours
     *
     * @param riddleUnderway indique si une énigme est en cours
     */
    void setRiddleUnderway(boolean riddleUnderway) {
        this.riddleUnderway = riddleUnderway;
    }

    /**
     * Fournit les énigmes et leurs réponses
     *
     * @return les énigmes et leurs réponses
     */
    @JsonIgnore
    HashMap<Integer, RiddleWrapper> getRiddleHashMap() {
        return riddleHashMap;
    }

    /**
     * Fournit une énigme et ses réponses correspondant à l'index de la HashMap
     *
     * @param index index de la Hashmap
     * @return une énigme et ses réponses correspondant à l'index de la HashMap
     */
    RiddleWrapper getRiddleHashMap(Integer index) {
        return riddleHashMap.get(index);
    }

    public boolean isFirstPlayer() {
        return isFirstPlayer;
    }

    public void setFirstPlayer(boolean firstPlayer) {
        isFirstPlayer = firstPlayer;
    }

    @Override
    public String toString() {
        return "Game{" +
                "currentIdRiddle=" + currentIdRiddle +
                ", riddleUnderway=" + riddleUnderway +
                ", riddleHashMap=" + riddleHashMap +
                '}';
    }

    public boolean correctAnswer(String answerArray[], String answerToValidate) {
        if (answerToValidate.length() > 0
                && answerToValidate.length() <
                CONFIGURATION_MANAGER.getIntegerProperty("game.answerMaxLength")) {

            answerToValidate = answerToValidate.toLowerCase();
            String[] wordsToAnalyse = answerToValidate.split("\\s+");

            for (int i = 0; i < wordsToAnalyse.length; i++) {
                wordsToAnalyse[i] = wordsToAnalyse[i].replaceAll("[^\\w]", "");
            }

            for (String answer : answerArray) {
                String[] wordsValid = answer.split("\\s+");

                for (int i = 0; i < wordsValid.length; i++) {
                    wordsValid[i] = wordsValid[i].replaceAll("[^\\w]", "");
                }

                if (wordsToAnalyse.length == 1 && wordsValid.length < 3) {
                    if (answer.equals(answerToValidate) || answer.contains(answerToValidate)) {
                        return true;
                    }
                }

                if (wordsToAnalyse.length == 2 && wordsValid.length < 4) {
                    String[] sortedArray = bubblesort(wordsToAnalyse);

                    if (sortedArray[0].length() > sortedArray[1].length()
                            && answer.contains(sortedArray[0])) {
                        return true;
                    }
                }

                if (wordsToAnalyse.length == 3 && wordsValid.length < 5) {
                    String[] sortedArray = bubblesort(wordsToAnalyse);

                    if (sortedArray[0].length() > sortedArray[1].length()
                            && answer.contains(sortedArray[0])
                            && answer.contains(sortedArray[1])) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private String[] bubblesort(String[] array) {
        boolean flag = true;
        String temp;

        while (flag) {
            flag = false;
            for (int j = 0; j < array.length - 1; ++j) {
                if (array[j].length() < array[j + 1].length()) {
                    temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                    flag = true;
                }
            }
        }

        return array;
    }
}
