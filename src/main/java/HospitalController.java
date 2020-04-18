import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * This is the controller for the Hospital screen.
 */
public class HospitalController extends ApplicationController implements Initializable {

    @FXML private Text PlayerDisplay;
    @FXML private Text ClericDialogueBox;
    @FXML private HBox HospitalDialogueBox;
    @FXML private Button HospitalNextButton;
    private Button HospitalYesButton;
    private Button HospitalNoButton;

    private int hospitalStatus; // stores the index of which dialogue should be shown.
    private Hospital hospital;

    /**
     * Initializes the components in the hospital, and starts the Nurse's dialogue.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        hospital = Hospital.getInstance();
        HospitalDialogueBox.setStyle("-fx-border-color: black");
        HospitalNextButton.setText("Next");

        HospitalYesButton = new Button("Yes");
        initButtons(HospitalYesButton);
        HospitalNoButton = new Button("No");
        initButtons(HospitalNoButton);
        showNextButton();

        animateText(ClericDialogueBox, HospitalNextButton, "Welcome to the field hospital.");
        resetHospitalStatus();
    }

    /**
     * Initializes the yes/no buttons.
     */
    private void initButtons(Button button) {
        button.setVisible(false);
        button.setDisable(true);
        button.setOnAction(new EventHandler<ActionEvent>() {
            /**
             * Called when either the yes or no buttons are clicked. If yes, heals the player. If no, advances to the next dialogue.
             */
            @Override
            public void handle(ActionEvent Event) {
                if (Event.getSource() == HospitalYesButton) {
                    hospital.healPlayerToFull();
                    setPlayerDisplay();
                }
                animateText(ClericDialogueBox, HospitalNextButton, "Not saying you should get yourself injured, but come back when you need healing!");
                showLeave();
            }
        });
        button.setStyle("-fx-font-size:30");
        button.setAlignment(Pos.CENTER);
    }

    /**
     * Sets up the player display.
     */
    public void setPlayerDisplay() {
        PlayerDisplay.setText(model.getPlayer().getName() + "   " + model.getPlayer().getHealth() + "/" + model.getPlayer().getMaxHealth() + " Health   " + model.getPlayer().getGold() + " gold");
    }

    /**
     * Sets the model, and puts the player in the hospital.
     *
     * @param model
     */
    public void setModel(GameModel model) {
        this.model = model;
        hospital.setPlayer(model.getPlayer());
        setPlayerDisplay();
    }

    /**
     * Called when the Next button is clicked on the Hospital Screen.
     *
     * @param Event the event which triggers this.
     * @throws IOException iff the .fxml file fails to load.
     */
    @FXML
    private void onHospitalNextButtonClicked(ActionEvent Event) throws IOException {
        if (HospitalNextButton.getText().equals("Next")) {
            String dialogue = getNextHospitalText();
            assert dialogue != null;
            animateText(ClericDialogueBox, HospitalNextButton, dialogue);
            if (isItYesNoTime()) {
                showYesNoButtons();
            } else if (isItLeavingTime()) {
                showLeave();
            }
        } else { // equals "Leave"
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/WhatToDoScreen.fxml"));
            getStage(Event).setScene(new Scene(loader.load()));
            WhatToDoScreenController screenController = loader.getController();
            screenController.setModel(model);
            screenController.setPlayerDisplay();
        }
    }

    /**
     * Changes the Hospital Next button to show the yes/no buttons instead.
     */
    private void showNextButton() {
        HospitalNextButton.setDisable(false);
        HospitalNextButton.setVisible(true);
        HospitalYesButton.setVisible(false);
        HospitalNoButton.setVisible(false);
        HospitalYesButton.setDisable(true);
        HospitalNoButton.setDisable(true);
        if (!HospitalDialogueBox.getChildren().contains(HospitalNextButton)) {
            HospitalDialogueBox.getChildren().add(HospitalNextButton);
        }
        if (HospitalDialogueBox.getChildren().contains(HospitalYesButton)) {
            HospitalDialogueBox.getChildren().remove(HospitalYesButton);
            HospitalDialogueBox.getChildren().remove(HospitalNoButton);
        }
        ClericDialogueBox.setWrappingWidth(880d);
    }

    /**
     * Shows the Yes/No buttons, and enables them for clicking.
     */
    private void showYesNoButtons() {
        HospitalNextButton.setDisable(true);
        HospitalNextButton.setVisible(false);
        HospitalYesButton.setVisible(true);
        HospitalNoButton.setVisible(true);
        HospitalYesButton.setDisable(false);
        HospitalNoButton.setDisable(false);
        HospitalDialogueBox.getChildren().remove(HospitalNextButton);
        HospitalDialogueBox.getChildren().add(HospitalYesButton);
        HospitalDialogueBox.getChildren().add(HospitalNoButton);
        ClericDialogueBox.setWrappingWidth(820d);
    }

    /**
     * Changes the buttons to the leaving scene.
     */
    private void showLeave() {
        showNextButton();
        HospitalNextButton.setText("Leave");
    }

    /**
     * Resets the hospital dialogue status.
     */
    private void resetHospitalStatus() {
        hospitalStatus = 0;
    }

    /**
     * @return true iff it's time to switch to the yes/no boxes.
     */
    private boolean isItYesNoTime() {
        return hospitalStatus == 6;
    }

    /**
     * @return true iff it's time for the player to leave the hospital.
     */
    private boolean isItLeavingTime() {
        return hospitalStatus == 11;
    }

    /**
     * @return the next dialogue the nurse is supposed to say.
     */
    public String getNextHospitalText() {
        hospitalStatus++;
        if (hospitalStatus == 1) {
            return "Let me take a look at your wounds...";
        }
        if (hospitalStatus == 5) {
            return "It doesn't look like you have enough gold for me to heal you. It costs " + hospital.getHealingCost()
                    + " gold, and you have " + model.getPlayer().getGold() + " gold.";
        }
        if (hospitalStatus == 6) {
            hospitalStatus = 10;
            return "I really would like to heal you, but I'm poor and don't have any extra supplies.";
        }
        if (hospitalStatus == 11) {
            return "Not saying you should get yourself injured, but come back when you need healing!";
        }
        if (hospital.playerNeedHealing()) {
            if (model.getPlayer().getGold() > 1) {
                if (hospitalStatus == 2) {
                    return "...";
                } else if (hospitalStatus == 3) {
                    return "It looks like you're injured pretty badly.";
                } else if (hospitalStatus == 4) {
                    if (hospital.playerHasEnoughGold()) {
                        hospitalStatus = 6;
                        return "I can heal you all the way to full, but it'll cost you " + hospital.getHealingCost() + " gold. Deal?";
                    } else {
                        return getNextHospitalText();
                    }
                }
            } else { // player has no gold
                if (hospitalStatus == 2) {
                    hospitalStatus = 5;
                    return "Supplies are low right now. I need gold to heal you, and you have none.";
                }
            }
        } else if (hospitalStatus == 2) { // player is not injured
            hospitalStatus = 10;
            return "You don't appear to have any injuries.";
        }
        return null;
    }
}