/**
 * Immutable representation of a card.
 */
public class Card {
    private String type;
    private String name;
    private int cost;
    private boolean isEnemyOnly;
    private int damage;
    private int hits;
    private int defense;

    /**
     * Constructor. Creates a new card from its name and cost.
     * @param name        The name of the card.
     * @param cost        The cost to play this card.
     * @param isEnemyOnly True if only the enemy can play this card.
     */
    public Card(String type, String name, int cost, boolean isEnemyOnly, int damage, int hits, int defense) {
        this.type = type;
        this.name = name;
        this.cost = cost;
        this.isEnemyOnly = isEnemyOnly;
        this.damage = damage;
        this.hits = hits;
    }

    /**
     * @return The name of the card.
     */
    public String getName() {
        return name;
    }

    /**
     * @return The cost to play this card.
     */
    public int getCost() {
        return cost;
    }

    /**
     * @return damage value of each hit.
     */
    public int getDamage() {
        return damage;
    }

    /**
     * @return number of hits the card performs.
     */
    public int getHits() {
        return hits;
    }

    /**
     * @return True if the card can only be played by an enemy.
     */
    public boolean isEnemyOnly() {
        return isEnemyOnly;
    }

    /**
     * Causes this card to damage the given Being.
     * @param being Being targeted by the card.
     */
    public void play(Being being) {
        being.takeDamage(damage * hits);
    }

    /**
     * @return the card's name and its cost
     */
    public String toString() {
        return name + " [" + cost + "]" + " [Damage = " + damage + "x" + hits + "]";
    }

    /**
     * @return a description of how much damage this card deals.
     */
    public String getDescription() {
        return "Deals " + damage + (hits != 1 ? " x " + hits : "") + " damage.";
    }
}
