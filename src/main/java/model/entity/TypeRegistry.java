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

    private static EntityType registerEntity(String typeId, AnimationStyle animationStyle) {
        EntityType newType = new EntityType(ZIndexCounter++, typeId, animationStyle);
        REGISTRY.put(typeId, newType);
        return newType;
    }

    private static NounType registerNoun(String typeId, EntityType referencedType) {
        NounType newType = new NounType(ZIndexCounter++, typeId, referencedType);
        REGISTRY.put(typeId, newType);
        return newType;
    }

    private static VerbType registerVerb(String typeId, boolean acceptsNoun, boolean acceptsProperty) {
        VerbType newType = new VerbType(ZIndexCounter++, typeId, acceptsNoun, acceptsProperty);
        REGISTRY.put(typeId, newType);
        return newType;
    }

    private static ConditionType registerCondition(String typeId) {
        ConditionType newType = new ConditionType(ZIndexCounter++, typeId);
        REGISTRY.put(typeId, newType);
        return newType;
    }

    private static PropertyType registerProperty(String typeId) {
        PropertyType newType = new PropertyType(ZIndexCounter++, typeId);
        REGISTRY.put(typeId, newType);
        return newType;
    }

    private static ConjunctionType registerConjunction(String typeId) {
        ConjunctionType newType = new ConjunctionType(ZIndexCounter++, typeId);
        REGISTRY.put(typeId, newType);
        return newType;
    }

    /**
     * Returns the registered {@link EntityType} for the given type ID, or {@code null} if not found.
     *
     * @param typeId the string identifier of the type
     * @return the corresponding entity type, or {@code null}
     */
    public static EntityType getType(String typeId) {
        return REGISTRY.get(typeId);
    }

    public static final EntityType LAVA = registerEntity("lava", AnimationStyle.TILED);
    public static final EntityType WATER = registerEntity("water", AnimationStyle.TILED);
    public static final EntityType WIRE = registerEntity("wire", AnimationStyle.TILED);
    public static final EntityType TILE = registerEntity("tile", AnimationStyle.WOBBLE);
    public static final EntityType DOCUMENT = registerEntity("document", AnimationStyle.WOBBLE);
    public static final EntityType ERROR = registerEntity("error", AnimationStyle.WOBBLE);
    public static final EntityType CHECK = registerEntity("check", AnimationStyle.WOBBLE);
    public static final EntityType WARNING = registerEntity("warning", AnimationStyle.WOBBLE);
    public static final EntityType RECORD = registerEntity("record", AnimationStyle.WOBBLE);
    public static final EntityType GIT = registerEntity("git", AnimationStyle.WOBBLE);
    public static final EntityType DATABASE = registerEntity("database", AnimationStyle.WOBBLE);
    public static final EntityType CHIP = registerEntity("chip", AnimationStyle.WOBBLE);
    public static final EntityType BUG = registerEntity("bug", AnimationStyle.DIRECTIONAL);
    public static final EntityType XORGATE = registerEntity("xorgate", AnimationStyle.DIRECTIONAL);
    public static final EntityType PYTHON = registerEntity("python", AnimationStyle.WOBBLE);
    public static final EntityType FLAG = registerEntity("flag", AnimationStyle.WOBBLE);
    public static final EntityType JAVA = registerEntity("java", AnimationStyle.WOBBLE);

    public static final NounType TEXT_JAVA = registerNoun("text_java", JAVA);
    public static final NounType TEXT_PYTHON = registerNoun("text_python", PYTHON);
    public static final NounType TEXT_XORGATE = registerNoun("text_xorgate", XORGATE);
    public static final NounType TEXT_WIRE = registerNoun("text_wire", WIRE);
    public static final NounType TEXT_LAVA = registerNoun("text_lava", LAVA);
    public static final NounType TEXT_WATER = registerNoun("text_water", WATER);
    public static final NounType TEXT_FLAG = registerNoun("text_flag", FLAG);
    public static final NounType TEXT_DOCUMENT = registerNoun("text_document", DOCUMENT);
    public static final NounType TEXT_TILE = registerNoun("text_tile", TILE);
    public static final NounType TEXT_WARNING = registerNoun("text_warning", WARNING);
    public static final NounType TEXT_RECORD = registerNoun("text_record", RECORD);
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
