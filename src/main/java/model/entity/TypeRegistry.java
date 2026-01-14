package model.entity;

import model.entity.word.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TypeRegistry {
    private static final Map<String, EntityType> VOCABULARY = new HashMap<>();
    private static int ZIndexCounter = 0;

    private static String getSpritePath(String typeId) {
        return "sprites/" + typeId + ".png";
    }

    private static EntityType registerEntity(String typeId, AnimationStyle animationStyle) {
        String spritePath = getSpritePath(typeId);
        EntityType newType = new EntityType(ZIndexCounter++, typeId, spritePath, animationStyle);
        VOCABULARY.put(typeId, newType);
        return newType;
    }

    private static NounType registerNoun(String typeId, EntityType referencedType) {
        String spritePath = getSpritePath(typeId);
        NounType newType = new NounType(ZIndexCounter++, typeId, spritePath, referencedType);
        VOCABULARY.put(typeId, newType);
        return newType;
    }

    @SuppressWarnings("unchecked")
    private static <T extends WordType> T registerWord(String typeId, PartOfSpeech partOfSpeech) {
        String spritePath = getSpritePath(typeId);
        int z = ZIndexCounter++;
        
        WordType newType = switch (partOfSpeech) {
            case VERB -> new VerbType(z, typeId, spritePath);
            case PROPERTY -> new PropertyType(z, typeId, spritePath);
            case CONDITION -> new ConditionType(z, typeId, spritePath);
            case NOUN -> throw new IllegalStateException("Use registerNoun for nouns.");
        };
        
        VOCABULARY.put(typeId, newType);
        return (T) newType;
    }

    public static EntityType getType(String typeId) {
        return VOCABULARY.get(typeId);
    }

    public static Collection<EntityType> getAllTypes() {
        return VOCABULARY.values();
    }

    public static final EntityType BABA = registerEntity("baba", AnimationStyle.CHARACTER);
    public static final EntityType KEKE = registerEntity("keke", AnimationStyle.CHARACTER);
    public static final EntityType WALL = registerEntity("wall", AnimationStyle.TILED);
    public static final EntityType LAVA = registerEntity("lava", AnimationStyle.TILED);
    public static final EntityType WATER = registerEntity("water", AnimationStyle.TILED);
    public static final EntityType CLOUD = registerEntity("cloud", AnimationStyle.TILED);
    public static final EntityType FLAG = registerEntity("flag", AnimationStyle.WOBBLE);
    public static final EntityType ROCK = registerEntity("rock", AnimationStyle.WOBBLE);
    public static final EntityType DOOR = registerEntity("door", AnimationStyle.WOBBLE);
    public static final EntityType KEY = registerEntity("key", AnimationStyle.WOBBLE);
    public static final EntityType TILE = registerEntity("tile", AnimationStyle.WOBBLE);
    public static final EntityType JAVA = registerEntity("java", AnimationStyle.WOBBLE);
    public static final EntityType CODE = registerEntity("code", AnimationStyle.WOBBLE);
    public static final EntityType FILE = registerEntity("file", AnimationStyle.WOBBLE);
    public static final EntityType DATABASE = registerEntity("database", AnimationStyle.WOBBLE);
    public static final EntityType GIT = registerEntity("git", AnimationStyle.WOBBLE);
    public static final EntityType CHIP = registerEntity("chip", AnimationStyle.WOBBLE);
    public static final EntityType SKULL = registerEntity("skull", AnimationStyle.DIRECTIONAL);
    public static final EntityType BLAHAJ = registerEntity("blahaj", AnimationStyle.DIRECTIONAL);

    public static final NounType TEXT_BABA = registerNoun("text_baba", BABA);
    public static final NounType TEXT_KEKE = registerNoun("text_keke", KEKE);
    public static final NounType TEXT_FLAG = registerNoun("text_flag", FLAG);
    public static final NounType TEXT_WALL = registerNoun("text_wall", WALL);
    public static final NounType TEXT_ROCK = registerNoun("text_rock", ROCK);
    public static final NounType TEXT_SKULL = registerNoun("text_skull", SKULL);
    public static final NounType TEXT_WATER = registerNoun("text_water", WATER);
    public static final NounType TEXT_DOOR = registerNoun("text_door", DOOR);
    public static final NounType TEXT_KEY = registerNoun("text_key", KEY);
    public static final NounType TEXT_TILE = registerNoun("text_tile", TILE);
    public static final NounType TEXT_LAVA = registerNoun("text_lava", LAVA);
    public static final NounType TEXT_BLAHAJ = registerNoun("text_blahaj", BLAHAJ);
    public static final NounType TEXT_JAVA = registerNoun("text_java", JAVA);
    public static final NounType TEXT_CODE = registerNoun("text_code", CODE);
    public static final NounType TEXT_FILE = registerNoun("text_file", FILE);
    public static final NounType TEXT_DATABASE = registerNoun("text_database", DATABASE);
    public static final NounType TEXT_GIT = registerNoun("text_git", GIT);
    public static final NounType TEXT_CLOUD = registerNoun("text_cloud", CLOUD);
    public static final NounType TEXT_CHIP = registerNoun("text_chip", CHIP);

    public static final VerbType IS = registerWord("text_is", PartOfSpeech.VERB);
    public static final VerbType HAS = registerWord("text_has", PartOfSpeech.VERB);
    public static final VerbType EXTEND = registerWord("text_extend", PartOfSpeech.VERB);

    public static final PropertyType YOU = registerWord("text_you", PartOfSpeech.PROPERTY);
    public static final PropertyType WIN = registerWord("text_win", PartOfSpeech.PROPERTY);
    public static final PropertyType DEFEAT = registerWord("text_defeat", PartOfSpeech.PROPERTY);
    public static final PropertyType PUSH = registerWord("text_push", PartOfSpeech.PROPERTY);
    public static final PropertyType STOP = registerWord("text_stop", PartOfSpeech.PROPERTY);
    public static final PropertyType SINK = registerWord("text_sink", PartOfSpeech.PROPERTY);
    public static final PropertyType HOT = registerWord("text_hot", PartOfSpeech.PROPERTY);
    public static final PropertyType MELT = registerWord("text_melt", PartOfSpeech.PROPERTY);
    public static final PropertyType OPEN = registerWord("text_open", PartOfSpeech.PROPERTY);
    public static final PropertyType SHUT = registerWord("text_shut", PartOfSpeech.PROPERTY);
    public static final PropertyType MOVE = registerWord("text_move", PartOfSpeech.PROPERTY);
    public static final PropertyType PRIVATE = registerWord("text_private", PartOfSpeech.PROPERTY);
    public static final PropertyType STATIC = registerWord("text_static", PartOfSpeech.PROPERTY);
    public static final PropertyType FINAL = registerWord("text_final", PartOfSpeech.PROPERTY);
    public static final PropertyType ABSTRACT = registerWord("text_abstract", PartOfSpeech.PROPERTY);

    public static final ConditionType ON = registerWord("text_on", PartOfSpeech.CONDITION);
    public static final ConditionType AND = registerWord("text_and", PartOfSpeech.CONDITION);
    public static final ConditionType NEAR = registerWord("text_near", PartOfSpeech.CONDITION);
}
