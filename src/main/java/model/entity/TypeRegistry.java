package model.entity;

import model.entity.word.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * Central registry for all entity-related types used by the application.
 *
 * <p>This class declares and registers a fixed set of types using static initializers.
 * Each registered type receives a unique z-index and a default sprite path
 * derived from its type id in the format: {@code "sprites/{typeId}.png"}.
 *
 * <p>The z-index can be changed by changing the order of registration.
 *
 * <p>If you want to use the types, you can reference them directly like this:
 *
 * <pre>
 * EntityType constant = TypeRegistry.JAVA;
 * </pre>
 */
public class TypeRegistry {
    private static final Map<String, EntityType> REGISTRY = new HashMap<>();
    private static int ZIndexCounter = 0;

    private TypeRegistry() {}

    private static String getSpritePath(String typeId) {
        return "sprite/" + typeId.toUpperCase() + ".png";
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

    private static ConjunctionType registerConjunction(String typeId) {
        String spritePath = getSpritePath(typeId);
        ConjunctionType newType = new ConjunctionType(ZIndexCounter++, typeId, spritePath);
        REGISTRY.put(typeId, newType);
        return newType;
    }

    public static EntityType getType(String typeId) {
        return REGISTRY.get(typeId);
    }

    public static final EntityType JAVA = registerEntity("java", AnimationStyle.WOBBLE);
    public static final EntityType PYTHON = registerEntity("python", AnimationStyle.WOBBLE);
    public static final EntityType XORGATE = registerEntity("xorgate", AnimationStyle.DIRECTIONAL);
    public static final EntityType WIRE = registerEntity("wire", AnimationStyle.TILED);
    public static final EntityType LAVA = registerEntity("lava", AnimationStyle.TILED);
    public static final EntityType WATER = registerEntity("water", AnimationStyle.TILED);
    public static final EntityType FLAG = registerEntity("flag", AnimationStyle.WOBBLE);
    public static final EntityType PAPER = registerEntity("paper", AnimationStyle.WOBBLE);
    public static final EntityType TILE = registerEntity("tile", AnimationStyle.WOBBLE);
    public static final EntityType WARNING = registerEntity("warning", AnimationStyle.WOBBLE);
    public static final EntityType ERROR = registerEntity("error", AnimationStyle.WOBBLE);
    public static final EntityType CHECK = registerEntity("check", AnimationStyle.WOBBLE);
    public static final EntityType BUG = registerEntity("bug", AnimationStyle.DIRECTIONAL);
    public static final EntityType DATABASE = registerEntity("database", AnimationStyle.WOBBLE);
    public static final EntityType GIT = registerEntity("git", AnimationStyle.WOBBLE);
    public static final EntityType CHIP = registerEntity("chip", AnimationStyle.WOBBLE);

    public static final NounType TEXT_JAVA = registerNoun("text_java", JAVA);
    public static final NounType TEXT_PYTHON = registerNoun("text_python", PYTHON);
    public static final NounType TEXT_XORGATE = registerNoun("text_xorgate", XORGATE);
    public static final NounType TEXT_WIRE = registerNoun("text_wire", WIRE);
    public static final NounType TEXT_LAVA = registerNoun("text_lava", LAVA);
    public static final NounType TEXT_WATER = registerNoun("text_water", WATER);
    public static final NounType TEXT_FLAG = registerNoun("text_flag", FLAG);
    public static final NounType TEXT_PAPER = registerNoun("text_rock", PAPER);
    public static final NounType TEXT_TILE = registerNoun("text_tile", TILE);
    public static final NounType TEXT_WARNING = registerNoun("text_warning", WARNING);
    public static final NounType TEXT_ERROR = registerNoun("text_error", ERROR);
    public static final NounType TEXT_CHECK = registerNoun("text_check", CHECK);
    public static final NounType TEXT_BUG = registerNoun("text_bug", BUG);
    public static final NounType TEXT_DATABASE = registerNoun("text_database", DATABASE);
    public static final NounType TEXT_GIT = registerNoun("text_git", GIT);
    public static final NounType TEXT_CHIP = registerNoun("text_chip", CHIP);

    public static final VerbType IS = registerVerb("is", true, true);
    public static final VerbType HAS = registerVerb("has", true, false);
    public static final VerbType EXTEND = registerVerb("extend", true, false);

    public static final PropertyType YOU = registerProperty("you");
    public static final PropertyType WIN = registerProperty("win");
    public static final PropertyType DEFEAT = registerProperty("defeat");
    public static final PropertyType PUSH = registerProperty("push");
    public static final PropertyType STOP = registerProperty("stop");
    public static final PropertyType SINK = registerProperty("sink");
    public static final PropertyType HOT = registerProperty("hot");
    public static final PropertyType MELT = registerProperty("melt");
    public static final PropertyType MOVE = registerProperty("move");
    public static final PropertyType MORE = registerProperty("more");

    public static final ConditionType ON = registerCondition("on");
    public static final ConditionType NEAR = registerCondition("near");
    public static final ConditionType FACING = registerCondition("facing");
    public static final ConditionType INSTANCEOF = registerCondition("instanceof");

    public static final ConjunctionType AND = registerConjunction("and");
}
