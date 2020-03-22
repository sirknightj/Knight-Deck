import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * This is the driver class of the program.
 */
public class Main {
    public static final boolean DEBUGSTATS = true; //Displays debug information about the game.
    private static Player player; // The player.
    private static double difficulty; // The difficulty
    public static final int BATTLEFIELD_SIZE = 3; // the maximum number of enemies on the battlefield
    public static final double DROP_CHANCE = 0.4; // the chance that the enemy will drop a card for the player to find.

    public static void main(String[] args) {
        System.out.println("=== Knight Deck ===");
        difficulty = 1.9;
        loadCards("cards.json");
        loadEnemies("enemies.json");

        if (DEBUGSTATS) { // to see if all the cards and enemies initialized correctly
            System.out.println("List of all cards:");
            for (Card card : CardFactory.getAllCards()) {
                System.out.println("\t" + card.toString());
            }
            System.out.println("List of all enemies:");
            for (EnemyTemplate e : EnemyFactory.getAllEnemies()) {
                System.out.println("\t" + e.toString());
            }
        }

        // Creating a new player
        player = new Player("Admin", 50, 3, testDeck());
        if (DEBUGSTATS) { // checking if the player has initialized correctly
            System.out.println("Your deck is as follows:");
            for (Card card : player.getDeck()) {
                System.out.println("\t" + card.getDescription());
            }
        }

        Scanner input = new Scanner(System.in);
        String response = "";
        while (!response.equalsIgnoreCase("q")) {
            if (DEBUGSTATS) {
                System.out.println("Difficulty " + difficulty);
            }
            System.out.println("What would you like to do?");
            System.out.println("\t" + "b to battle");
            System.out.println("\t" + "h to visit the field hospital");
            System.out.println("\t" + "s to check out the shop");
            System.out.println("\t" + "q to quit");
            System.out.print("Action> ");
            response = input.nextLine().toLowerCase();
            System.out.println();
            if (response.equals("b")) {
                toBattle();
            } else if (response.equals("h")) {
                visitHospital();
            } else if (response.equals("s")) {
                System.out.println("You have " + player.getGold() + " gold.");
                System.out.println("Coming soon.");
            } else if (response.equals("q")) {
                System.out.println("Thanks for playing Knight Deck!");
            } else {
                System.out.println("Unrecognizable input.");
            }
            System.out.println();
        }
    }

    /**
     * Loads all cards into CardFactory from data file
     *
     * @param dataFile Name of the JSON resource file containing card data
     * @throws RuntimeException iff the cards failed to load.
     */
    private static void loadCards(String dataFile) {
        try {
            Reader cardFile = Files.newBufferedReader(Paths.get(Main.class.getResource(dataFile).toURI()));
            List<Card> cards = new Gson().fromJson(cardFile, new TypeToken<List<Card>>() {
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
     * @param dataFile Name of the JSON resource file containing enemy data
     * @throws RuntimeException iff the the enemies failed to load.
     */
    private static void loadEnemies(String dataFile) {
        try {
            Reader enemyFile = Files.newBufferedReader(Paths.get(Main.class.getResource(dataFile).toURI()));

            List<EnemyTemplate> enemies = new Gson().fromJson(enemyFile, new TypeToken<List<EnemyTemplate>>() {
            }.getType());
            for (EnemyTemplate enemy : enemies) {
                EnemyFactory.addEnemyTemplate(enemy);
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

    /**
     * Random enemy selection, and starts the battle.
     */
    private static void toBattle() {
        // Adding the enemies to battle
        List<Enemy> enemies = new ArrayList<>();
        int costOfThisField = 0;
        int end = 0;
        Object[] enemyList = EnemyFactory.getAllEnemies().toArray();
        while (enemies.isEmpty() || end < 10) {
            Enemy enemy = ((EnemyTemplate) enemyList[(int) (Math.random() * enemyList.length)]).create();
            if (enemy.getCost() + costOfThisField <= difficulty) {
                enemies.add(enemy);
                costOfThisField += enemy.getCost();
                end = 0;
            }
            if (enemies.size() >= BATTLEFIELD_SIZE) {
                break;
            }
            end++;
        }

        // Battle
        Battle battle = new Battle(player, enemies);
        battle.start();

        // Post-battle
        difficulty *= 1.24;
    }


    /**
     * Driver for when the player visits the hospital.
     */
    private static void visitHospital() {
        System.out.println("Cleric: Welcome to the field hospital. For now, I'll heal you for free.");
        System.out.println("\tCleric used heal.");
        player.heal(player.getMaxHealth() - player.getHealth());
        System.out.println(player.healthStatus());
    }
}
