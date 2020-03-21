import java.util.ArrayList;
import java.util.List;

public class Enemy extends Being {

    private int maxHealth;
    private List<Card> deck;

    /**
     * Constructor.
     * @param name  The name of the enemy.
     * @param maxHealth The max health of the enemy.
     * @param maxActionPoints   The max action points of the enemy.
     * @param deck  The deck the enemy has.
     */
    public Enemy(String name, int maxHealth, int maxActionPoints, List<Card> deck) {
        super(name, maxHealth, maxActionPoints, 0);
        this.maxHealth = maxHealth;
        this.deck = new ArrayList<>(deck);
        assert(!deck.isEmpty());

        // make sure each card is a valid enemy card
        for (Card card : deck) {
            assert(card.isPlayableBy(this));
        }
    }

    /**
     * @return the Card for this Enemy to play. Returns null if it cannot find one
     */
    public Card chooseCard() {
        if (actionPoints > 0) {
            for (int endTurn = 0; endTurn < 10; endTurn++) {
                Card card = getRandomCard();
                if (card.getCost() <= actionPoints) {
                    return card;
                }
            }
        }
        return null;
    }

    /**
     * Plays the given card against the given target. If card is solely defensive, the target does not matter.
     * @param card      Card to play
     * @param target    Target to attack (should be a Player?)
     */
    public void playCard(Card card, Being target) {
        card.play(this, target);
        actionPoints -= card.getCost();
        assert(actionPoints >= 0);
    }

    /**
     * @return a random card in this enemy's deck.
     */
    public Card getRandomCard() {
        return deck.get((int) (Math.random() * deck.size()));
    }

    @Override
    public List<Card> getDeck() {
        return new ArrayList<>(deck);
    }

    @Override
    public void takeDamage(int damage, int hits) {
        assert(damage > 0);
        assert(hits > 0);
        damage = Math.max(damage - defense, 0);
        health = Math.max(health - damage * hits, 0);
    }

    @Override
    public String healthStatus() {
        if (health > 1) {
            return name + " has " + health + "/" + maxHealth + " health and " + defense + " defense";
        } else {
            return name + " is dead.";
        }
    }

    /**
     * @return the name of the enemy, along with its health status
     */
    public String toString() {
        return name + ": " + healthStatus();
    }
}
