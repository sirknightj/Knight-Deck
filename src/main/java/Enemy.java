import java.util.List;

public class Enemy extends Being {

    /**
     * Constructor.
     * @param name  The name of the enemy.
     * @param maxHealth The max health of the enemy.
     * @param maxActionPoints   The max action points of the enemy.
     * @param deck  The deck the enemy has.
     */
    public Enemy(String name, int maxHealth, int maxActionPoints, List<Card> deck) {
        super(name, maxHealth, maxActionPoints, deck);
    }

    /**
     * @return a random card in the this enemy's deck.
     */
    public Card getRandomCard() {
        return deck.get((int)(Math.random() * deck.size()));
    }

    /**
     * @return the name of the enemy, along with its health status
     */
    public String toString() {
        return name + ": " + healthStatus();
    }
}
