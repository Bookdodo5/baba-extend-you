package model.map;

import model.entity.Entity;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Represents a 2D grid-based level map containing entities.
 */
public class LevelMap {
    private final int width;
    private final int height;
    private final Map<Point, List<Entity>> grid;
    private final Map<Entity, Point> entityPositions;

    /**
     * Creates an empty level map with the given dimensions.
     *
     * @param width  the number of columns
     * @param height the number of rows
     */
    public LevelMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new HashMap<>();
        this.entityPositions = new HashMap<>();
    }

    /**
     * Creates a deep copy of the given level map, cloning all entities and their positions.
     *
     * @param other the level map to copy
     */
    public LevelMap(LevelMap other) {
        this.width = other.width;
        this.height = other.height;
        this.grid = new HashMap<>();
        this.entityPositions = new HashMap<>();
        for (Entity entity : other.getEntities()) {
            Entity clonedEntity = new Entity(entity);
            Point position = other.entityPositions.get(entity);
            this.setPosition(clonedEntity, position.x, position.y);
        }
    }

    /**
     * Returns the number of columns in this map.
     *
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the number of rows in this map.
     *
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns whether the given grid coordinate is within the map bounds.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return {@code true} if the coordinate is inside the map
     */
    public boolean isInside(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    /**
     * Remove an entity from the map.
     *
     * @param entity The entity to remove.
     */
    public void removeEntity(Entity entity) {
        Point position = entityPositions.get(entity);
        if (position != null) {
            List<Entity> cell = grid.get(position);
            if (cell != null) {
                cell.remove(entity);
                if (cell.isEmpty()) {
                    grid.remove(position);
                }
            }
            entityPositions.remove(entity);
        }
    }

    /**
     * Add and set the position of an entity on the map.
     * If the entity already exists, it will be moved to the new position.
     *
     * @param entity The entity to place or move.
     * @param newX   The new x-coordinate.
     * @param newY   The new y-coordinate.
     */
    public void setPosition(Entity entity, int newX, int newY) {
        Point oldPosition = entityPositions.get(entity);
        if (oldPosition != null) {
            List<Entity> cell = grid.get(oldPosition);
            if (cell != null) {
                cell.remove(entity);
                if (cell.isEmpty()) {
                    grid.remove(oldPosition);
                }
            }
        }

        Point newPosition = new Point(newX, newY);
        grid.computeIfAbsent(newPosition, k -> new ArrayList<>()).add(entity);
        entityPositions.put(entity, newPosition);
    }

    /**
     * Get the position of an entity on the map.
     *
     * @param entity The entity whose position is to be retrieved.
     * @return A Point representing the entity's position.
     * @throws IllegalStateException if the entity is not found on the map.
     */
    public Point getPosition(Entity entity) {
        Point position = entityPositions.get(entity);
        if (position == null) {
            throw new IllegalStateException("Entity not found in map: " + entity.getEntityId());
        }
        return new Point(position);
    }

    /** Get the X coordinate of an entity on the map.
     *
     * @param entity The entity whose X coordinate is to be retrieved.
     * @return The X coordinate of the entity.
     * @throws IllegalStateException if the entity is not found on the map.
     */
    public int getX(Entity entity) {
        return getPosition(entity).x;
    }

    /** Get the Y coordinate of an entity on the map.
     *
     * @param entity The entity whose Y coordinate is to be retrieved.
     * @return The Y coordinate of the entity.
     * @throws IllegalStateException if the entity is not found on the map.
     */
    public int getY(Entity entity) {
        return getPosition(entity).y;
    }

    /** Get all entities at a specific position on the map.
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @return A list of entities at the specified position.
     */
    public List<Entity> getEntitiesAt(int x, int y) {
        return grid.getOrDefault(new Point(x, y), List.of());
    }

    /**
     * Returns all entities at the given {@link java.awt.Point} position on the map.
     *
     * @param position the position to query
     * @return a list of entities at that position
     */
    public List<Entity> getEntitiesAt(Point position) {
        return getEntitiesAt(position.x, position.y);
    }

    /** Get all entites on the map.
     *
     * @return A list of all entities on the map.
     */
    public List<Entity> getEntities() {
        return new ArrayList<>(entityPositions.keySet());
    }

    /**
     * Finds an entity on the map by its UUID.
     *
     * @param entityId the UUID to search for
     * @return the entity with the given UUID, or {@code null} if not found
     */
    public Entity getEntityById(UUID entityId) {
        return entityPositions.keySet().stream()
                .filter(e -> e.getEntityId().equals(entityId))
                .findFirst()
                .orElse(null);
    }
}
