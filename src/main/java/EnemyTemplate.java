import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a "type" of Enemy. Can use .create() to construct an enemy from this template.
 */
public class EnemyTemplate {
    public final String name; // the name of the enemy
    public final List<String> deck; // names of the deck
    public final List<String> cardDrops; // names of the cards it can drop
    public final int cost; // the cost to deploy this enemy on the battlefield
    public final int gold; // the maximum gold dropped on defeat

    @SerializedName(value = "health")
    public final int maxHealth;

    @SerializedName(value = "actionPoints")
    public final int maxActionPoints;

    /**
     * Constructor.
     */
    public EnemyTemplate(String name, List<String> cardDrops, int maxHealth, int maxActionPoints, List<String> deck, int cost, int gold) {
        this.name = name;
        this.cardDrops = cardDrops;
        this.maxHealth = maxHealth;
        this.maxActionPoints = maxActionPoints;
        this.deck = deck;
        this.cost = cost;
        this.gold = gold;
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
        List<Card> cardDrop = new ArrayList<>();
        for (String cardName : cardDrops) {
            cardDrop.add(CardFactory.getCard(cardName));
        }
        return new Enemy(name, maxHealth, maxActionPoints, cards, cost, gold, cardDrop);
    }

    /**
     * @return String representation of this template, including name, health, action points, deck, cost, and drops
     */
    public String toString() {
        return name + ": " + maxHealth + " HP, " + maxActionPoints + " AP. Deck: " + deck + ". Cost: " + cost + ", Gold " + gold + ", cardDrops " + cardDrops;
    }
}