import java.util.List;

/**
 * This contains all of the elements of a being: player or enemy.
 */
public abstract class Being {
    protected String name;
    protected int health;
    protected int maxHealth;
    protected List<Card> deck;
    protected int actionPoints;
    protected int maxActionPoints;

    public Being(String name, int maxHealth, int maxActionPoints, List<Card> deck) {
        this.name = name;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.maxActionPoints = maxActionPoints;
        this.actionPoints = maxActionPoints;
        this.deck = deck;
    }

    /**
     * @return  The name of the being.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name  The new name of the being.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return  The health of the being.
     */
    public int getHealth() {
        return health;
    }

    /**
     * @param health    The health of the being.
     */
    public void setHealth(int health) {
        this.health = health;
    }

    /**
     * @return  The maximum health of the being.
     */
    public int getMaxHealth() {
        return maxHealth;
    }

    /**
     * @param maxHealth The maximum health of the being.
     */
    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    /**
     * @return The deck of the current being.
     */
    public List<Card> getDeck() {
        return deck;
    }

    /**
     * @param deck  The deck of the current being.
     */
    public void setDeck(List<Card> deck) {
        this.deck = deck;
    }

    /**
     * @return  The action points of the being.
     */
    public int getActionPoints() {
        return this.actionPoints;
    }

    /**
     * @param actionPoints  The number of action points the being has.
     */
    public void setActionPoints(int actionPoints) {
        this.actionPoints = actionPoints;
    }

    /**
     * @return  The maximum action points the being has.
     */
    public int getMaxActionPoints() {
        return maxActionPoints;
    }

    /**
     * @param maxActionPoints   The maximum action points the being has.
     */
    public void setMaxActionPoints(int maxActionPoints) {
        this.maxActionPoints = maxActionPoints;
    }

    /**
     * @return  The health / maxHealth of the being in String form.
     */
    public String healthStatus() {
        if(health > 1) {
            return name + " has " + health + "/" + maxHealth + " health";
        } else {
            return name + " is dead.";
        }
    }

    /**
     * The player takes damage.
     * @param damage    The damage this takes.
     */
    public void takeDamage(int damage) {
        health -= damage;
    }
}
