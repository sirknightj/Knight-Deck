import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the driver class of the program.
 */
public class Main {

    public static final boolean DEBUGSTATS = false; //Displays debug information about the game.
    private static Player player; //The player.

    public static void main(String[] args) {
        System.out.println("=== Knight Deck ===");

        loadCards(new Gson(), "cards.json");
        loadEnemies(new Gson(), "enemies.json");

        if (DEBUGSTATS) { // to see if all the cards and enemies initialized correctly
            System.out.println("List of all cards:");
            for (Card card : CardFactory.getAllCards()) {
                System.out.println("\t" + card.toString());
            }
            System.out.println("List of all enemies:");
            for (Enemy e : EnemyFactory.getAllEnemies()) {
                System.out.println("\t" + e.toString());
            }
        }

        // Creating a new player
        player = new Player("Admin", 50, 2, testDeck());
        if (DEBUGSTATS) { // checking if the player has initialized correctly
            System.out.println("Your deck is as follows:");
            for (Card card : player.getDeck()) {
                System.out.println("\t" + card.getDescription());
            }
        }

        // Adding the enemies to battle
        List<Enemy> enemies = new ArrayList<>();
        enemies.add(EnemyFactory.getEnemy("Beast"));

        // Battle
        Battle battle = new Battle(player, enemies);
        battle.start();
    }

    /**
     * Loads all cards into CardFactory from data file
     *
     * @param gson     Gson instance
     * @param dataFile Name of the JSON resource file containing card data
     * @throws RuntimeException iff the cards failed to load.
     */
    private static void loadCards(Gson gson, String dataFile) {
        try {
            Reader cardFile = Files.newBufferedReader(Paths.get(Main.class.getResource(dataFile).toURI()));
            List<Card> cards = gson.fromJson(cardFile, new TypeToken<List<Card>>() {
            }.getType());
            for (Card card : cards) {
                CardFactory.addCard(card);
            }
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException("Failed to load cards");
        }
    }

    /**
     * Loads all enemies into EnemyFactory from data file
     *
     * @param gson     Gson instance
     * @param dataFile Name of the JSON resource file containing enemy data
     * @throws RuntimeException iff the the enemies failed to load.
     */
    private static void loadEnemies(Gson gson, String dataFile) {
        try {
            Reader enemyFile = Files.newBufferedReader(Paths.get(Main.class.getResource(dataFile).toURI()));
            List<Enemy> enemies = gson.fromJson(enemyFile, new TypeToken<List<Enemy>>() {
            }.getType());
            for (Enemy enemy : enemies) {
                List<Card> newDeck = new ArrayList<>();
                for (String invalidCard : enemy.getUnofficialDeck()) {
                    newDeck.add(CardFactory.getCard(invalidCard));
                }
                enemy.setDeck(newDeck);
                EnemyFactory.addEnemy(enemy);
            }
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException("Failed to load enemies");
        }
    }

    /**
     * @return A preset card deck for testing purposes.
     */
    private static List<Card> testDeck() {
        List<Card> playerDeck = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            playerDeck.add(CardFactory.getCard("Stab"));
        }
        playerDeck.add(CardFactory.getCard("Smash"));
        playerDeck.add(CardFactory.getCard("Defensive Stance"));
        playerDeck.add(CardFactory.getCard("Block"));
        playerDeck.add(CardFactory.getCard("Double Tap"));
        return playerDeck;
    }
}
