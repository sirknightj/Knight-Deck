import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * The starting point for the Knight Deck application.
 */
public class ApplicationMain extends Application {

    private static final String NAME_OF_WINDOW = "Knight Deck";
    private static Button startButton, creditsButton;

    /**
     * Launches this application.
     */
    public static void main(String[] args) {
        launch(args);
    }


    /**
     * Loads the HomeScreen file.
     *
     * @param primaryStage the stage on which the resources are set.
     * @throws IOException iff the .fxml file is not loaded properly.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle(NAME_OF_WINDOW);
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/HomeScreen.fxml")));
        primaryStage.setScene(scene);
        scene.lookup("#EntireWindow").requestFocus();
        primaryStage.show();
    }
}
