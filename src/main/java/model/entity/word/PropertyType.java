package model.entity.word;

public class PropertyType extends WordType{
    public PropertyType(int zIndex, String typeId, String spritePath) {
        super(zIndex, typeId, spritePath, PartOfSpeech.PROPERTY);
    }
}
