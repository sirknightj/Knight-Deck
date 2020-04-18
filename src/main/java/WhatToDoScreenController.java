import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * This is the controller for the WhatToDoScreen.
 */
public class WhatToDoScreenController extends ApplicationController implements Initializable {
    private GameModel model; // the model that manages all the game data

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
    private Text PlayerNameDisplay;
    @FXML
    private Text PlayerHealthDisplay;
    @FXML
    private Text PlayerGoldDisplay;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    /**
     * Creates a new player, or loads the player, given their name.
     *
     * @param name the name of the player to create a new file for, or load from.
     */
    public void createPlayer(String name) {
        model = new GameModel();
        model.loadPlayer(name);
    }

    public void setModel(GameModel model) {
        this.model = model;
    }

    /**
     * Saves the game, and sets the display of the player's stats.
     */
    public void setPlayerDisplay() {
        model.saveData();
        PlayerNameDisplay.setText(model.getPlayer().getName());
        PlayerHealthDisplay.setText(model.getPlayer().getHealth() + "/" + model.getPlayer().getMaxHealth() + " Health");
        PlayerGoldDisplay.setText(model.getPlayer().getGold() + " gold");
    }

    /**
     * Called when the the battle button is clicked on the pre-battle screen.
     * Switches the scene to the battle screen.
     *
     * @param Event The event which triggers this.
     * @throws IOException iff the .fxml file is not loaded properly.
     */
    @FXML
    private void onBattleButtonClick(ActionEvent Event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/BattleScreen.fxml"));
        getStage(Event).setScene(new Scene(loader.load()));
        BattleScreenController controller = loader.getController();
        controller.setModel(model);
    }

    /**
     * Called when the farming button is clicked on the pre-battle screen.
     */
    @FXML
    private void onEasierBattleButtonClick(ActionEvent Event) {

    }

    /**
     * Called when the hospital button is clicked on the pre-battle screen.
     *
     * @param Event the event which triggers this.
     * @throws IOException if the .fxml file is not loaded properly.
     */
    @FXML
    private void onHospitalButtonClick(ActionEvent Event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/HospitalScreen.fxml"));
        getStage(Event).setScene(new Scene(loader.load()));
        HospitalController controller = loader.getController();
        controller.setModel(model);
    }

    /**
     * Called when the shop button is clicked on the pre-battle screen.
     */
    @FXML
    private void onShopButtonClick(ActionEvent Event) {

    }
}
