package model.entity;

public class EntityType {

    private final String typeId;
    private final String spritePath;
    private final AnimationStyle animationStyle;
    private final int ZIndex;

    public EntityType(int ZIndex, String typeId, String spritePath, AnimationStyle animationStyle) {
        this.typeId = typeId;
        this.spritePath = spritePath;
        this.animationStyle = animationStyle;
        this.ZIndex = ZIndex;
    }

    public String getTypeId() {
        return typeId;
    }

    public String getSpritePath() {
        return spritePath;
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
