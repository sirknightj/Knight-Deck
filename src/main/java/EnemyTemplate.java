import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a "type" of Enemy. Can use .create() to construct an enemy from this template.
 */
public class EnemyTemplate {
    public final String name;
    public final List<String> deck; // names of the deck

    @SerializedName(value = "health")
    public final int maxHealth;

    @SerializedName(value = "actionPoints")
    public final int maxActionPoints;

    /**
     * Constructor
     */
    public EnemyTemplate(String name, int maxHealth, int maxActionPoints, List<String> deck) {
        this.name = name;
        this.maxHealth = maxHealth;
        this.maxActionPoints = maxActionPoints;
        this.deck = deck;
    }

    /**
     * Uses this template to create a fully-healed Enemy with the properties of this EnemyTemplate.
     *
     * @return a new Enemy using this template
     */
    public Enemy create() {
        List<Card> cards = new ArrayList<>();
        for (String cardName : deck) {
            cards.add(CardFactory.getCard(cardName));
        }
        return new Enemy(name, maxHealth, maxActionPoints, cards);
    }

    /**
     * @return String representation of this template, including name, health, action points, and deck
     */
    public String toString() {
        return name + ": " + maxHealth + " HP, " + maxActionPoints + " AP. Deck: " + deck;
    }
}
