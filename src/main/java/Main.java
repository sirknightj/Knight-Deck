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

    private static Player player; //The player.

    public static void main(String[] args) {
        System.out.println("=== Knight Deck ===");

        loadCards(new Gson(), "cards.json");

        System.out.println("List of cards: ");
        for (Card card : CardFactory.getAllCards()) {
            System.out.println("\t" + card.toString());
        }

        // Creating a new player
        player = new Player("Admin", 50, 2, testDeck());
        System.out.println(player.getDeck());

        // Initializing the bear's cards
        List<Card> bearCards = new ArrayList<>();
        bearCards.add(CardFactory.getCard("Slash"));
        bearCards.add(CardFactory.getCard("Charge"));
        Enemy enemy = new Enemy("Bear", 40, 2, bearCards);

        // Adding the bear to the enemy list
        List<Enemy> enemies = new ArrayList<>();
        enemies.add(enemy);

        // Battle
        Battle battle = new Battle(player, enemies);
        battle.start();
    }

    /**
     * Loads all cards into CardFactory from data file
     * @param gson      Gson instance
     * @param dataFile  Name of the JSON resource file containing card data
     */
    private static void loadCards(Gson gson, String dataFile) {
        try {
            Reader attackCardFile = Files.newBufferedReader(Paths.get(Main.class.getResource(dataFile).toURI()));
            List<Card> cards = gson.fromJson(attackCardFile, new TypeToken<List<Card>>() {}.getType());
            for (Card card : cards) {
                CardFactory.addCard(card);
            }
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException("Failed to load cards");
        }
    }

    /**
     * @return  A preset card deck for testing purposes.
     */
    private static List<Card> testDeck() {
        List<Card> playerDeck = new ArrayList<>();
        for(int i = 0; i < 3; i++) {
            playerDeck.add(CardFactory.getCard("Stab"));
        }
        playerDeck.add(CardFactory.getCard("Smash"));
        playerDeck.add(CardFactory.getCard("Defensive Stance"));
        playerDeck.add(CardFactory.getCard("Block"));
        playerDeck.add(CardFactory.getCard("Double Tap"));
        return playerDeck;
    }
}
