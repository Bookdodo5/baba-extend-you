package model.particle;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import utils.ImageUtils;

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

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void update() {
        long age = System.currentTimeMillis() - createdTime;
        x = originalX + vx * age;
        y = originalY + vy * age;
    }

    public Image getImage() {
        return coloredImage;
    }

    public boolean isDead() {
        long age = System.currentTimeMillis() - createdTime;
        return age >= (long) type.getFrameCount() * type.getFrameDuration();
    }

    public int getCurrentFrame() {
        long age = System.currentTimeMillis() - createdTime;
        return (int) (age / type.getFrameDuration());
    }
}
