import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * The controller. Controls all elements of HomeScreen and the WhatToDoScreen
 */
public class ApplicationController implements Initializable {

    private static ApplicationModel model; // manages all of the data.
    private Stage primaryStage; // the stage on which the application is presented.

    @FXML
    private TextField NameInputBox;
    @FXML
    private Text NoNameErrorMessage;
    @FXML
    private Button PlayButton;
    @FXML
    private Button BattleButton;
    @FXML
    private Button EasierBattleButton;
    @FXML
    private Button HospitalButton;
    @FXML
    private Button ShopButton;
    @FXML
    private HBox CardHolder;
    @FXML
    private Button HospitalYesButton;
    @FXML
    private Button HospitalNoButton;
    @FXML
    private Text ClericDialogueBox;
    @FXML
    private HBox HospitalDialogueBox;
    @FXML
    private Button HospitalNextButton;
    @FXML
    private Text PreBattlePlayerName;
    @FXML
    private Text PreBattlePlayerHealth;
    @FXML
    private Text PreBattleGoldDisplay;

    private List<Button> battleButtons; // the buttons to place inside CardHolder

    Font descriptionFont; // the preset font for descriptions


    /**
     * Called when this controller is initialized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        descriptionFont = new Font(10);
        try {
            Path urlPath = Paths.get(url.toURI());
            if (urlPath.equals(Paths.get(getClass().getResource("/WhatToDoScreen.fxml").toURI()))) {
                initializeWhatToDoScreen();
            } else if (urlPath.equals(Paths.get(getClass().getResource("/BattleScreen.fxml").toURI()))) {
                System.out.println("To Battle!");
                battleButtons = new ArrayList<>();
                model.initializeBattle();
                resetCardDisplay(model.getPlayer().getActionDeck());
            } else if (urlPath.equals(Paths.get(getClass().getResource("/HospitalScreen.fxml").toURI()))) {
                initializeHospital();
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * Called when the user clicks the "Play" button.
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
            model = new ApplicationModel(name, this);
            goToPreBattleMenu(Event);
        }
    }

    /**
     * Switches the scene to the pre-battle scene.
     *
     * @param Event The event which triggers this.
     * @throws IOException iff the .fxml file is not loaded properly.
     */
    private void goToPreBattleMenu(ActionEvent Event) throws IOException {
        getStage(Event).setScene(new Scene(FXMLLoader.load(getClass().getResource("/WhatToDoScreen.fxml"))));
    }

    /**
     * Called when the the battle button is clicked on the pre-battle screen.
     * Switches the scene to the battle screen.
     *
     * @param Event The event which triggers this.
     * @throws IOException iff the .fxml file is not loaded properly.
     */
    public void onBattleButtonClick(ActionEvent Event) throws IOException {
        getStage(Event).setScene(new Scene(FXMLLoader.load(getClass().getResource("/BattleScreen.fxml"))));
    }

    /**
     * Called when the farming button is clicked on the pre-battle screen.
     */
    public void onEasierBattleButtonClick(ActionEvent Event) {

    }

    /**
     * Called when the hospital button is clicked on the pre-battle screen.
     *
     * @param Event the event which triggers this.
     * @throws IOException if the .fxml file is not loaded properly.
     */
    public void onHospitalButtonClick(ActionEvent Event) throws IOException {
        getStage(Event).setScene(new Scene(FXMLLoader.load(getClass().getResource("/HospitalScreen.fxml"))));
    }

    /**
     * Called when the shop button is clicked on the pre-battle screen.
     */
    public void onShopButtonClick(ActionEvent Event) {

    }

    /**
     * Called when either the Yes or No buttons on the hospital screen. Heals the player and takes their
     * gold away if yes.
     *
     * @param Event the event which triggers this.
     * @throws IOException iff the .fxml file is not loaded properly.
     */
    public void HospitalFunction(ActionEvent Event) throws IOException {
        if (Event.getSource() == HospitalYesButton) {
            model.healPlayerToFull();
        }
        animateText(ClericDialogueBox, HospitalNextButton, "Not saying you should get yourself injured, but come back when you need healing!");
        changeHospitalNextButton(false);
    }

