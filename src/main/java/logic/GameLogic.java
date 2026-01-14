package logic;

import logic.input.InputUtility;
import javafx.scene.input.KeyCode;

public class GameLogic {
    public void update() {
        if(InputUtility.isPressed(KeyCode.SPACE)) System.out.println("Space");
        if(InputUtility.isPressed(KeyCode.W)) System.out.println("W");
        if(InputUtility.isPressed(KeyCode.S)) System.out.println("S");
        if(InputUtility.isPressed(KeyCode.A)) System.out.println("A");
        if(InputUtility.isPressed(KeyCode.D)) System.out.println("D");
        if(InputUtility.isPressed(KeyCode.ESCAPE)) System.exit(0);
    }
}
