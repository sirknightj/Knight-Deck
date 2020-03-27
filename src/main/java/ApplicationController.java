import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class ApplicationController {

    @FXML
//    private static TextField inputBox = (TextField) getScene().lookup("NameInputBox"); // where the player inputs their name

    private ApplicationModel model = new ApplicationModel();

    /**
     * Called when the user clicks the "Play" button.
     *
     * @param Event the triggering Event.
     */
    public void play(ActionEvent Event) {
        // checks for the user's name and looks up their save file.
    }
}
