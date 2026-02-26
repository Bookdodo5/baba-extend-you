package model.entity.word;

/**
 * Represents a verb word type in the game.
 * Verbs can accept nouns or properties as an effect, and only nouns as a subject. (IS, HAS, EXTEND)
 */
public class VerbType extends WordType{

    private final boolean acceptsNoun;
    private final boolean acceptsProperty;

    public VerbType(int zIndex, String typeId, boolean acceptsNoun, boolean acceptsProperty) {
        super(zIndex, typeId, PartOfSpeech.VERB);
        this.acceptsNoun = acceptsNoun;
        this.acceptsProperty = acceptsProperty;
    }

    /**
     * Returns whether this verb can take a noun as its effect.
     *
     * @return {@code true} if the verb accepts a noun effect
     */
    public boolean acceptsNoun() {
        return acceptsNoun;
    }

    /**
     * Returns whether this verb can take a property as its effect.
     *
     * @return {@code true} if the verb accepts a property effect
     */
    public boolean acceptsProperty() {
        return acceptsProperty;
    }
}
