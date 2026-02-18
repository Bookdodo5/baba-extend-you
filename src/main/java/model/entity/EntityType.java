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

    public EntityType(int ZIndex, String typeId, AnimationStyle animationStyle) {
        this.typeId = typeId;
        this.animationStyle = animationStyle;
        this.ZIndex = ZIndex;
        this.spriteSheet = ImageUtils.getImage(getSpritePath(typeId));
    }

    private static String getSpritePath(String typeId) {
        return "/sprite/" + typeId.toUpperCase() + ".png";
    }

    public String getTypeId() {
        return typeId;
    }

    public Image getSpriteSheet() {
        return spriteSheet;
    }

    public AnimationStyle getAnimationStyle() {
        return animationStyle;
    }

    public int getZIndex() {
        return ZIndex;
    }

    public boolean isText() {
        return false;
    }
}
