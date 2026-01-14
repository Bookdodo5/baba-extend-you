package model.entity.word;

import model.entity.AnimationStyle;
import model.entity.EntityType;

public abstract class WordType extends EntityType {
    private final PartOfSpeech partOfSpeech;

    public WordType(int zIndex, String typeId, String spritePath, PartOfSpeech partOfSpeech) {
        super(zIndex, typeId, spritePath, AnimationStyle.WOBBLE);
        this.partOfSpeech = partOfSpeech;
    }

    @Override
    public boolean isText() {
        return true;
    }

    public PartOfSpeech getPartOfSpeech() {
        return partOfSpeech;
    }
}