    /**
     * Changes the Hospital Next button to show the yes/no buttons instead.
     *
     * @param now true if the yes/no buttons should be showing. false if the yes/no buttons should be showing.
     */
    public void changeHospitalNextButton(boolean now) {
        HospitalNextButton.setDisable(now);
        HospitalNextButton.setVisible(!now);
        HospitalYesButton.setVisible(now);
        HospitalNoButton.setVisible(now);
        HospitalYesButton.setDisable(!now);
        HospitalNoButton.setDisable(!now);
        if (now) {
            HospitalNextButton.setText("Leave");
        }
    }

    /**
     * Resets the cards to be displayed on the bottom bar of the battle screen.
     *
     * @param cards The cards to be displayed.
     */
    public void resetCardDisplay(List<Card> cards) {
        CardHolder.getChildren().clear();
        battleButtons.clear();
        for (Card card : cards) {
            Text description = new Text(card.getDescription(model.getPlayer()));
            description.setWrappingWidth(50);
            description.setFont(descriptionFont);
            Button button = new Button(card.getName(), description);
            button.setMaxSize(3000, 3000);
            button.wrapTextProperty().setValue(true);
            button.setContentDisplay(ContentDisplay.BOTTOM);
            button.setOnAction(actionEvent -> {
//               if(model.battleManager.play(card)) {
//                   button.setVisible(false);
//               } else {
//                   errorMessage.setText(battleManager.getErrorMessage())
//               }
            });
            battleButtons.add(button);
        }
        CardHolder.getChildren().addAll(battleButtons);
    }

    private void initializeWhatToDoScreen() {
        PreBattlePlayerName.setText(model.getPlayer().getName());
        PreBattlePlayerHealth.setText(model.getPlayer().getHealth() + "/" + model.getPlayer().getMaxHealth() + " Health");
        PreBattleGoldDisplay.setText(model.getPlayer().getGold() + " gold");
    }

    /**
     * Initializes the components in the hospital, and starts the Nurse's dialogue.
     */
    private void initializeHospital() {
        changeHospitalNextButton(false);
        HospitalDialogueBox.setStyle("-fx-border-color: black");
        HospitalNextButton.setText("Next");
        animateText(ClericDialogueBox, HospitalNextButton, "Welcome to the field hospital.");
        model.resetHospitalStatus();
    }

    /**
     * Called when the Next button is clicked on the Hospital Screen.
     *
     * @param Event the event which triggers this.
     * @throws IOException iff the .fxml file fails to load.
     */
    public void onHospitalNextButtonClicked(ActionEvent Event) throws IOException {
        if (HospitalNextButton.getText().equals("Next")) {
            String dialogue = model.getNextHospitalText();
            assert dialogue != null;
            animateText(ClericDialogueBox, HospitalNextButton, dialogue);
            if (model.isItYesNoTime()) {
                changeHospitalNextButton(true);
            } else if (model.isItLeavingTime()) {
                HospitalNextButton.setText("Leave");
            }
        } else { // equals "Leave"
            getStage(Event).setScene(new Scene(FXMLLoader.load(getClass().getResource("/WhatToDoScreen.fxml"))));
        }
    }

    /**
     * Makes the dialogue inside the textbox seem like the person's talking. Also disables the Next button
     * while the animation is running.
     *
     * @param textbox  the textbox to animate.
     * @param next     the button to disable during the duration of the animation.
     * @param dialogue the dialogue to put in the textbox.
     */
    private void animateText(Text textbox, Button next, String dialogue) {
        textbox.setText("");
        next.setDisable(true);
        final IntegerProperty i = new SimpleIntegerProperty(0);
        Timeline timeline = new Timeline();
        KeyFrame keyFrame = new KeyFrame(
                Duration.millis(30),
                event -> {
                    if (i.get() > dialogue.length()) {
                        timeline.stop();
                        next.setDisable(false);
                    } else {
                        textbox.setText(dialogue.substring(0, i.get()));
                        i.set(i.get() + 1);
                    }
                });
        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    /**
     * @param Event
     * @return
     */
    private Stage getStage(ActionEvent Event) {
        return (Stage) ((Node) Event.getSource()).getScene().getWindow();
    }
}
