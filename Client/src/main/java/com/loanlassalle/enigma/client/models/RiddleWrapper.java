package com.loanlassalle.enigma.client.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * Modélise un wrapper d'une énigme
 *
 * @author Tano Iannetta, Lassalle Loan, Jérémie Zanone
 * @since 13.04.2017
 */
public class RiddleWrapper implements Serializable {

    /**
     * Utilisé pour contenir la question de l'énigme
     */
    @JsonIgnore
    private String question;

    /**
     * Utilisé pour contenir les réponses à l'énigme
     */
    @JsonIgnore
    private List<String> answers;

    /**
     * Utilisé pour le mapping JSON
     */
    public RiddleWrapper() {
    }

    RiddleWrapper(String question, List<String> answers) {
        this.question = question;
        this.answers = answers;
    }

    /**
     * Fournit la question de l'énigme
     *
     * @return la question de l'énigme
     */
    public String getQuestion() {
        return question;
    }

    /**
     * Définit la question de l'énigme
     *
     * @param question la question de l'énigme
     */
    public void setQuestion(String question) {
        this.question = question;
    }

    /**
     * Fournit les réponses à l'énigme
     *
     * @return les réponses à l'énigme
     */
    public List<String> getAnswers() {
        return answers;
    }

    /**
     * Définit les réponses à l'énigme
     *
     * @param answers les réponses à l'énigme
     */
    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    /**
     * Fournit la réponse à l'énigme correspondant à l'index
     *
     * @param index index de la réponse à l'énigme
     * @return la réponse à l'énigme correspondant à l'index
     */
    public String getAnswers(Integer index) {
        return answers.get(index);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof RiddleWrapper)) return false;

        RiddleWrapper riddleWrapper = (RiddleWrapper) o;

        return new EqualsBuilder()
                .append(question, riddleWrapper.question)
                .append(answers, riddleWrapper.answers)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(question)
                .append(answers)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "RiddleWrapper{" +
                "question='" + question + '\'' +
                ", answers=" + answers +
                '}';
    }
}
