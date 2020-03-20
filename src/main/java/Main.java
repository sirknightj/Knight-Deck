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

        Gson gson = new Gson();
        try {
            loadAttackCards(gson);
        } catch (URISyntaxException | IOException e) {
            System.err.println("Failed to load attack cards");
        }

        System.out.println("List of cards: ");
        for (Card card : CardFactory.getAllCards()) {
            System.out.println("\t" + card.toString());
        }

        // Creating a new player
        player = new Player("Admin", 50, 2, testDeck(), 3);
        System.out.println(player.getDeck());

        // Initializing the bear's cards
        List<Card> bearCards = new ArrayList<>();
        bearCards.add(CardFactory.getAttackCard("Double tap"));
        Enemy enemy = new Enemy("Bear", 40, 1, bearCards);

        // Adding the bear to the enemy list
        List<Enemy> enemies = new ArrayList<>();
        enemies.add(enemy);

        // Battle
        Battle battle = new Battle(player, enemies);
        battle.start();
    }

    /**
     * Loads all attack cards into CardFactory from data file
     * @param gson Gson instance
     * @throws URISyntaxException
     * @throws IOException
     */
    private static void loadAttackCards(Gson gson) throws URISyntaxException, IOException {
        // file containing attack card data
        Reader attackCardFile = Files.newBufferedReader(Paths.get(Main.class.getResource("attack.json").toURI()));

        List<AttackCard> attackCards = gson.fromJson(attackCardFile, new TypeToken<List<AttackCard>>() {}.getType());

        for (AttackCard card : attackCards) {
            CardFactory.addCard(card);
        }
    }

    /**
     * @return  A preset card deck for testing purposes.
     */
    private static List<Card> testDeck() {
        List<Card> playerDeck = new ArrayList<>();
        for(int i = 0; i < 6; i++) {
            playerDeck.add(CardFactory.getAttackCard("Stab"));
        }
        playerDeck.add(CardFactory.getAttackCard("Smash"));
        playerDeck.add(CardFactory.getAttackCard("Double tap"));
        return playerDeck;
    }
}
