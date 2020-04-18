import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;

/**
 * This is the controller for the HomeScreen (login screen).
 */
public class HomeScreenController extends ApplicationController {

    @FXML
    private TextField NameInputBox;
    @FXML
    private Text NoNameErrorMessage;

    /**
     * Called when the user clicks the "Play" button. Loads the player profile, or creates
     * a new game file.
     *
     * @param Event the triggering Event.
     * @throws IOException iff the .fxml file is not loaded properly.
     */
    public void play(ActionEvent Event) throws IOException {
        String name = NameInputBox.getText().trim();
        if (name.isEmpty()) {
            NoNameErrorMessage.setText("Please enter your name.");
        } else {
            System.out.println("Name: " + name);
            GameModel model = new GameModel();
            model.loadPlayer(name);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/WhatToDoScreen.fxml"));
            getStage(Event).setScene(new Scene(loader.load()));
            WhatToDoScreenController screenController = loader.getController();
            screenController.setModel(model);
            screenController.setPlayerDisplay();
        }
    }
}
