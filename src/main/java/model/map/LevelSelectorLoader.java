package model.map;

import model.entity.Direction;
import model.entity.Entity;
import model.entity.EntityType;
import model.entity.TypeRegistry;

import java.io.InputStream;
import java.util.Scanner;

public class LevelSelectorLoader {
    private LevelSelectorLoader() {
    }

    public static LevelSelectorMap loadLevelSelector() {

        InputStream inputStream = LevelSelectorLoader.class.getClassLoader().getResourceAsStream("map/MAP.csv");

        if (inputStream == null) {
            System.err.println("File not found: MAP.csv");
            return null;
        }

        try (Scanner myReader = new Scanner(inputStream)) {
            if (!myReader.hasNextLine()) return null;
            String[] mapSize = myReader.nextLine().trim().split(",");
            int width = Integer.parseInt(mapSize[0]);
            int height = Integer.parseInt(mapSize[1]);

            LevelSelectorMap levelSelectorMap = new LevelSelectorMap(width, height);

            for (int i = 0; i < height; i++) {
                if (!myReader.hasNextLine()) break;
                String[] cells = myReader.nextLine().trim().split(",");

                for (int j = 0; j < Math.min(width, cells.length); j++) {
                    String cellData = cells[j].trim();
                    if (cellData.isBlank()) continue;
                    String[] entityIds = cellData.split("\\+");

                    for (String entityId : entityIds) {
                        String[] parts = entityId.trim().split("-");

                        EntityType entityType = TypeRegistry.getType(parts[0]);
                        if (entityType == null) continue;
                        Entity newEntity = new Entity(entityType);

                        Direction facing = Direction.DOWN;
                        if (parts.length == 2) {
                            facing = Direction.valueOf(parts[1].toUpperCase());
                        }
                        newEntity.setDirection(facing);

                        levelSelectorMap.setPosition(newEntity, j, i);
                    }
                }
            }
            return levelSelectorMap;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }
}
