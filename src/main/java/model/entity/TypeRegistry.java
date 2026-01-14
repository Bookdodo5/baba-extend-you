package model.entity;

import model.entity.word.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TypeRegistry {
    private static final Map<String, EntityType> REGISTRY = new HashMap<>();
    private static int ZIndexCounter = 0;

    private TypeRegistry() {}

    private static String getSpritePath(String typeId) {
        return "sprites/" + typeId + ".png";
    }

    private static EntityType registerEntity(String typeId, AnimationStyle animationStyle) {
        String spritePath = getSpritePath(typeId);
        EntityType newType = new EntityType(ZIndexCounter++, typeId, spritePath, animationStyle);
        REGISTRY.put(typeId, newType);
        return newType;
    }

    private static NounType registerNoun(String typeId, EntityType referencedType) {
        String spritePath = getSpritePath(typeId);
        NounType newType = new NounType(ZIndexCounter++, typeId, spritePath, referencedType);
        REGISTRY.put(typeId, newType);
        return newType;
    }

    private static VerbType registerVerb(String typeId, boolean acceptsNoun, boolean acceptsProperty) {
        String spritePath = getSpritePath(typeId);
        VerbType newType = new VerbType(ZIndexCounter++, typeId, spritePath, acceptsNoun, acceptsProperty);
        REGISTRY.put(typeId, newType);
        return newType;
    }

    private static ConditionType registerCondition(String typeId) {
        String spritePath = getSpritePath(typeId);
        ConditionType newType = new ConditionType(ZIndexCounter++, typeId, spritePath);
        REGISTRY.put(typeId, newType);
        return newType;
    }

    private static PropertyType registerProperty(String typeId) {
        String spritePath = getSpritePath(typeId);
        PropertyType newType = new PropertyType(ZIndexCounter++, typeId, spritePath);
        REGISTRY.put(typeId, newType);
        return newType;
    }

    public static EntityType getType(String typeId) {
        return REGISTRY.get(typeId);
    }

    public static Collection<EntityType> getAllTypes() {
        return REGISTRY.values();
    }

    public static final EntityType JAVA = registerEntity("java", AnimationStyle.CHARACTER);
    public static final EntityType PYTHON = registerEntity("python", AnimationStyle.CHARACTER);
    public static final EntityType WALL = registerEntity("wall", AnimationStyle.TILED);
    public static final EntityType LAVA = registerEntity("lava", AnimationStyle.TILED);
    public static final EntityType WATER = registerEntity("water", AnimationStyle.TILED);
    public static final EntityType FLAG = registerEntity("flag", AnimationStyle.WOBBLE);
    public static final EntityType ROCK = registerEntity("rock", AnimationStyle.WOBBLE);
    public static final EntityType DOOR = registerEntity("door", AnimationStyle.WOBBLE);
    public static final EntityType KEY = registerEntity("key", AnimationStyle.WOBBLE);
    public static final EntityType TILE = registerEntity("tile", AnimationStyle.WOBBLE);
    public static final EntityType WARNING = registerEntity("warning", AnimationStyle.WOBBLE);
    public static final EntityType ERROR = registerEntity("error", AnimationStyle.WOBBLE);
    public static final EntityType CODE = registerEntity("code", AnimationStyle.WOBBLE);
    public static final EntityType FILE = registerEntity("file", AnimationStyle.WOBBLE);
    public static final EntityType DATABASE = registerEntity("database", AnimationStyle.WOBBLE);
    public static final EntityType GIT = registerEntity("git", AnimationStyle.WOBBLE);
    public static final EntityType CHIP = registerEntity("chip", AnimationStyle.WOBBLE);

    public static final NounType TEXT_JAVA = registerNoun("text_baba", JAVA);
    public static final NounType TEXT_PYTHON = registerNoun("text_keke", PYTHON);
    public static final NounType TEXT_WALL = registerNoun("text_wall", WALL);
    public static final NounType TEXT_LAVA = registerNoun("text_lava", LAVA);
    public static final NounType TEXT_WATER = registerNoun("text_water", WATER);
    public static final NounType TEXT_FLAG = registerNoun("text_flag", FLAG);
    public static final NounType TEXT_ROCK = registerNoun("text_rock", ROCK);
    public static final NounType TEXT_DOOR = registerNoun("text_door", DOOR);
    public static final NounType TEXT_KEY = registerNoun("text_key", KEY);
    public static final NounType TEXT_TILE = registerNoun("text_tile", TILE);
    public static final NounType TEXT_WARNING = registerNoun("text_java", WARNING);
    public static final NounType TEXT_ERROR = registerNoun("text_java", ERROR);
    public static final NounType TEXT_CODE = registerNoun("text_code", CODE);
    public static final NounType TEXT_FILE = registerNoun("text_file", FILE);
    public static final NounType TEXT_DATABASE = registerNoun("text_database", DATABASE);
    public static final NounType TEXT_GIT = registerNoun("text_git", GIT);
    public static final NounType TEXT_CHIP = registerNoun("text_chip", CHIP);

    public static final VerbType IS = registerVerb("text_is", true, true);
    public static final VerbType HAS = registerVerb("text_has", true, false);
    public static final VerbType EXTEND = registerVerb("text_extend", true, false);

    public static final PropertyType YOU = registerProperty("text_you");
    public static final PropertyType WIN = registerProperty("text_win");
    public static final PropertyType DEFEAT = registerProperty("text_defeat");
    public static final PropertyType PUSH = registerProperty("text_push");
    public static final PropertyType STOP = registerProperty("text_stop");
    public static final PropertyType SINK = registerProperty("text_sink");
    public static final PropertyType HOT = registerProperty("text_hot");
    public static final PropertyType MELT = registerProperty("text_melt");
    public static final PropertyType OPEN = registerProperty("text_open");
    public static final PropertyType SHUT = registerProperty("text_shut");
    public static final PropertyType MOVE = registerProperty("text_move");
    public static final PropertyType PRIVATE = registerProperty("text_private");
    public static final PropertyType STATIC = registerProperty("text_static");
    public static final PropertyType FINAL = registerProperty("text_final");
    public static final PropertyType ABSTRACT = registerProperty("text_abstract");

    public static final ConditionType ON = registerCondition("text_on");
    public static final ConditionType AND = registerCondition("text_and");
    public static final ConditionType NEAR = registerCondition("text_near");
    public static final ConditionType FACING = registerCondition("text_facing");
}
