import java.util.*;

/**
 * Creates Enemies
 */
public class EnemyFactory {
    private static Map<String, EnemyTemplate> nameToEnemy = new HashMap<>();

    public static void addEnemyTemplate(EnemyTemplate enemy) {
        nameToEnemy.put(enemy.name.toLowerCase(), enemy);
    }

    /**
     * @return Set of all Enemies
     */
    public static Set<EnemyTemplate> getAllEnemies() {
        return new HashSet<>(nameToEnemy.values());
    }

    /**
     * Returns a new enemy with the given name.
     *
     * @param name Name (type) of the enemy to construct
     * @return New enemy of the given type, null if enemy not found
     */
    public static Enemy getEnemy(String name) {
        EnemyTemplate template = nameToEnemy.get(name.toLowerCase());
        return (template == null) ? null : template.create();
    }
}
