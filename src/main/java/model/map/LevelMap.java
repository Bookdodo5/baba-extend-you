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
    private final List<Entity> entities;

    public LevelMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new HashMap<>();
        this.entities = new ArrayList<Entity>();
    }

    public LevelMap(LevelMap other) {
        this.width = other.width;
        this.height = other.height;
        this.grid = new HashMap<>();
        this.entities = new ArrayList<Entity>();
        for (Entity entity : other.entities) {
            Entity copiedEntity = new Entity(entity);
            this.addEntity(copiedEntity);
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

    public void addEntity(Entity entity) {
        Point position = new Point(entity.getPosX(), entity.getPosY());
        grid.computeIfAbsent(position, k -> new ArrayList<>()).add(entity);
        entities.add(entity);
    }

    public void removeEntity(Entity entity) {
        Point position = new Point(entity.getPosX(), entity.getPosY());
        List<Entity> cell = grid.get(position);
        if (cell != null) {
            cell.remove(entity);
        }
        entities.remove(entity);
    }

    public void setEntityPosition(Entity entity, int newX, int newY) {
        if (!entities.contains(entity)) {
            throw new IllegalArgumentException("Entity with ID " + entity.getEntityId() + " does not exist in this map.");
        }

        Point oldPosition = new Point(entity.getPosX(), entity.getPosY());
        List<Entity> cell = grid.get(oldPosition);
        if (cell != null) {
            cell.remove(entity);
            if (cell.isEmpty()) grid.remove(oldPosition);
        }

        entity.setPosX(newX);
        entity.setPosY(newY);

        Point newPosition = new Point(newX, newY);
        grid.computeIfAbsent(newPosition, k -> new ArrayList<>()).add(entity);
    }

    public List<Entity> getEntitiesAt(int x, int y) {
        return grid.getOrDefault(new Point(x, y), List.of());
    }

    public List<Entity> getEntitiesOfType(EntityType type) {
        return entities.stream()
                .filter(entity -> entity.getType() == type)
                .collect(Collectors.toList());
    }

    public List<Entity> getEntities() {
        return entities;
    }
}
