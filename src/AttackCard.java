/**
 * This contains all the elements of an attack card.
 */
public class AttackCard extends Card {
    private int damage;
    private int times;

    /**
     * Constructor. Creates a new AttackCard from its name, description, cost, and damage.
     * Assumes that the attack is only performed once.
     * @param name          The name of the card.
     * @param description   The description of the card.
     * @param cost          The cost to play this cad.
     * @param damage        The damage the card deals.
     */
    public AttackCard(String name, String description, int cost, int damage) {
        super(name, description, cost);
        this.damage = damage;
        this.times = 1;
    }

    /**
     * Constructor. Creates a new AttackCard from its name, description, damage, and number
     * of attacks it will perform.
     * @param name          The name of the card.
     * @param description   The description of the card.
     * @param cost          The cost to play this cad.
     * @param damage        The damage the card deals.
     * @param times         The number of attacks that will occur.
     */
    public AttackCard(String name, String description, int cost, int damage, int times) {
        super(name, description, cost);
        this.damage = damage;
        this.times = times;
    }

    /**
     * This tells the being to take damage.
     * @param being     The being targeted by the card.
     */
    @Override
    public void play(Being being) {
        being.takeDamage(damage * times);
    }
}