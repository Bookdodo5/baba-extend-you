package model.particle;

import javafx.scene.image.Image;
import utils.ImageUtils;

import java.util.Objects;

/**
 * Represents a type of particle effect in the game with its associated sprite.
 */
public enum ParticleType {
    PUFF("/particle/PUFF.png", 8, 100),
    WIN("/particle/WIN.png", 8, 100),
    CROSS("/particle/CROSS.png", 8, 100),
    HOT("/particle/HOT.png", 8, 100),
    DESTROY("/particle/DESTROY.png", 6, 25);

    private final Image spriteSheet;
    private final int frameCount;
    private final int frameDuration;

    ParticleType(String spritePath, int frameCount, int frameDuration) {
        this.spriteSheet = ImageUtils.getImage(spritePath);
        this.frameCount = frameCount;
        this.frameDuration = frameDuration;
    }

    public Image getSpriteSheet() {
        return spriteSheet;
    }
    public int getFrameCount() {
        return frameCount;
    }
    public int getFrameDuration() {
        return frameDuration;
    }
}
