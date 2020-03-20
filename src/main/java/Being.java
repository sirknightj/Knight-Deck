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
    protected int defense;

    /**
     * Constructor. Also sets actionPoints and health to their respective maximum values.
     * @param name the name of the being.
     * @param maxHealth the maximum health of the being.
     * @param maxActionPoints the maximum action points this being can perform, per turn.
     * @param deck the assortment of cards available to the being.
     */
    public Being(String name, int maxHealth, int maxActionPoints, List<Card> deck) {
        this.name = name;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.maxActionPoints = maxActionPoints;
        this.actionPoints = maxActionPoints;
        this.deck = deck;
        this.defense = 0;
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
     * @return  The current defense this being has.
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
     * This being takes damage.
     * @param damage    The damage this takes.
     */
    public void takeDamage(int damage) {
        health -= damage;
    }
}
