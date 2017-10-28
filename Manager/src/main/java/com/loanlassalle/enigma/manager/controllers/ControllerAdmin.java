package com.loanlassalle.enigma.manager.controllers;

import com.loanlassalle.enigma.manager.controllers.database.DatabaseAccess;
import com.loanlassalle.enigma.manager.models.Riddle;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;

/**
 * Cette classe gère l'ajout, la modification et la suppression des énigmes.
 *
 * @author Tano Iannetta, Loan Lassalle et Wojciech Myszkorowski
 * @since 13.04.2017
 */
public class ControllerAdmin {

    public Button closeButton;
    public Button deleteButton;
    public Button validateButton;
    public TextArea questionField;
    public TextField answer1Field;
    public TextField answer2Field;
    public TextField answer3Field;
    public Label totalRiddle;
    public ComboBox riddleCombobox;
    private List<Riddle> riddleList;

    @FXML
    private void closeButtonAction() {
        // get a handle to the stage
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    public void initialize() {
        //ajout da liste graphique dans la combobox
        refreshRiddleList();
    }

    /**
     * Affiche les enigmes dans le menu déroulant
     */
    public void displayRiddle() {
        int indexSelected = riddleCombobox.getSelectionModel().getSelectedIndex();

        if (indexSelected == 0) {
            clearFields();
        } else if (checkRiddleList(indexSelected - 1)) {
            setRiddleFields(indexSelected - 1);
        }
    }

    /**
     * permet de valider l'ajout ou la modification d'une énigme.
     */
    public void validateRiddle() {
        if (checkFileds()) {
            int indexSelected = riddleCombobox.getSelectionModel().getSelectedIndex();

            if (indexSelected == 0) {
                addRiddle();
            } else if (checkRiddleList(indexSelected - 1)) {
                modifyRiddle(indexSelected - 1);
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("modification base de données");
            alert.setHeaderText("modification d'une énigme");
            alert.setContentText("la liste à été modifiés");
            alert.showAndWait();

            refreshRiddleList();
        } else {
            System.out.println("ici");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Validation incorrect");
            alert.setHeaderText("saisie incorrect");
            alert.setContentText("veuillez resssayer");
            alert.showAndWait();
        }
    }

    /**
     * supprime une énigme de la base de données ainsi que de la liste déroulante
     */
    public void deleteRiddle() {
        int indexSelected = riddleCombobox.getSelectionModel().getSelectedIndex() - 1;

        if (checkRiddleList(indexSelected)) {
            DatabaseAccess.getInstance()
                    .delete(Riddle.class, riddleList.get(indexSelected).getIdRiddle());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("modification base de données");
            alert.setHeaderText("Suppression d'une énigme");
            alert.setContentText("la liste contient une égnime en moins");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur suppression");
            alert.setHeaderText("saisie incorrect");
            alert.setContentText("veuillez resssayer");
            alert.showAndWait();
        }

        refreshRiddleList();
    }

    /**
     * permet de remplir les champs text avec l'énigme choisi dans le menu déroulant
     *
     * @param index
     */
    private void setRiddleFields(int index) {
        Riddle riddle = riddleList.get(index);

        questionField.setText(riddle.getQuestion());
        answer1Field.setText(riddle.getAnswer1());
        answer2Field.setText(riddle.getAnswer2() == null ? "" : riddle.getAnswer2());
        answer3Field.setText(riddle.getAnswer3() == null ? "" : riddle.getAnswer3());
    }

    /**
     * ajoute une énigme dans la base de données ainsi que dans a la liste déroulantes
     */
    private void addRiddle() {
        DatabaseAccess.getInstance().save(
                new Riddle(0,
                        questionField.getText(),
                        answer1Field.getText(),
                        answer2Field.getText(),
                        answer3Field.getText()));
    }

    /**
     * Modifie l'enigmes choisi grâce à l'index en allant recuperer les informations dans la liste
     * d'enigmes
     *
     * @param index index de l'énigme choisi
     */
    private void modifyRiddle(int index) {
        Riddle riddle = riddleList.get(index);

        riddle.setQuestion(questionField.getText());
        riddle.setAnswer1(answer1Field.getText());
        riddle.setAnswer2(answer2Field.getText());
        riddle.setAnswer3(answer3Field.getText());

        DatabaseAccess.getInstance().update(riddle);
    }

    /**
     * Retourne vrai si les champs à remplir respecte les conditions.
     *
     * @return vrai si les champs remplis respectent les règles
     */
    private boolean checkFileds() {
        if (questionField.getText().length() >= 90
                || answer1Field.getText().length() >= 30
                || answer2Field.getText().length() >= 30
                || answer1Field.getText().length() >= 30) {
            return false;
        }
        if (questionField.getText() == null && answer1Field.getText() != null
                || questionField.getText() != null && answer1Field.getText() == null) {
            return false;
        }
        String notAllowed = "+*ç%&/()'{}[]<>";
        String textQuestion = questionField.getText();
        String answer1 = answer1Field.getText();

        return !(textQuestion.isEmpty()
                || answer1.isEmpty()
                || textQuestion.contains(notAllowed)
                || answer1.contains(notAllowed)
                || answer2Field.getText().contains(notAllowed)
                || answer3Field.getText().contains(notAllowed));
    }

    /**
     * Vide les champs textes
     */
    private void clearFields() {
        questionField.setText("");
        answer1Field.setText("");
        answer2Field.setText("");
        answer3Field.setText("");
    }

    /**
     * Permet de rafraichir la liste des énigmes dans le menu déroulant
     */
    private void refreshRiddleList() {
        clearFields();

        riddleCombobox.getItems().clear();
        riddleCombobox.getItems().add(0, "Ajouter énigme");
        riddleCombobox.getSelectionModel().select(0);

        riddleList = DatabaseAccess.getInstance().get(Riddle.class);
        int riddleListSize = riddleList.size();
        totalRiddle.setText(String.valueOf(riddleListSize));

        // MISE EN PLACE DE LA LISTE DEROULANTE
        for (int i = 1; i <= riddleListSize; i++) {
            riddleCombobox.getItems().add(i, "enigme " + i);
        }
    }

    /**
     * Verfie que l'index selectionner n'est pas null et qu'il est comrpis entre la taille de la liste et 0
     *
     * @param indexSelected index seléctionné
     * @return si l'index est séléctionné est correct
     */
    private boolean checkRiddleList(int indexSelected) {
        return riddleList != null && indexSelected >= 0 && riddleList.size() >= indexSelected;
    }
}