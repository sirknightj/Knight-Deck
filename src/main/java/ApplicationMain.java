import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.w3c.dom.Text;

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
     * @throws Exception iff anything goes wrong.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle(NAME_OF_WINDOW);
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/HomeScreen.fxml")));
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
