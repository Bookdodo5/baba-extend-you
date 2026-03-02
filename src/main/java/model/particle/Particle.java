package model.particle;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import utils.ImageUtils;

/**
 * Represents a visual particle effect with position, velocity, type, and color.
 */
public class Particle {
    private double x;
    private double y;
    private final double originalX;
    private final double originalY;
    private final double vx;
    private final double vy;
    private final ParticleType type;
    private final long createdTime;
    private final Image coloredImage;

    /**
     * Creates a new particle at the specified position with the given velocity, type, and color.
     *
     * @param x     the initial x-coordinate (in grid units)
     * @param y     the initial y-coordinate (in grid units)
     * @param vx    the x velocity (grid units per millisecond)
     * @param vy    the y velocity (grid units per millisecond)
     * @param type  the particle type defining the sprite and lifetime
     * @param color the tint color applied to the sprite
     */
    public Particle(double x, double y, double vx, double vy, ParticleType type, Color color) {
        this.originalX = x;
        this.originalY = y;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.type = type;
        this.createdTime = System.currentTimeMillis();
        this.coloredImage = ImageUtils.applyColor(type.getSpriteSheet(), color);
    }

    /**
     * Returns the current x-coordinate of this particle.
     *
     * @return the x position
     */
    public double getX() {
        return x;
    }

    /**
     * Returns the current y-coordinate of this particle.
     *
     * @return the y position
     */
    public double getY() {
        return y;
    }

    /** Updates this particle's position based on elapsed time since creation. */
    public void update() {
        long age = System.currentTimeMillis() - createdTime;
        x = originalX + vx * age;
        y = originalY + vy * age;
    }

    /**
     * Returns the tinted image used to render this particle.
     *
     * @return the colored particle image
     */
    public Image getImage() {
        return coloredImage;
    }

    /**
     * Returns whether this particle has exceeded its lifetime and should be removed.
     *
     * @return {@code true} if the particle is expired
     */
    public boolean isDead() {
        long age = System.currentTimeMillis() - createdTime;
        return age >= (long) type.getFrameCount() * type.getFrameDuration();
    }

    /**
     * Returns the current animation frame index based on elapsed time.
     *
     * @return the current frame index
     */
    public int getCurrentFrame() {
        long age = System.currentTimeMillis() - createdTime;
        return (int) (age / type.getFrameDuration());
    }
}
