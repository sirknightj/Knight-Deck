import javafx.animation.FadeTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * This controls all of the elements on the battle screen.
 */
public class BattleScreenController extends ApplicationController implements Initializable {

    @FXML private VBox BattleScreen;
    @FXML private HBox CardHolder;
    @FXML private HBox EnemyHolder;
    @FXML private Text APDisplay;
    @FXML private Text ActionSummary;
    @FXML private Button EndTurnButton;
    private Region LSpacer, RSpacer;
    private GameModel model;
    BattleManager battleManager;
    private SimpleStringProperty actionPointsDisplay; // the text to be displayed in the ActionPoints display.

    /**
     * TODO: Auto end turn, handle player death, handle loot drops, add enemy turn display, add health display,
     * add animdations for cards coming in, and work on graphics.
     */

    /**
     * Called when this controller is initialized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        actionPointsDisplay = new SimpleStringProperty();
        APDisplay.textProperty().bind(actionPointsDisplay);
        LSpacer = new Region();
        HBox.setHgrow(LSpacer, Priority.ALWAYS);
        RSpacer = new Region();
        HBox.setHgrow(RSpacer, Priority.ALWAYS);
    }

    /**
     * Accepts the model from the other controller, and also initializes the battle.
     *
     * @param model the model from the other controller.
     */
    public void setModel(GameModel model) {
        this.model = model;
        battleManager = model.startBattle(model.getDifficulty());
        battleManager.start();
        System.out.println(battleManager.getEnemies().toString());
        startPlayerTurn();
    }

    /**
     * Does the pre-player turn agenda. Updates all of the holders to display the enemies, the player, and the cards.
     */
    private void startPlayerTurn() {
        battleManager.prePlayerTurn();
        assert model.getPlayer().getActionDeck() != null;
        initMobDisplay(battleManager.getEnemies());
        updateDisplays();
        CardHolder.setVisible(true);
        EndTurnButton.setDisable(false);
    }

