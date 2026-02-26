package model.action;

import model.entity.Entity;
import model.map.LevelMap;
import model.particle.Particle;
import model.particle.ParticleType;
import state.PlayingState;
import utils.ImageUtils;

/**
 * An action that destroys an entity from the level map.
 */
public class DestroyAction implements Action {

    private final LevelMap levelMap;
    private final Entity entity;
    private final int posX;
    private final int posY;

    /**
     * Destroy the given entity, removing it from the levelMap.
     * @param levelMap
     * @param entity
     */
    public DestroyAction(LevelMap levelMap, Entity entity) {
        this.levelMap = levelMap;
        this.entity = entity;
        this.posX = levelMap.getX(entity);
        this.posY = levelMap.getY(entity);
    }

    /**
     * Returns the entity that this action will destroy.
     *
     * @return the entity targeted for destruction
     */
    public Entity getEntity() {
        return entity;
    }

    @Override
    public void execute() {
        levelMap.removeEntity(entity);
    }

    @Override
    public void undo() {
        levelMap.setPosition(entity, posX, posY);
    }

    /**
     * Spawns destruction particle effects at the entity's last position.
     *
     * @param playingState the playing state to add particles to
     */
    public void addParticle(PlayingState playingState) {
        for (int i = 0; i < 4; i++) {
            playingState.addParticle(new Particle(
                    posX + (Math.random() - 0.5) / 4.0,
                    posY + (Math.random() - 0.5) / 4.0,
                    (Math.random() - 0.5) / 1000.0,
                    (Math.random() - 0.5) / 1000.0,
                    ParticleType.DESTROY,
                    ImageUtils.averageColor(entity.getType().getSpriteSheet())
            ));
        }
    }
}