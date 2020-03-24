import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * This is the driver class of the program.
 */
public class Main {
    public static final boolean DEBUGSTATS = true; // Displays debug information about the game.
    private static Player player; // The player.
    private static final double STARTING_DIFFICULTY = 1.22;
    private static double difficulty; // The difficulty
    public static final int BATTLEFIELD_SIZE = 3; // the maximum number of enemies on the battlefield
    public static final double DROP_CHANCE = 0.4; // the chance that the enemy will drop a card for the player to find.
    public static final int TEXT_DELAY = 1400; // the text delay in milliseconds.

    public static void main(String[] args) {
        System.out.println("=== Knight Deck ===");
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

        Scanner input = new Scanner(System.in);

        System.out.println("What is your name? ");
        System.out.print("Name> ");
        String name = input.nextLine().trim();

        SaveState save = getSaveState(name);
        if (save != null) { // Found a save
            System.out.println("Found player " + name + ". Loaded from save file.");
            difficulty = save.getDifficulty();
            player = save.constructPlayer();
        } else { // No save
            player = new Player(name, 50, 3, testDeck());
            difficulty = STARTING_DIFFICULTY;
            makeSaveState(player, difficulty);
        }

        if (DEBUGSTATS) { // checking if the player has initialized correctly
            System.out.println("Your deck is as follows:");
            for (Card card : player.getDeck()) {
                System.out.println("\t" + card.getDescription());
            }
        }

        String response = "";
        while (!response.equalsIgnoreCase("q")) {
            if (DEBUGSTATS) {
                System.out.println("Difficulty " + difficulty);
            }
            System.out.println(player.healthStatus());
            System.out.println("You have " + player.getGold() + " gold.");
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
            player.sortDeck();
            makeSaveState(player, difficulty); // save after every action
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
     * Returns path to the save file of a player with the given name. There may be no file at this location.
     *
     * @param name Player name to get save file path of
     * @return relative path to where a save file of the player would be
     */
    private static String getSaveFilePath(String name) {
        // Returned filename is of the format prefix-hash.json, where prefix is part of the name,
        //   and hash is the SHA-1 of the full name. This ensures that there are no naming conflicts.

        // Calculate SHA-1 hash of name
        String hash = "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] messageDigest = md.digest(name.getBytes());
            hash = new BigInteger(1, messageDigest).toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error getting SHA-1 hash of " + name);
        }

        // Get the first 5 characters of the alphanumeric part of name
        String prefix = name.replaceAll("[^a-zA-Z0-9]", "");
        prefix = (prefix.length() > 5) ? prefix.substring(0, 5) : prefix;

        return "saves/" + prefix + "-" + hash + ".json";
    }

    /**
     * Returns the save state for the given player if it exists, else null
     *
     * @param name Name of player
     * @return SaveState for given name, null if player has not saved before.
     */
    private static SaveState getSaveState(String name) {
        String fileName = getSaveFilePath(name);
        File file = new File(fileName);
        if (file.exists() && !file.isDirectory()) {
            try {
                return new Gson().fromJson(new FileReader(fileName), new TypeToken<SaveState>() {
                }.getType());
            } catch (IOException e) {
                System.err.println("Error loading save file " + fileName);
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Stores the given Player and difficulty into a JSON save file.
     *
     * @param player     Player to store
     * @param difficulty Current difficulty to store
     */
    private static void makeSaveState(Player player, double difficulty) {
        SaveState save = new SaveState(player, difficulty);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String fileName = getSaveFilePath(player.getName());
        try (Writer writer = new FileWriter(fileName)) {
            gson.toJson(save, writer);
        } catch (IOException e) {
            System.err.println("Error saving data of " + player.getName() + " into " + fileName);
            System.err.println(Arrays.toString(e.getStackTrace()));
        }
    }

    /**
     * @return A preset card deck for testing purposes.
     */
    private static List<Card> testDeck() {
        List<Card> playerDeck = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            playerDeck.add(CardFactory.getCard("Stab"));
            playerDeck.add(CardFactory.getCard("Shield"));
        }
        playerDeck.add(CardFactory.getCard("Smash"));
        playerDeck.add(CardFactory.getCard("Defensive Stance"));
        playerDeck.add(CardFactory.getCard("Block"));
        playerDeck.add(CardFactory.getCard("Double Tap"));
        playerDeck.add(CardFactory.getCard("Sickle"));
        playerDeck.add(CardFactory.getCard("Scythe"));
        Collections.sort(playerDeck);
        return playerDeck;
    }

    /**
     * Selects random enemies, based on the difficulty, and starts the battle.
     */
    private static void toBattle() {
        // Adding the enemies to battle
        List<Enemy> enemies = new ArrayList<>();
        double costOfThisField = 0;
        Object[] enemyList = EnemyFactory.getAllEnemies().toArray();
        int end = 0;
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
        difficulty *= 1.22;
    }

    /**
     * Driver for when the player visits the hospital.
     */
    private static void visitHospital() {
        System.out.println("Cleric: Welcome to the field hospital.");
        textWait();
        if (player.getHealth() < player.maxHealth) {
            if (player.getGold() < 1) {
                System.out.println("Cleric: I'm sorry, but I'm greedy. I need gold to heal you.");
                textWait();
            } else {
                System.out.println("Cleric: Let me take a look at your wounds...");
                textWait();
                System.out.println("Cleric: ...");
                textWait();
                System.out.println("Cleric: It looks like you're injured pretty badly.");
                textWait();
                if (player.getGold() >= (int) Math.ceil(Math.log(player.getMaxHealth() - player.getHealth()) / Math.log(1.4))) {
                    System.out.println("Cleric: I can heal you all the way to full, but it'll cost you " + (int) Math.ceil(Math.log(player.getMaxHealth() - player.getHealth()) / Math.log(1.4)) + " gold (y/n).");
                    System.out.println("\t(You have " + player.getGold() + " gold.)");
                    System.out.print("> ");
                    Scanner input = new Scanner(System.in);
                    String response = input.nextLine();
                    while (!response.equals("y") && !response.equals("n")) {
                        System.out.println("Cleric: Sorry, I don't understand.");
                        System.out.print("> ");
                        response = input.nextLine();
                    }
                    if (response.equals("y")) {
                        System.out.println("Cleric: Get ready!");
                        textWait();
                        System.out.println("\t" + "Cleric used heal!");
                        textWait();
                        player.takeGold((int) Math.ceil(Math.log(player.getMaxHealth() - player.getHealth()) / Math.log(1.4)));
                        player.heal(player.getMaxHealth() - player.getHealth());
                        System.out.println("\t" + player.healthStatus());
                        textWait();
                    }
                } else {
                    System.out.println("Cleric: Sorry, but you don't have enough gold to cover treatment.");
                    textWait();
                    System.out.println("Cleric: I really would like to heal you, but I'm poor and don't have any extra supplies.");
                    textWait();
                }
            }
        } else {
            System.out.println("Cleric: You don't appear to have any injuries.");
            textWait();
        }
        if(player.getHealth() == player.getMaxHealth()) {
            System.out.println("Cleric: Not saying you should get yourself injured, but come back when you need healing!");
            textWait();
        } else {
            System.out.println("Cleric: Come back soon!");
            textWait();
        }
    }

    public static void textWait() {
        try {
            Thread.sleep(TEXT_DELAY);
        } catch (InterruptedException e) {
            System.out.println("Interrupted Exception!");
        }
    }
}
