import java.util.*;

/**
 * Creates Enemies
 */
public class EnemyFactory {
    private static Map<String, Enemy> nameToEnemy = new HashMap<>();

    public static void addEnemy(Enemy enemy) {
        nameToEnemy.put(enemy.getName().toLowerCase(), enemy);
    }

    /**
     * @return Set of all Enemies
     */
    public static Set<Enemy> getAllEnemies() {
        return new HashSet<>(nameToEnemy.values());
    }

    /**
     * Returns a new enemy with the given name.
     *
     * @param name Name of the enemy to construct
     * @return Enemy with the given name, null if enemy not found
     */
    public static Enemy getEnemy(String name) {
        Enemy found = nameToEnemy.get(name.toLowerCase());
        if (found != null) {
            found = new Enemy(found.getName(), found.getMaxHealth(), found.getMaxActionPoints(), found.getDeck());
        }
        return found;
    }

    /**
     * Returns a new enemy with the given name, but with a custom name.
     *
     * @param name Name of the enemy to construct
     * @return Enemy with the given name, null if enemy not found
     */
    public static Enemy getEnemy(String name, String newName) {
        Enemy found = nameToEnemy.get(name.toLowerCase());
        if (found != null) {
            found = new Enemy(newName, found.getMaxHealth(), found.getMaxActionPoints(), found.getDeck());
        }
        return found;
    }
}
