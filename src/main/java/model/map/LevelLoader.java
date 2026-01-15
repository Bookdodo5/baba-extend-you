package model.map;

import model.entity.Direction;
import model.entity.Entity;
import model.entity.EntityType;
import model.entity.TypeRegistry;

import java.io.InputStream;
import java.util.Scanner;

/**
 * <h1><b>Utility class for loading maps from CSV.</b></h1>
 * 
 * <h2>Example (level1.csv):</h2>
 * ---------------------------------<br/>
 * 5,3<br/>
 * wall+rock,wall,text_java,text_is,text_you<br/>
 * java,.,text_flag,text_is,text_win<br/>
 * .,.,flag,.,rock<br/>
 * ---------------------------------<br/>
 * <b>, : tile separator</b><br/>
 * <b>+ : entity stacking</b><br/>
 * <b>. : empty tile</b>
 */
public class LevelLoader {

    private LevelLoader() {}

    public static LevelMap loadLevel(String fileName) {

        InputStream inputStream = LevelLoader.class.getClassLoader().getResourceAsStream(fileName);

        if (inputStream == null) {
            System.out.println("Cannot find file!");
            return null;
        }

        try (Scanner myReader = new Scanner(inputStream)) {
            if (!myReader.hasNextLine()) return null;
            String[] mapSize = myReader.nextLine().split(",");
            int width = Integer.parseInt(mapSize[0]);
            int height = Integer.parseInt(mapSize[1]);

            LevelMap levelMap = new LevelMap(width, height);

            for (int i = 0; i < height; i++) {
                if (!myReader.hasNextLine()) break;
                String[] cells = myReader.nextLine().split(",");

                for (int j = 0; j < Math.min(width, cells.length); j++) {
                    String cellData = cells[j].trim();
                    if (cellData.isBlank()) continue;
                    String[] entityIds = cellData.split("\\+");

                    for (String entityId : entityIds) {
                        EntityType entityType = TypeRegistry.getType(entityId);
                        if (entityType == null) continue;
                        levelMap.addEntity(new Entity(entityType, j, i));
                    }
                }
            }
            return levelMap;
        } catch (Exception e) {
            System.out.println("File contains string with incorrect format!");
            return null;
        }
    }
}
