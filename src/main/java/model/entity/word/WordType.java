package model.entity.word;

import model.entity.AnimationStyle;
import model.entity.EntityType;

/**
 * Abstract class representing an entity type which can be used to form rules.
 */
public abstract class WordType extends EntityType {
    private final PartOfSpeech partOfSpeech;

    public WordType(int zIndex, String typeId, PartOfSpeech partOfSpeech) {
        super(zIndex, typeId, AnimationStyle.WOBBLE);
        this.partOfSpeech = partOfSpeech;
    }

    /**
     * Returns {@code true} because all word types represent text tiles.
     *
     * @return {@code true}
     */
    @Override
    public boolean isText() {
        return true;
    }

    /**
     * Returns the part of speech for this word type (NOUN, VERB, PROPERTY, etc.).
     *
     * @return the part of speech
     */
    public PartOfSpeech getPartOfSpeech() {
        return partOfSpeech;
    }
}
