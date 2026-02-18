package state;

import application.GameController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import utils.GraphicUtils;

import java.util.Objects;

import static application.Constant.*;

public class TitleState implements GameState {
    private VBox titleBox;
    private static final Image TITLE_IMAGE = new Image(
            Objects.requireNonNull(
                    TitleState.class.getResourceAsStream("/title/title.gif")
            )
    );
    private static final Image TITLE_BACKGROUND = new Image(
            Objects.requireNonNull(
                    TitleState.class.getResourceAsStream("/title/background.png")
            )
    );

    /**
     *
     */
    @Override
    public void onEnter(GameStateEnum previousState) {
        // TODO (SOUND) : play state transition sound
        // TODO (SOUND) : play title music
        createTitleBox();
        putTitleBox();
    }

    /**
     *
     */
    @Override
    public void onExit() {
        // TODO (SOUND) : stop title music
        removeTitleBox();
    }

    /**
     *
     */
    @Override
    public void update() {

    }

    /**
     *
     */
    @Override
    public void render(GraphicsContext gc) {
        long currentTime = System.currentTimeMillis();
        double percentInCycle = (double) (currentTime % MILLISECONDS_PER_TITLE_CYCLE) / MILLISECONDS_PER_TITLE_CYCLE;
        double hueShift = (double) ((currentTime * 5) % MILLISECONDS_PER_TITLE_CYCLE) / MILLISECONDS_PER_TITLE_CYCLE;

        ColorAdjust hue = new ColorAdjust(hueShift * 2 - 1, 1, 0.3, 0.2);
        gc.setEffect(hue);

        gc.drawImage(
                TITLE_BACKGROUND,
                TITLE_BACKGROUND.getWidth() * percentInCycle / 2, TITLE_BACKGROUND.getHeight() * percentInCycle / 2,
                TITLE_BACKGROUND.getWidth() / 2, TITLE_BACKGROUND.getHeight() / 2,
                0, 0,
                gc.getCanvas().getWidth(), gc.getCanvas().getHeight()
        );
        gc.setEffect(null);

        Color theme = GameController.getInstance().getColorTheme();
        Color bgColor = theme.interpolate(Color.BLACK, 0.8);
        gc.setFill(bgColor.interpolate(Color.TRANSPARENT, 0.1));
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
    }

    private void createTitleBox() {
        titleBox = new VBox();
        titleBox.setPrefSize(TARGET_SCREEN_WIDTH, TARGET_SCREEN_HEIGHT);

        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(10, 50, 50, 10));
        titleBox.setSpacing(5);
        titleBox.setPickOnBounds(true);

        //Title text
        ImageView titleImageView = new ImageView(TITLE_IMAGE);
        titleImageView.setPreserveRatio(true);
        titleImageView.setSmooth(false);
        titleImageView.setScaleX(2.0);
        titleImageView.setScaleY(2.0);
        VBox.setMargin(titleImageView, new Insets(0, 0, 80, 0));

        //Buttons
        Button startButton = GraphicUtils.createButton("Start Game", this::startGame, 400, 40);
        Button creditsButton = GraphicUtils.createButton("Credits", this::credits, 400, 40);
        Button exitButton = GraphicUtils.createButton("Exit The Game", this::exitGame, 400, 40);

        titleBox.getChildren().addAll(titleImageView, startButton, creditsButton, exitButton);
    }

    private void putTitleBox() {
        StackPane rootPane = GameController.getInstance().getRootPane();
        if (rootPane != null && titleBox != null) {
            rootPane.getChildren().add(titleBox);
        }
    }

    private void removeTitleBox() {
        StackPane rootPane = GameController.getInstance().getRootPane();
        if (rootPane != null && titleBox != null) {
            rootPane.getChildren().remove(titleBox);
        }
    }

    private void startGame() {
        GameController.getInstance().setState(GameStateEnum.MAP);
    }

    private void credits() {
        GameController.getInstance().setState(GameStateEnum.CREDITS);
    }

    private void exitGame() {
        System.exit(0);
    }
}
