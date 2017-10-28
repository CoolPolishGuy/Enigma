package com.loanlassalle.enigma.manager.models;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * Modélise une énigme de la base de données
 *
 * @author Lassalle Loan, Jérémie Zanone
 * @since 13.04.2017
 */
public class Riddle implements Serializable {

    private Integer idRiddle;
    private String question;
    private String answer1;
    private String answer2;
    private String answer3;

    public Riddle() {
    }

    public Riddle(Integer idRiddle, String question, String answer1, String answer2, String answer3) {
        this.idRiddle = idRiddle;
        this.question = question;
        this.answer1 = answer1;
        this.answer2 = answer2;
        this.answer3 = answer3;
    }

    public Integer getIdRiddle() {
        return idRiddle;
    }

    public void setIdRiddle(Integer idRiddle) {
        this.idRiddle = idRiddle;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer1() {
        return answer1;
    }

    public void setAnswer1(String answer1) {
        this.answer1 = answer1;
    }

    public String getAnswer2() {
        return answer2;
    }

    public void setAnswer2(String answer2) {
        this.answer2 = answer2;
    }

    public String getAnswer3() {
        return answer3;
    }

    public void setAnswer3(String answer3) {
        this.answer3 = answer3;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Riddle)) return false;

        Riddle riddle = (Riddle) o;

        return new EqualsBuilder()
                .append(idRiddle, riddle.idRiddle)
                .append(question, riddle.question)
                .append(answer1, riddle.answer1)
                .append(answer2, riddle.answer2)
                .append(answer3, riddle.answer3)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(idRiddle)
                .append(question)
                .append(answer1)
                .append(answer2)
                .append(answer3)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "RiddleWrapper{" +
                "idRiddle=" + idRiddle +
                ", question='" + question + '\'' +
                ", answer1='" + answer1 + '\'' +
                ", answer2='" + answer2 + '\'' +
                ", answer3='" + answer3 + '\'' +
                '}';
    }
}
