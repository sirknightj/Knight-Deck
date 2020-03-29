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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Holds all of the game data.
 */
public class ApplicationModel {

    public Player player; // The player.
    private final double STARTING_DIFFICULTY = 1.22;
    private double difficulty; // The current difficulty
    public final int BATTLEFIELD_SIZE = 3; // the maximum number of enemies on the battlefield
    public final double DROP_CHANCE = 0.4; // the chance that the enemy will drop a card for the player to find.
    public final int TEXT_DELAY = 100; // the text delay in milliseconds.
    private int hospitalStatus; // the hospital dialogue.
    private static ApplicationController controller; // this controller.
//    public BattleManager battleManager;

    /**
     * Constructor. Initializes all of the resources.
     *
     * @throws RuntimeException if the resources fail to load.
     */
    public ApplicationModel(String name, ApplicationController controller) {
        try { // Loading the cards.
            Reader cardFile = Files.newBufferedReader(Paths.get(getClass().getResource("cards.json").toURI()));
            List<Card> cards = new Gson().fromJson(cardFile, new TypeToken<List<Card>>() {
            }.getType());
            for (Card card : cards) {
                CardFactory.addCard(card);
            }
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException("Failed to load cards");
        }
        try { // Loading the enemies.
            Reader enemyFile = Files.newBufferedReader(Paths.get(getClass().getResource("enemies.json").toURI()));

            List<EnemyTemplate> enemies = new Gson().fromJson(enemyFile, new TypeToken<List<EnemyTemplate>>() {
            }.getType());
            for (EnemyTemplate enemy : enemies) {
                EnemyFactory.addEnemyTemplate(enemy);
            }
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException("Failed to load enemies");
        }

        SaveState save = getSaveState(name);
        if (save != null) { // Found a save
            System.out.println("Found player " + name + ". Loaded from save file."); // TODO: Remove this line after testing is done.
            difficulty = save.getDifficulty();
            player = save.constructPlayer();
        } else { // No save
            player = new Player(name, 50, 3, defaultDeck(), 4);
            difficulty = STARTING_DIFFICULTY;
            makeSaveState(player, difficulty);
        }
        hospitalStatus = 0;
        this.controller = controller;
    }

    /**
     * @return the player this game is considering.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Resets the hospital dialogue status.
     */
    public void resetHospitalStatus() {
        hospitalStatus = 0;
    }

    /**
     * @return true iff it's time to switch to the yes/no boxes.
     */
    public boolean isItYesNoTime() {
        return hospitalStatus == 6;
    }

    public boolean isItLeavingTime() {
        return hospitalStatus == 11;
    }

    public String getNextHospitalText() {
        hospitalStatus++;
        if (hospitalStatus == 1) {
            return "Let me take a look at your wounds...";
        }
        if (hospitalStatus == 5) {
            return "It doesn't look like you have enough gold for me to heal you. It costs " + getHealingCost() + " gold, and you have " + player.getGold() + " gold.";
        }
        if (hospitalStatus == 6) {
            hospitalStatus = 10;
            return "I really would like to heal you, but I'm poor and don't have any extra supplies.";
        }
        if (hospitalStatus == 11) {
            return "Not saying you should get yourself injured, but come back when you need healing!";
        }
        if (player.getHealth() < player.getMaxHealth()) {
            if (player.getGold() > 1) {
                if (hospitalStatus == 2) {
                    return "...";
                } else if (hospitalStatus == 3) {
                    return "It looks like you're injured pretty badly.";
                } else if (hospitalStatus == 4) {
                    if (player.getGold() >= getHealingCost()) {
                        hospitalStatus = 6;
                        return "I can heal you all the way to full, but it'll cost you " + getHealingCost() + " gold. Deal?";
                    } else {
                        return getNextHospitalText();
                    }
                }
            } else { // player has no gold
                if (hospitalStatus == 2) {
                    hospitalStatus = 5;
                    return "Supplies are low right now. I need gold to heal you, and you have none.";
                }
            }
        } else if (hospitalStatus == 2) { // player is not injured
            hospitalStatus = 10;
            return "You don't appear to have any injuries.";
        }
        return null;
    }

    public int getHealingCost() {
        return (int) Math.ceil(Math.log(player.getMaxHealth() - player.getHealth()) / Math.log(1.4));
    }

    /**
     * Heals the player to full. Takes away the appropriate gold.
     */
    public void healPlayerToFull() {
        player.takeGold(getHealingCost());
        player.heal(player.getMaxHealth() - player.getHealth());
    }

    /**
     * Returns path to the save file of a player with the given name. There may be no file at this location.
     *
     * @param name Player name to get save file path of
     * @return relative path to where a save file of the player would be
     */
    private String getSaveFilePath(String name) {
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
    private SaveState getSaveState(String name) {
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
    private void makeSaveState(Player player, double difficulty) {
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
     * @return The preset deck.
     */
    private static List<Card> defaultDeck() {
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

    public void initializeBattle() {

    }


}
