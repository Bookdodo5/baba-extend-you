package model.entity;

import javafx.scene.image.Image;
import utils.ImageUtils;

import java.util.Objects;

/**
 * Represents a type of entity (ex. TEXT_JAVA, JAVA object, TEXT_IS) in the game with its associated properties.
 */
public class EntityType {

    private final String typeId;
    private final AnimationStyle animationStyle;
    private final Image spriteSheet;
    private final int ZIndex;

    /**
     * Creates an entity type with the given z-index, ID, and animation style.
     * The sprite sheet is loaded automatically based on the type ID.
     *
     * @param ZIndex         the rendering depth order
     * @param typeId         the unique string identifier (e.g. "java", "text_java")
     * @param animationStyle the animation style for this type
     */
    public EntityType(int ZIndex, String typeId, AnimationStyle animationStyle) {
        this.typeId = typeId;
        this.animationStyle = animationStyle;
        this.ZIndex = ZIndex;
        this.spriteSheet = ImageUtils.getImage(getSpritePath(typeId));
    }

    private static String getSpritePath(String typeId) {
        return "/sprite/" + typeId.toUpperCase() + ".png";
    }

    /**
     * Returns the unique string identifier of this type.
     *
     * @return the type ID
     */
    public String getTypeId() {
        return typeId;
    }

    /**
     * Returns the sprite sheet image used for rendering this entity type.
     *
     * @return the sprite sheet
     */
    public Image getSpriteSheet() {
        return spriteSheet;
    }

    /**
     * Returns the animation style used by this entity type.
     *
     * @return the animation style
     */
    public AnimationStyle getAnimationStyle() {
        return animationStyle;
    }

    /**
     * Returns the z-index used for rendering order (higher = rendered on top).
     *
     * @return the z-index
     */
    public int getZIndex() {
        return ZIndex;
    }

    /**
     * Returns whether this entity type represents a text/word tile.
     *
     * @return {@code true} if this type is a word tile
     */
    public boolean isText() {
        return false;
    }
}
