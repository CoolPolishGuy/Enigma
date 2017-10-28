package com.loanlassalle.enigma.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.loanlassalle.enigma.server.controllers.ConfigurationManager;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Modélise un salon de jeu
 *
 * @author Tano Iannetta, Lassalle Loan, Jérémie Zanone
 * @since 13.04.2017
 */
public class Room implements Serializable {

    /**
     * Utilisé pour accéder aux fichiers de propriétés
     */
    private static final ConfigurationManager CONFIGURATION_MANAGER;

    /**
     * Utilisé pour définir un identifiant unique à chaqque salon
     */
    private static Integer lastIdRoom;

    /**
     * Utilisé pour contenir tous les salons créés
     */
    private static Hashtable<Integer, Room> roomHashtable;

    static {
        CONFIGURATION_MANAGER = ConfigurationManager.getInstance();
        lastIdRoom = 0;
        roomHashtable = new Hashtable<>();
        addEmptyRoom();
    }

    /**
     * Utilisé pour identifier un salon
     */
    private Integer idRoom;
    /**
     * Utilisé pour définit un nom à un salon
     */
    private String name;
    /**
     * Utilisé pour définir un jeu
     */
    private Game game;
    /**
     * Utilisé pour définir les joueurs d'un salon
     */
    private List<Player> playerList;

    /**
     * Utilisé pour le mapping JSON
     */
    public Room() {
    }

    private Room(String name) {
        this(++lastIdRoom, name, new Game());
    }

    private Room(Integer idRoom, String name, Game game) {
        this.idRoom = idRoom;
        this.name = name;
        this.game = game;
        playerList = new CopyOnWriteArrayList<>();
    }

    /**
     * Fournit le dernier identifiant créé pour un salon
     *
     * @return le dernier identifiant créé pour un salon
     */
    public static Integer getLastIdRoom() {
        return lastIdRoom;
    }

    /**
     * Fournit les salons créés
     *
     * @return les salons créés
     */
    public static Hashtable<Integer, Room> getRoomHashtable() {
        return roomHashtable;
    }

    /**
     * Fournit le salon correspondant à l'index de la HashMap
     *
     * @param index index de la HashMap
     * @return le salon correspondant à l'index de la HashMap
     */
    public static Room getRoomHashtable(Integer index) {
        return roomHashtable.get(index);
    }

    /**
     * Ajoute un salon vide si aucun salon vidde existe
     */
    public static void addEmptyRoom() {
        for (Room room : getRoomHashtable().values()) {
            if (room.isEmpty()) {
                return;
            }
        }

        roomHashtable.put(lastIdRoom + 1, new Room("vRoom_" + (lastIdRoom + 1)));
    }

    /**
     * Fournit l'identifiant du salon
     *
     * @return l'identifiant du salon
     */
    public Integer getIdRoom() {
        return idRoom;
    }

    /**
     * Fournit le nom du salon
     *
     * @return le nom du salon
     */
    public String getName() {
        return name;
    }

    /**
     * Définit le nom du salon
     *
     * @param name le nom du salon
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Fournit le jeu du salon
     *
     * @return le jeu du salon
     */
    public Game getGame() {
        return game;
    }

    /**
     * Définit un nouveau jeu pour le salon
     */
    public void setNewGame() {

        // Réinitialise le nombre d'essais, le score de chaque joueur et prépare les joueurs
        for (Player player : getPlayerList()) {
            player.resetAttempts();
            player.resetScore();
            player.setReady(true);
        }

        this.game = new Game();
    }

    /**
     * Fournit les joueurs du salon
     *
     * @return les joueurs du salon
     */
    public List<Player> getPlayerList() {
        return playerList;
    }

    /**
     * Fournit l'identifiant de l'énigme courante
     *
     * @return l'identifiant de l'énigme courante
     */
    @JsonIgnore
    public Integer getCurrentIdRiddle() {
        return game.getCurrentIdRiddle();
    }

    /**
     * Incrément l'identfiant de l'énigme en cours
     */
    public void incrementCurrentIdRiddle() {
        game.incrementCurrentIdRiddle();
    }

    /**
     * Indique si une énigme est en cours
     *
     * @return true si une énigme est en cours, false sinon
     */
    @JsonIgnore
    public boolean isRiddleUnderway() {
        return game.isRiddleUnderway();
    }

    /**
     * Définit si une énigme est en cours
     *
     * @param riddleUnderway indique si une énigme est en cours
     */
    public void setRiddleUnderway(boolean riddleUnderway) {
        game.setRiddleUnderway(riddleUnderway);
    }

    /**
     * Fournit les énigmes et leurs réponses
     *
     * @return les énigmes et leurs réponses
     */
    @JsonIgnore
    public HashMap<Integer, RiddleWrapper> getRiddleHashMap() {
        return game.getRiddleHashMap();
    }

    /**
     * Fournit une énigme et ses réponses correspondant à l'index de la HashMap
     *
     * @param index index de la Hashmap
     * @return une énigme et ses réponses correspondant à l'index de la HashMap
     */
    public RiddleWrapper getRiddleHashMap(Integer index) {
        return game.getRiddleHashMap(index);
    }

    /**
     * Indique si le salon est vide
     *
     * @return true si le salon est vide, false sinon
     */
    @JsonIgnore
    public boolean isEmpty() {
        return playerList.isEmpty();
    }

    /**
     * Indique si le salon est plein
     *
     * @return true si le salon est plein, false sinon
     */
    @JsonIgnore
    public boolean isFull() {
        return playerList.size() == CONFIGURATION_MANAGER.getIntegerProperty("game.playerMax");
    }

    /**
     * Indique si le salon est prêt pour démarrer un jeu
     *
     * @return true si le salon est prêt, false sinon
     */
    @JsonIgnore
    public boolean isReady() {
        return playerList.size() >= CONFIGURATION_MANAGER.getIntegerProperty("game.playerMin");
    }

    /**
     * Fournit le meilleur score des joueurs du salon
     *
     * @return meilleur score des joueurs du salon
     */
    @JsonIgnore
    public Integer getBestScore() {
        Integer score = 0;

        for (Player p : playerList) {
            if (p.getScore() > score) {
                score = p.getScore();
            }
        }

        return score;
    }

    /**
     * Réinitialise le nombre d'essais et prépare les joueurs
     */
    public void resetRound() {
        game.setFirstPlayer(true);

        for (Player player : getPlayerList()) {
            player.resetAttempts();
            player.setReady(true);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Room)) return false;

        Room room = (Room) o;

        return new EqualsBuilder()
                .append(idRoom, room.idRoom)
                .append(name, room.name)
                .append(game, room.game)
                .append(playerList, room.playerList)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(idRoom)
                .append(name)
                .append(game)
                .append(playerList)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "Room{" +
                "idRoom=" + idRoom +
                ", name='" + name + '\'' +
                ", game=" + game +
                ", playerList=" + playerList +
                '}';
    }
}
