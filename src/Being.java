/**
 * This contains all of the elements of a being: player or enemy.
 */
public abstract class Being {
    private String name;
    private int health;

    /**
     * The player takes damage.
     * @param damage    The damage this takes.
     */
    public void takeDamage(int damage) {
        health -= damage;
    }
}
