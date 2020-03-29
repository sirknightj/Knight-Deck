import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * The controller. Controls all elements of HomeScreen and the WhatToDoScreen
 */
public class ApplicationController implements Initializable {

    private ApplicationModel model; // manages all of the data.
    private Stage primaryStage; // the stage on which the application is presented.

    @FXML private TextField NameInputBox;
    @FXML private Text NoNameErrorMessage;
    @FXML private Button PlayButton;
    @FXML private Button BattleButton;
    @FXML private Button EasierBattleButton;
    @FXML private Button HospitalButton;
    @FXML private Button ShopButton;
    @FXML private FlowPane CardHolder;
    private List<Button> battleButtons; // the buttons to place inside CardHolder

    Font descriptionFont; // the preset font for descriptions

    /**
     * Called when this controller is initialized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        descriptionFont = new Font(10);
        battleButtons = new ArrayList<>();
    }

    /**
     * Called when the user clicks the "Play" button.
     *
     * @param Event the triggering Event.
     * @throws IOException iff the .fxml file is not loaded properly.
     */
    public void play(ActionEvent Event) throws IOException {
        updateStage(Event);
        String name = NameInputBox.getText().trim();
        if (name.isEmpty()) {
            NoNameErrorMessage.setText("Please enter your name.");
        } else {
            System.out.println("Name: " + name);
            goToPreBattleMenu(Event);
            model = new ApplicationModel(name);
        }
    }

    /**
     * Switches the scene to the pre-battle scene.
     *
     * @param Event The event which triggers this.
     * @throws IOException iff the .fxml file is not loaded properly.
     */
    private void goToPreBattleMenu(ActionEvent Event) throws IOException {
        primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/WhatToDoScreen.fxml"))));
        primaryStage.show();
    }

    /**
     * Called when the the battle button is clicked on the pre-battle screen.
     * Switches the scene to the battle screen.
     *
     * @param Event The event which triggers this.
     * @throws IOException iff the .fxml file is not loaded properly.
     */
    public void onBattleButtonClick(ActionEvent Event) throws IOException {
        updateStage(Event);
        primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/BattleScreen.fxml"))));
        primaryStage.show();
    }

    /**
     * Called when the farming button is clicked on the pre-battle screen.
     */
    public void onEasierBattleButtonClick(ActionEvent Event) {

    }

    /**
     * Called when the hospital button is clicked on the pre-battle screen.
     */
    public void onHospitalButtonClick(ActionEvent Event) {

    }

    /**
     * Called when the hospital button is clicked on the pre-battle screen.
     */
    public void onShopButtonClick(ActionEvent Event) {

    }

    /**
     * Resets the cards to be displayed on the bottom bar of the battle screen.
     *
     * @param cards The cards to be displayed.
     */
    public void resetCardDisplay(List<Card> cards) {
        CardHolder = (FlowPane) primaryStage.getScene().lookup("CardHolder");
        CardHolder.getChildren().clear();
        battleButtons.clear();
        for (Card card : cards) {
            Button buttonToAdd = new Button(card.getName());
            Label description = new Label(card.getDescription(model.getPlayer()));
            description.setFont(descriptionFont);
            buttonToAdd.setGraphic(description);
            battleButtons.add(buttonToAdd);
        }
        CardHolder.getChildren().addAll(battleButtons);
    }

    private void updateStage(ActionEvent Event) {
        primaryStage = (Stage) ((Node) Event.getSource()).getScene().getWindow();
    }
}
