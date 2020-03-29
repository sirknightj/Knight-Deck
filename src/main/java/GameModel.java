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
 * Model for the entire game state.
 */
public class GameModel {
    private Player player; // The player

    private static final double STARTING_DIFFICULTY = 1.22;
    private double difficulty; // The difficulty

    public static final int BATTLEFIELD_SIZE = 3; // the maximum number of enemies on the battlefield
    public static final double DROP_CHANCE = 0.4; // the chance that the enemy will drop a card for the player to find.

    private static final String CARDS_DATA_FILE = "cards.json";
    private static final String ENEMIES_DATA_FILE = "enemies.json";

    public GameModel() {
        loadCards();
        loadEnemies();

        assert !CardFactory.getAllCards().isEmpty();
        assert !EnemyFactory.getAllEnemies().isEmpty();

        Shop.setVisitable();
    }

    public void loadPlayer(String playerName) {
        SaveState save = getSaveState(playerName);
        if (save != null) { // Found a save
            difficulty = save.getDifficulty();
            player = save.constructPlayer();
        } else { // No save
            player = new Player(playerName, 50, 3, getInitialDeck(), 4);
            difficulty = STARTING_DIFFICULTY;
            saveData();
        }
    }

    public void saveData() {
        player.sortDeck();
        makeSaveState(player, difficulty);
    }

    public BattleManager startBattle(double battleFieldStamina) {
        // Adding the enemies to battle
        List<Enemy> enemies = new ArrayList<>();
        double costOfThisField = 0;
        List<EnemyTemplate> enemyList = new ArrayList<>(EnemyFactory.getAllEnemies());

        for (int i = 0; i < 10 || enemies.isEmpty(); i++) {
            Enemy enemy = enemyList.get((int) (Math.random() * enemyList.size())).create();
            if (enemy.getCost() + costOfThisField <= battleFieldStamina) {
                enemies.add(enemy);
                costOfThisField += enemy.getCost();
                i = 0;
            }
            if (enemies.size() >= BATTLEFIELD_SIZE) {
                break;
            }
        }

        return new BattleManager(player, enemies);
    }
    public BattleManager startFinalBattle() {
        List<Enemy> enemies = new ArrayList<>();
        enemies.add(EnemyFactory.getEnemy("Beast"));
        enemies.add(EnemyFactory.getEnemy("Knight"));
        enemies.add(EnemyFactory.getEnemy("Wizard"));

        return new BattleManager(player, enemies);
    }


    public Player getPlayer() {
        return player;
    }

    public double getDifficulty() {
        return difficulty;
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
        String hash;
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
     * Loads all cards into CardFactory from data file
     *
     * @throws RuntimeException iff the cards failed to load.
     */
    private void loadCards() {
        try {
            Reader cardFile = Files.newBufferedReader(Paths.get(getClass().getResource(GameModel.CARDS_DATA_FILE).toURI()));
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
     * @throws RuntimeException iff the the enemies failed to load.
     */
    private void loadEnemies() {
        try {
            Reader enemyFile = Files.newBufferedReader(Paths.get(getClass().getResource(GameModel.ENEMIES_DATA_FILE).toURI()));

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
     * @return A preset card deck that the player initially has.
     */
    private static List<Card> getInitialDeck() {
        List<Card> deck = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            deck.add(CardFactory.getCard("Stab"));
            deck.add(CardFactory.getCard("Shield"));
        }
        deck.add(CardFactory.getCard("Smash"));
        deck.add(CardFactory.getCard("Defensive Stance"));
        deck.add(CardFactory.getCard("Block"));
        deck.add(CardFactory.getCard("Double Tap"));
        deck.add(CardFactory.getCard("Sickle"));
        deck.add(CardFactory.getCard("Scythe"));
        Collections.sort(deck);
        return deck;
    }
}
