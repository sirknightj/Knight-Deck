/**
 * Immutable representation of an attack card.
 */
public class AttackCard extends Card {
    private int damage; // damage per hit
    private int hits; // number of attacks

    /**
     * Constructor.
     * @param name      Name of card
     * @param cost      Cost to play card
     * @param damage    Damage of each hit
     * @param hits      The number of hits the card performs
     */
    public AttackCard(String name, int cost, int damage, int hits) {
        super(name, cost);
        this.damage = damage;
        this.hits = hits;
    }

    /**
     * @return damage value of each hit
     */
    public int getDamage() {
        return damage;
    }

    /**
     * @return number of hits the card performs
     */
    public int getHits() {
        return hits;
    }

    /**
     * Causes this card to damage the given Being.
     * @param being     Being targeted by the card
     */
    @Override
    public void play(Being being) {
        being.takeDamage(getDamage() * getHits());
    }

    @Override
    public String getDescription() {
        return "Deals " + getDamage() + (hits != 1 ? " x " + hits : "") + " damage.";
    }

    @Override
    public String toString() {
        return super.toString() + " [Damage = " + getDamage() + "x" + hits + "]";
    }
}