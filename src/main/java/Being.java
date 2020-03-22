import java.util.ArrayList;
import java.util.List;

/**
 * This contains the elements of a basic Being.
 */
public abstract class Being {
    protected String name;
    protected int maxHealth;
    protected int health;
    protected int actionPoints;
    protected int maxActionPoints;
    protected int defense;
    protected List<Card> deck;

    /**
     * Constructor. Also sets action points and health to their respective maximum values.
     *
     * @param name            the name of the being.
     * @param maxHealth       the maximum health of the being.
     * @param maxActionPoints the maximum action points this being can perform, per turn.
     * @param deck            the deck that the being has
     */
    public Being(String name, int maxHealth, int maxActionPoints, List<Card> deck) {
        this.name = name;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.actionPoints = maxActionPoints;
        this.maxActionPoints = maxActionPoints;
        this.deck = new ArrayList<>(deck);
        this.defense = 0;
    }

    /**
     * @return The name of the being.
     */
    public String getName() {
        return name;
    }

    /**
     * @return The health of the being.
     */
    public int getHealth() {
        return health;
    }

    /**
     * @return The action points of the being.
     */
    public int getActionPoints() {
        return actionPoints;
    }

    /**
     * @return The maximum action points of the being.
     */
    public int getMaxActionPoints() {
        return maxActionPoints;
    }

    /**
     * Resets the player's action points to their max.
     */
    public void resetActionPoints() {
        actionPoints = maxActionPoints;
    }

    /**
     * @return The current defense this being has.
     */
    public int getDefense() {
        return defense;
    }

    /**
     * @param defense The current defense this being has.
     */
    public void setDefense(int defense) {
        this.defense = defense;
    }

    /**
     * @param damage Damage per hit against being
     * @param hits   Number of attacks
     */
    public void takeDamage(int damage, int hits) {
        assert (damage > 0);
        assert (hits > 0);
        damage = Math.max(damage - defense, 0);
        health = Math.max(health - damage * hits, 0);
    }

    /**
     * @return The health / maxHealth of the being in String form.
     */
    public String healthStatus() {
        if (health > 0) {
            return name + " has " + health + "/" + maxHealth + " health" + ((defense > 0) ? " and " + defense + " defense." : ".");
        } else {
            return name + " is dead.";
        }
    };

    /**
     * @return The deck of the current being.
     */
    public List<Card> getDeck() {
        return new ArrayList<>(deck);
    }

    @Override
    public String toString() {
        return name + ": " + health + "/" + maxHealth + ". Deck: " + deck;
    }
}
