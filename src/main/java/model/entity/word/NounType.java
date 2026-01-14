package model.entity.word;

import model.entity.EntityType;

public class NounType extends WordType {
    EntityType referencedType;

    public NounType(int zIndex, String typeId, String spritePath, EntityType referencedType) {
        super(zIndex, typeId, spritePath, PartOfSpeech.NOUN);
        this.referencedType = referencedType;
    }

    public EntityType getReferencedType() {
        return referencedType;
    }
}
