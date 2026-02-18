package state;

import application.GameController;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import logic.input.InputCommand;
import logic.input.InputUtility;
import utils.GraphicUtils;

import static application.Constant.TARGET_SCREEN_HEIGHT;
import static application.Constant.TARGET_SCREEN_WIDTH;

/**
 * Represents the credits screen state displaying game credits and acknowledgments.
 */
public class CreditsState implements GameState {

    private BorderPane mainLayout;

    /**
     * Initializes the credits screen when entering this state.
     *
     * @param previousState The previous game state
     */
    @Override
    public void onEnter(GameStateEnum previousState) {
        createCreditsLayout();
        putCreditsLayout();
    }

    /**
     * Cleans up the credits screen when exiting this state.
     */
    @Override
    public void onExit() {
        removeCreditsLayout();
    }

    /**
     * Updates the credits state, handling user input to return to the title screen.
     */
    @Override
    public void update() {
        InputCommand playerInput = InputUtility.getTriggered();
        if (playerInput == InputCommand.MENU) {
            GameController.getInstance().setState(GameStateEnum.TITLE);
        }
    }

    /**
     * Renders the background for the credits screen.
     *
     * @param gc The graphics context to render on
     */
    @Override
    public void render(GraphicsContext gc) {
        Color theme = GameController.getInstance().getColorTheme();
        gc.setFill(theme.interpolate(Color.BLACK, 0.8));
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
    }

    private void createCreditsLayout() {
        mainLayout = new BorderPane();
        mainLayout.setPrefSize(TARGET_SCREEN_WIDTH, TARGET_SCREEN_HEIGHT);
        mainLayout.setPickOnBounds(true);

        // Header Section
        HBox creditsTitle = GraphicUtils.createTextNode("Credits", 4.0, Color.WHITE);
        mainLayout.setTop(creditsTitle);
        BorderPane.setMargin(creditsTitle, new Insets(20, 20, 30, 20));

        // Main Content Section
        HBox creditsGrid = new HBox();
        creditsGrid.setAlignment(Pos.TOP_CENTER);
        creditsGrid.setSpacing(100);
        creditsGrid.getChildren().addAll(
                createLeftColumn(),
                createRightColumn()
        );

        mainLayout.setCenter(creditsGrid);

        // ESC to return
        HBox footerText = GraphicUtils.createTextNode("Press ESC to return", 1.0, Color.LIGHTGRAY);
        mainLayout.setBottom(footerText);
        BorderPane.setMargin(footerText, new Insets(10, 20, 20, 20));

        TranslateTransition footerAnimation = new TranslateTransition(Duration.seconds(0.5), footerText);
        footerAnimation.setInterpolator(Interpolator.SPLINE(0.25, 0, 0.25, 1));
        footerAnimation.setFromY(0);
        footerAnimation.setToY(-5);
        footerAnimation.setCycleCount(TranslateTransition.INDEFINITE);
        footerAnimation.setAutoReverse(true);

        footerAnimation.play();
    }

    private VBox createLeftColumn() {
        VBox leftColumn = new VBox(30);

        // Team Member Section
        VBox teamMemberSection = createSection(
                "Team Members",
                new String[]{
                        "Ratchaphon (6833233321)",
                        "Tapanan (6833058921)",
                        "Thianrawit (6833128121)",
                        "Tinnaphop (6833096721)"
                }
        );

        // Software Used Section
        VBox softwareSection = createSection(
                "Software used",
                new String[]{
                        "Piskel",
                        "IntelliJ Ultimate",
                        "Github Copilot",
                        "MusicGPT"
                }
        );

        leftColumn.getChildren().addAll(
                teamMemberSection,
                softwareSection
        );

        return leftColumn;
    }

    private VBox createRightColumn() {
        VBox rightColumn = new VBox(30);

        // Original Game Section
        VBox originalGameSection = createSection(
                "Original Game",
                new String[]{
                        "Baba is you", "(by Arvi Teikari)",
                }
        );

        // Technologies Section
        VBox techSection = createSection(
                "Built With",
                new String[]{
                        "Java 24.0.1",
                        "JavaFX 24.0.1",
                        "JUnit 5",
                        "Gradle",
                }
        );

        // Special Thanks Section
        VBox thanksSection = createSection(
                "Special Thanks",
                new String[]{
                        "2110215 CU ProgMeth",
                        "All Playtesters"
                }
        );

        rightColumn.getChildren().addAll(
                originalGameSection,
                techSection,
                thanksSection
        );

        return rightColumn;
    }

    private VBox createSection(String title, String[] items) {
        VBox section = new VBox(4);

        // Section Header
        HBox sectionHeader = GraphicUtils.createTextNode(title, 1.5, Color.GOLDENROD);
        section.getChildren().add(sectionHeader);

        // Section Items
        for (String item : items) {
            HBox itemNode = GraphicUtils.createTextNode(item, 1.0, Color.WHITE);
            section.getChildren().add(itemNode);
        }

        return section;
    }

    private void putCreditsLayout() {
        StackPane rootPane = GameController.getInstance().getRootPane();
        if (rootPane != null && mainLayout != null) {
            rootPane.getChildren().add(mainLayout);
        }
    }

    private void removeCreditsLayout() {
        StackPane rootPane = GameController.getInstance().getRootPane();
        if (rootPane != null && mainLayout != null) {
            rootPane.getChildren().remove(mainLayout);
        }
    }
}
