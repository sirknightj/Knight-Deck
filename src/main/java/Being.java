import java.util.List;

/**
 * This contains the elements of a basic Being.
 */
public abstract class Being {
    protected String name;
    protected int health;
    protected int actionPoints;
    protected int maxActionPoints;
    protected int defense;

    /**
     * Constructor. Also sets action points and health to their respective maximum values.
     * @param name the name of the being.
     * @param maxHealth the maximum health of the being.
     * @param maxActionPoints the maximum action points this being can perform, per turn.
     * @param defense the defense of the enemy
     */
    public Being(String name, int maxHealth, int maxActionPoints, int defense) {
        this.name = name;
        this.health = maxHealth;
        this.actionPoints = maxActionPoints;
        this.maxActionPoints = maxActionPoints;
        this.defense = defense;
    }

    /**
     * @return  The name of the being.
     */
    public String getName() {
        return name;
    }

    /**
     * @return  The health of the being.
     */
    public int getHealth() {
        return health;
    }

    /**
     * @return  The action points of the being.
     */
    public int getActionPoints() {
        return this.actionPoints;
    }

    /**
     * Resets the player's action points to their max.
     */
    public void resetActionPoints() {
        actionPoints = maxActionPoints;
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
     * @param damage    Damage per hit against being
     * @param hits      Number of attacks
     */
    public abstract void takeDamage(int damage, int hits);

    /**
     * @return  The health / maxHealth of the being in String form.
     */
    public abstract String healthStatus();

    /**
     * @return The deck of the current being.
     */
    public abstract List<Card> getDeck();

    @Override
    public String toString() {
        return name;
    }
}
