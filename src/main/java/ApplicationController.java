import javafx.animation.*;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Contains helpful methods which are useful in the various screen controllers.
 */
public class ApplicationController {

    protected static GameModel model; // manages all of the game data.

    /**
     * Makes the dialogue inside the textbox seem like the person's talking. Also disables the Next button
     * while the animation is running.
     *
     * @param textbox  the textbox to animate.
     * @param next     the button to disable during the duration of the animation.
     * @param dialogue the dialogue to put in the textbox.
     */
    protected void animateText(Text textbox, Button next, String dialogue) {
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
     * Makes the dialogue inside the textbox seem like the person's talking.
     *
     * @param textbox  the textbox to animate.
     * @param dialogue the dialogue to put in the textbox.
     */
    protected void animateText(Text textbox, String dialogue, EventHandler<ActionEvent> onFinished, int pauseDurationBeforeOnFinished) {
        textbox.setText("");
        final IntegerProperty i = new SimpleIntegerProperty(0);
        Timeline timeline = new Timeline();
        KeyFrame keyFrame = new KeyFrame(
                Duration.millis(30),
                event -> {
                    if (i.get() > dialogue.length()) {
                        timeline.stop();
                    } else {
                        textbox.setText(dialogue.substring(0, i.get()));
                        i.set(i.get() + 1);
                    }
                });
        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(dialogue.length());
        timeline.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                PauseTransition pause = new PauseTransition(Duration.seconds(pauseDurationBeforeOnFinished));
                pause.setOnFinished(onFinished);
                pause.play();
            }
        });
        timeline.play();
    }

    /**
     * Animates the textbox to translate downwards and fade out.
     *
     * @param text the textbox to be animated.
     */
    protected void floatingText(Text text) {
        TranslateTransition transition = new TranslateTransition(Duration.seconds(1), text);
        transition.setByY(100);

        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), text);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);

        ParallelTransition parallelTransition = new ParallelTransition(transition, fadeTransition);
        parallelTransition.play();
    }

    /**
     * Returns the current stage.
     *
     * @param Event the event that occurred on the stage.
     * @return the stage the event occurred on.
     */
    protected Stage getStage(ActionEvent Event) {
        return (Stage) ((Node) Event.getSource()).getScene().getWindow();
    }
}
