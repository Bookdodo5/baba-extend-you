package model.map;

import model.entity.Entity;
import model.entity.EntityType;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class LevelMap {
    private final int width;
    private final int height;
    private final Map<Point, List<Entity>> grid;
    private final Map<Entity, Point> entityPositions;

    public LevelMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new HashMap<>();
        this.entityPositions = new HashMap<>();
    }

    public LevelMap(LevelMap other) {
        this.width = other.width;
        this.height = other.height;
        this.grid = new HashMap<>();
        this.entityPositions = new HashMap<>();
        for (Entity entity : other.getEntities()) {
            Entity copiedEntity = new Entity(entity);
            Point position = other.entityPositions.get(entity);
            this.setEntityPosition(copiedEntity, position.x, position.y);
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isInside(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

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

    public void setEntityPosition(Entity entity, int newX, int newY) {
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

    public Point getEntityPosition(Entity entity) {
        Point position = entityPositions.get(entity);
        if (position == null) {
            throw new IllegalStateException("Entity not found in map: " + entity.getEntityId());
        }
        return new Point(position);
    }

    public int getEntityX(Entity entity) {
        return getEntityPosition(entity).x;
    }

    public int getEntityY(Entity entity) {
        return getEntityPosition(entity).y;
    }

    public List<Entity> getEntitiesAt(int x, int y) {
        return grid.getOrDefault(new Point(x, y), List.of());
    }

    public List<Entity> getEntities() {
        return new ArrayList<>(entityPositions.keySet());
    }
}
