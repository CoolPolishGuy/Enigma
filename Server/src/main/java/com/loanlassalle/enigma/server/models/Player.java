package com.loanlassalle.enigma.server.models;

import com.loanlassalle.enigma.server.controllers.ConfigurationManager;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * Modélise un joueur
 *
 * @author Tano Iannetta, Lassalle Loan, Wojciech Myszkorowski, Jérémie Zanone
 * @since 13.04.2017
 */
public class Player implements Serializable {

    /**
     * Utilisé pour accéder aux fichiers de propriétés
     */
    private static final ConfigurationManager CONFIGURATION_MANAGER;

    static {
        CONFIGURATION_MANAGER = ConfigurationManager.getInstance();
    }

    /**
     * Utilisé pour déifnir le nom du joueur
     */
    private String name;

    /**
     * Utilisé pour définit le nombre d'essais de réponses
     */
    private Integer attempts;

    /**
     * Utilisé pour définir le score du joueur
     */
    private Integer score;

    /**
     * Utilisé pour indiquer si le joueur est prêt à jouer
     */
    private boolean ready;

    /**
     * Utilisé pour le mapping JSON
     */
    public Player() {
    }

    public Player(String name) {
        this(name,
                CONFIGURATION_MANAGER.getIntegerProperty("player.attempsMax"),
                0,
                false);
    }

    private Player(String name, Integer attempts, Integer score, boolean ready) {
        this.name = name;
        this.attempts = attempts;
        this.score = score;
        this.ready = ready;
    }

    /**
     * Fournit le nom du joueur
     *
     * @return le nom du joueur
     */
    public String getName() {
        return name;
    }

    /**
     * Définit le nom du joueur
     *
     * @param name le nom du joueur
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Fournit le nombre d'essais de réponses
     *
     * @return le nombre d'essais de réponses
     */
    public Integer getAttempts() {
        return attempts;
    }

    /**
     * Réinitialise le nombre d'éssais de réponse
     */
    void resetAttempts() {
        attempts = Integer.valueOf(CONFIGURATION_MANAGER.getProperty("player.attempsMax"));
    }

    /**
     * Décrémente le nombre d'essais de réponses
     *
     * @param decrement valeur du décrement
     */
    public void decrementAttempts(Integer decrement) {
        if (attempts - decrement >= 0) {
            attempts -= decrement;
        }
    }

    /**
     * Fournit le score du joueur
     *
     * @return le score du joueur
     */
    public Integer getScore() {
        return score;
    }

    /**
     * Réinitialise le score du joueur
     */
    void resetScore() {
        score = 0;
    }

    /**
     * Incrémente le score du joueur
     *
     * @param increment valeur de l'incrément
     */
    public void incrementScore(Integer increment) {
        if (score + increment >= 0) {
            score += increment;
        }
    }

    /**
     * Indique si le joueur est prêt à jouer
     *
     * @return true si le joueur est prêt à jouer, false sinon
     */
    public boolean isReady() {
        return ready;
    }

    /**
     * Définit l'état du joueur pour jouer
     *
     * @param ready état du joueur pour jouer
     */
    public void setReady(boolean ready) {
        this.ready = ready;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Player)) return false;

        Player player = (Player) o;

        return new EqualsBuilder()
                .append(name, player.name)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", attempts=" + attempts +
                ", score=" + score +
                '}';
    }
}
