package com.loanlassalle.enigma.client.models;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

/**
 * This class associate a player with a label and a image to display in the game frame
 *
 * @author Tano Iannetta
 */
public class InterfacePlayer {
    private Player player;
    private Label label;
    private ImageView imageView;

    /**
     * Associate a player with a label and an image view
     *
     * @param player    the player
     * @param label     a label to display the name of the player
     * @param imageView a image to display the state of the player
     */
    public InterfacePlayer(Player player, Label label, ImageView imageView) {
        this.imageView = imageView;
        this.label = label;
        this.player = player;
        label.setText(this.player.getName());
    }

    /**
     * return the player
     *
     * @return player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * set the player
     *
     * @param player player
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * return the label
     *
     * @return label
     */
    public Label getLabel() {
        return label;
    }

    /**
     * set label
     *
     * @param label label to set
     */
    public void setLabel(Label label) {
        this.label = label;
    }

    /**
     * return image view associated
     *
     * @return image view
     */
    public ImageView getImageView() {
        return imageView;
    }

    /**
     * set an image view
     *
     * @param imageView image view to set
     */
    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

}
