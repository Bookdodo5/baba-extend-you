package state;

import application.GameController;
import application.Audio;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import logic.input.InputCommand;
import logic.input.InputUtility;
import utils.GraphicUtils;
import utils.ImageUtils;

import static application.Constant.*;

public class TitleState implements GameState {

    private static final int SPACING = 5;
    private static final int TOTAL_BUTTON = 3;
    private static final int TITLE_BUTTON_WIDTH = 400;
    private static final int TITLE_BUTTON_HEIGHT = 40;
    private static final int TITLE_TEXT_MARGIN = 60;
    private static final double TITLE_TEXT_SCALE = 2.0;

    private static final Image TITLE_IMAGE = ImageUtils.getImage("/title/title.gif");
    private static final Image TITLE_BACKGROUND = ImageUtils.getImage("/title/background.png");
    private static final Image JAVA_IMAGE = ImageUtils.getImage("/sprite/JAVA.png");

    private VBox titleBox;
    private ImageView selectIndicator;
    private int currentSelectedIndex = 0;

    /**
     *
     */
    @Override
    public void onEnter(GameStateEnum previousState) {
        Audio.playMusic("sound/music/Pixel_Quest_MainTheme.wav");
        createTitleBox();
        putTitleBox();
    }

    /**
     *
     */
    @Override
    public void onExit() {
        removeTitleBox();
    }

    /**
     *
     */
    @Override
    public void update() {
        InputCommand playerInput = InputUtility.getTriggered();

        if (playerInput == InputCommand.MOVE_UP) {
            Audio.playSfx("sound/SFX/select.wav");
            currentSelectedIndex = (currentSelectedIndex + TOTAL_BUTTON - 1) % TOTAL_BUTTON;
        }
        if (playerInput == InputCommand.MOVE_DOWN) {
            Audio.playSfx("sound/SFX/select.wav");
            currentSelectedIndex = (currentSelectedIndex + 1) % TOTAL_BUTTON;
        }
        if (playerInput == InputCommand.TRIGGER) {
            Audio.playSfx("sound/SFX/confirm.wav");
            switch (currentSelectedIndex) {
                case 0 -> startGame();
                case 1 -> credits();
                case 2 -> exitGame();
            }
        }
        if (playerInput == InputCommand.MENU) {
            Audio.playSfx("sound/SFX/esc.wav");
            removeTitleBox();
            createTitleBox();
            putTitleBox();
        }

        GraphicUtils.updateIndicatorPosition(
                selectIndicator,
                currentSelectedIndex,
                TITLE_BUTTON_HEIGHT, SPACING
        );
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
        titleBox.setSpacing(SPACING);
        titleBox.setPickOnBounds(true);

        //Title text
        ImageView titleImageView = new ImageView(TITLE_IMAGE);
        titleImageView.setPreserveRatio(true);
        titleImageView.setSmooth(false);
        titleImageView.setScaleX(TITLE_TEXT_SCALE);
        titleImageView.setScaleY(TITLE_TEXT_SCALE);
        VBox.setMargin(titleImageView, new Insets(0, 0, TITLE_TEXT_MARGIN, 0));

        //Buttons
        Button startButton = GraphicUtils.createButton("Start Game", this::startGame, TITLE_BUTTON_WIDTH, TITLE_BUTTON_HEIGHT);
        Button creditsButton = GraphicUtils.createButton("Credits", this::credits, TITLE_BUTTON_WIDTH, TITLE_BUTTON_HEIGHT);
        Button exitButton = GraphicUtils.createButton("Exit The Game", this::exitGame, TITLE_BUTTON_WIDTH, TITLE_BUTTON_HEIGHT);

        // Java sprite indicator
        selectIndicator = GraphicUtils.createButtonIndicator(JAVA_IMAGE, TITLE_BUTTON_WIDTH);

        VBox buttonContainer = new VBox(SPACING);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.getChildren().addAll(startButton, creditsButton, exitButton);

        StackPane contentPane = new StackPane();
        contentPane.setAlignment(Pos.CENTER);
        contentPane.getChildren().addAll(buttonContainer, selectIndicator);

        titleBox.getChildren().addAll(titleImageView, contentPane);
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
