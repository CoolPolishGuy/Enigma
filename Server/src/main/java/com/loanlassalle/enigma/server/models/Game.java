package com.loanlassalle.enigma.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.loanlassalle.enigma.server.controllers.ConfigurationManager;
import com.loanlassalle.enigma.server.controllers.database.DatabaseAccess;

import java.io.Serializable;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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

    /**
     * Utilisé pour récupérer les énigmes et leurs réponses
     */
    @JsonIgnore
    private HashMap<Integer, RiddleWrapper> riddleHashMap;

    @JsonIgnore
    private boolean isFirstPlayer;

    public Game() {
        this(0, false, new HashMap<>());
        putRiddles();
    }

    private Game(Integer currentIdRiddle,
                 boolean riddleUnderway,
                 HashMap<Integer, RiddleWrapper> riddleHashMap) {
        this.currentIdRiddle = currentIdRiddle;
        this.riddleUnderway = riddleUnderway;
        this.riddleHashMap = riddleHashMap;
        isFirstPlayer = true;
    }

    public static boolean correctAnswer(List<String> answerArray, String answerToValidate) {
        if (answerToValidate.length() > 0
                && answerToValidate.length() <
                CONFIGURATION_MANAGER.getIntegerProperty("game.answerMaxLength")) {

            answerToValidate = Normalizer.normalize(answerToValidate.toLowerCase(), Normalizer.Form.NFD)
                    .replaceAll("[^\\p{ASCII}]", "");

            String[] wordsToAnalyse = answerToValidate.split("\\s+");

            for (String answer : answerArray) {
                if (answer != null) {
                    answer = Normalizer.normalize(answer.toLowerCase(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]",
                            "");

                    // si réponse exacte
                    if (answer.equals(answerToValidate)) {
                        return true;
                    }

                    String[] wordsValid = answer.split("\\s+");

                    if (wordsToAnalyse.length == 1 && wordsValid.length < 3) {
                        String[] sortedArray = bubblesort(wordsToAnalyse);
                        String[] wordsOK = answer.split("\\s+");
                        wordsOK = bubblesort(wordsOK);
                        if (answer.equals(answerToValidate) || answer.contains(answerToValidate) && wordsOK[0].length() == sortedArray[0].length()) {

                            return true;
                        }
                    }

                    if (wordsToAnalyse.length == 2 && wordsValid.length < 4) {
                        String[] sortedArray = bubblesort(wordsToAnalyse);
                        String[] wordsOK = answer.split("\\s+");
                        wordsOK = bubblesort(wordsOK);

                        // si le mot est précédé par un déterminant
                        if (sortedArray[0].length() > sortedArray[1].length() && answer.contains(sortedArray[0])
                                && (sortedArray[0].equals("le") || sortedArray[1].equals("la")
                                || sortedArray[1].equals("les") || sortedArray[1].equals("des")
                                || sortedArray[1].equals("une") || sortedArray[1].equals("un"))) {

                            return true;
                        } // sinon doit contenir les deux mots les plus longs
                        else if (wordsOK[0].equals(sortedArray[0]) && wordsOK[1].equals(sortedArray[1])) {
                            return true;

                        }
                    }

                    if (wordsToAnalyse.length == 3 && wordsValid.length < 5) {
                        String[] sortedArray = bubblesort(wordsToAnalyse);

                        if (sortedArray[0].length() > sortedArray[1].length() && answer.contains(sortedArray[0])
                                && answer.contains(sortedArray[1])) {

                            return true;
                        }
                    }

                    if (wordsToAnalyse.length == 4 && wordsValid.length < 6) {
                        String[] sortedArray = bubblesort(wordsToAnalyse);

                        if (sortedArray[0].length() > sortedArray[1].length() && answer.contains(sortedArray[0])
                                && answer.contains(sortedArray[1]) && answer.contains(sortedArray[2])) {

                            return true;
                        }
                    }

                    if (wordsToAnalyse.length > 4 && wordsValid.length >= 6) {
                        String[] sortedArray = bubblesort(wordsToAnalyse);

                        if (answer.contains(sortedArray[0]) && answer.contains(sortedArray[1])
                                && answer.contains(sortedArray[2]) && answer.contains(sortedArray[4])) {

                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private static String[] bubblesort(String[] array) {
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

    /**
     * Insert les énigmes et leurs réponses dans la HashMap
     */
    private void putRiddles() {
        List<Riddle> data = DatabaseAccess.getInstance().get(Riddle.class);

        Collections.shuffle(data);
        riddleHashMap.clear();

        for (int i = 0; i < CONFIGURATION_MANAGER.getIntegerProperty("game.questionMax"); ++i) {
            List<String> answers = new ArrayList<>();
            answers.add(data.get(i).getAnswer1());
            answers.add(data.get(i).getAnswer2());
            answers.add(data.get(i).getAnswer3());

            riddleHashMap.put(i, new RiddleWrapper(data.get(i).getQuestion(), answers));
        }
    }
}
