package model.entity.word;

public class PropertyType extends EffectType {
    public PropertyType(int zIndex, String typeId, String spritePath) {
        super(zIndex, typeId, spritePath, PartOfSpeech.PROPERTY);
    }
}