    /**
     * Plays the card. Assumes the card can be played against the enemy.
     *
     * @param card  the card to be played.
     * @param enemy the enemy to be targeted.
     */
    private void attack(Card card, Enemy enemy) {
        BattleManager.ActionSummary as = battleManager.playerAction(card, enemy);
        for (Being target : as.getOpponents()) {
            for (Node node : EnemyHolder.getChildren()) {
                if (node instanceof StackPane) {
                    String enemyName = ((Text) ((StackPane) node).getChildren().get(1)).getText();
                    if (enemyName.equals(target.getName())) {
                        Text damageDealt = new Text("-" + as.getCardPlayed().getDamage());
                        damageDealt.setFont(new Font(20));
                        ((StackPane) node).getChildren().add(damageDealt);
                        floatingText(damageDealt);
                    }
                }
            }
        }
        updateDisplays();
        animateText(ActionSummary, EndTurnButton, as.toString());
        if (battleManager.isBattleOver()) {
            Set<Card> choose = battleManager.postGame();
            BattleScreen.getChildren().clear();
            Text endingDialogue = new Text();
            endingDialogue.setFont(new Font(20));
            BattleScreen.getChildren().add(endingDialogue);
            if (true) { // choose.isEmpty()
                animateText(endingDialogue, "The enemies didn't drop any cards....", new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        try {
                            goToWhatToDoScreen();
                        } catch (IOException e) {
                            System.out.println("HUGE ERROR!");
                            e.printStackTrace();
                        }
                    }
                }, 2);
            }
        }
    }

    /**
     * Returns to the WhatToDoScreen.
     *
     * @throws IOException iff the .fxml file is not found.
     */
    private void goToWhatToDoScreen() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/WhatToDoScreen.fxml"));
        ((Stage) (BattleScreen.getScene()).getWindow()).setScene(new Scene(loader.load()));
        WhatToDoScreenController screenController = loader.getController();
        screenController.setModel(model);
        screenController.setPlayerDisplay();
    }

    /**
     * Updates the enemy displays, card displays, and the actionPoint display.
     */
    private void updateDisplays() {
        resetCardDisplay(model.getPlayer().getActionDeck());
        updateMobDisplay();
        actionPointsDisplay.setValue(model.getPlayer().getActionPoints() + "/" + model.getPlayer().getMaxActionPoints() + " Action Points");
    }

    /**
     * Called when the EndTurn button is clicked. Ends the player's turn, and performs the enemy's turn and post turn.
     * @param Event
     */
    @FXML
    private void onEndTurnClicked(ActionEvent Event) {
        EndTurnButton.setDisable(true);
        battleManager.preEnemyTurn();
        List<BattleManager.ActionSummary> moves = battleManager.enemiesTurn();
        for (BattleManager.ActionSummary move : moves) {
            System.out.println(move); // TODO: Parse this to make it readable!
        }
        battleManager.postTurn();
        startPlayerTurn();
    }

    /**
     * Creates all of the views of all the beings on the battlefield.
     *
     * @param enemies The enemies on the battlefield.
     */
    public void initMobDisplay(List<Enemy> enemies) {
        EnemyHolder.getChildren().clear();
        resetPlayerDisplay();
        for (Enemy enemy : enemies) {
            Rectangle enemyOutline = getOutline();

            Text name = new Text(enemy.getName());
            name.setTextAlignment(TextAlignment.CENTER);
            name.setFont(new Font(20));

            StackPane enemyHolder = new StackPane();
            enemyHolder.getChildren().addAll(enemyOutline, name);
            enemyHolder.setOnDragOver(new EventHandler<DragEvent>() {
                @Override
                public void handle(DragEvent dragEvent) {
                    if (dragEvent.getDragboard().hasString()) {
                        Card card = CardFactory.getCard(dragEvent.getDragboard().getString());
                        if (card != null && card.getDamage() > 0) {
                            dragEvent.acceptTransferModes(TransferMode.ANY);
                        }
                    }
                    dragEvent.consume();
                }
            });
            enemyHolder.setOnDragDropped(new EventHandler<DragEvent>() {
                @Override
                public void handle(DragEvent dragEvent) {
                    boolean isCheating = true;
                    for (Card card : model.getPlayer().getActionDeck()) {
                        if (dragEvent.getDragboard().getString().equals(card.getName())) {
                            isCheating = false;
                        }
                    }
                    Card card = CardFactory.getCard(dragEvent.getDragboard().getString());
                    if (card == null || card.getCost() > model.getPlayer().getActionPoints() || isCheating) {
                        animateText(ActionSummary, EndTurnButton, "Stop cheating! That doesn't work.");
                    } else {
                        Card playedCard = CardFactory.getCard(dragEvent.getDragboard().getString());
                        attack(playedCard, enemy);
                    }
                    dragEvent.consume();
                }
            });
            EnemyHolder.getChildren().add(enemyHolder);
        }
    }

    /**
     * Updates the views (player and all enemies) in the enemy holder by removing all dead beings on the battlefield.
     * Dead beings are faded out.
     */
    private void updateMobDisplay() {
        Set<String> enemyNames = new HashSet<>();
        for (Enemy enemy : battleManager.getEnemies()) {
            enemyNames.add(enemy.getName());
        }
        Iterator<Node> nodeIterator = EnemyHolder.getChildren().iterator();
        int index = 0;
        while (nodeIterator.hasNext()) {
            Node node = nodeIterator.next();
            if (node instanceof StackPane) {
                String nameOfBeing = ((Text) ((StackPane) node).getChildren().get(1)).getText();
                if (!nameOfBeing.equals(model.getPlayer().getName()) && !enemyNames.contains(nameOfBeing)) {
                    FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), node);
                    fadeOut.setFromValue(1.0);
                    fadeOut.setToValue(0.0);
                    int finalIndex = index;
                    fadeOut.setOnFinished(actionEvent -> {
                        EnemyHolder.getChildren().remove(finalIndex);
                    });
                    fadeOut.play();
                }
            }
            index++;
        }
    }

    /**
     * Creates and places the player view into the holder.
     */
    public void resetPlayerDisplay() {
        Rectangle playerOutline = getOutline();
        Text name = new Text(model.getPlayer().getName());
        name.setFont(new Font(20));
        StackPane playerHolder = new StackPane();
        playerHolder.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                if (dragEvent.getDragboard().hasString()) {
                    if (CardFactory.getCard(dragEvent.getDragboard().getString()).getDamage() == 0) {
                        dragEvent.acceptTransferModes(TransferMode.ANY);
                    }
                }
                dragEvent.consume();
            }
        });
        playerHolder.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                Card playedCard = CardFactory.getCard(dragEvent.getDragboard().getString());
                attack(playedCard, battleManager.getEnemies().get(0));
                dragEvent.consume();
            }
        });
        playerHolder.getChildren().addAll(playerOutline, name);
        EnemyHolder.getChildren().add(playerHolder);
        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        EnemyHolder.getChildren().add(region);
    }

    /**
     * Resets the cards to be displayed on the bottom bar of the battle screen.
     *
     * @param cards The cards to be displayed.
     */
    public void resetCardDisplay(List<Card> cards) {
        CardHolder.getChildren().clear();
        CardHolder.getChildren().add(LSpacer);
        for (Card card : cards) {
            final int PADDING = 5;
            Rectangle cardOutline = getOutline();

            Text description = new Text(card.getDescription(model.getPlayer()));
            description.setWrappingWidth(cardOutline.getWidth() - 2 * PADDING);
            description.setTextAlignment(TextAlignment.CENTER);
            description.setFont(new Font(10));

            Text cardTitle = new Text(card.getName());
            cardTitle.setTextAlignment(TextAlignment.CENTER);
            cardTitle.setFont(new Font(20));
            cardTitle.setWrappingWidth(cardOutline.getWidth() - 2 * PADDING);
            VBox button = new VBox(cardTitle);
            button.setPrefSize(cardOutline.getWidth() - 2 * PADDING, cardOutline.getHeight() - 4 * PADDING);

            if (card.getCost() <= model.getPlayer().getActionPoints()) {
                button.setCursor(Cursor.OPEN_HAND);
            } else {
                button.setCursor(Cursor.DEFAULT);
            }

            button.setMaxSize(3000, 3000);
            button.setAlignment(Pos.CENTER);

            Region region = new Region();
            VBox.setVgrow(region, Priority.SOMETIMES);
            button.getChildren().add(region);
            button.getChildren().add(description);
            StackPane cardHolder = new StackPane();
            cardHolder.getChildren().addAll(cardOutline, button);
            cardHolder.setOnDragDetected(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if (model.getPlayer().getActionPoints() >= card.getCost()) {
                        Dragboard dragboard = cardHolder.startDragAndDrop(TransferMode.ANY);
                        ClipboardContent content = new ClipboardContent();
                        content.putString(card.getName());
                        dragboard.setContent(content);
                    }
                    mouseEvent.consume();
                }
            });

            CardHolder.getChildren().add(cardHolder);
        }
        CardHolder.getChildren().addAll(EndTurnButton, RSpacer);
    }

    /**
     * @return a rectangular outline of the enemy view, card view, or player view.
     */
    private Rectangle getOutline() {
        Rectangle rectangle = new Rectangle();
        rectangle.setWidth(100);
        rectangle.setHeight(160);
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.setStroke(Color.BLACK);
        return rectangle;
    }
}
