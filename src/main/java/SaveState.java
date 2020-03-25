import java.util.List;
import java.util.stream.Collectors;

/**
 * Serializable/Deserializable save state.
 */
public class SaveState {
    public String name;
    public int health;
    public int maxHealth;
    public int maxActionPoints;
    public int gold;
    public List<String> deck;
    public double difficulty;
    public boolean discardActionDeck;

    /**
     * Creates a save state from the given Player.
     * @param player Player to save
     */
    public SaveState(Player player, double difficulty) {
        this.name = player.getName();
        this.health = player.getHealth();
        this.maxHealth = player.getMaxHealth();
        this.maxActionPoints = player.getMaxActionPoints();
        this.gold = player.getGold();
        this.deck = player.getDeck().stream().map(Card::getName).collect(Collectors.toList());
        this.difficulty = difficulty;
        this.discardActionDeck = player.isActionDeckDiscarded();
    }

    /**
     * Creates a new Player from this save state representation
     * @return new Player
     */
    public Player constructPlayer() {
        List<Card> cardDeck = deck.stream().map(CardFactory::getCard).collect(Collectors.toList());
        Player player = new Player(name, maxHealth, maxActionPoints, cardDeck, discardActionDeck);
        player.addGold(gold);
        player.setHealth(health);
        return player;
    }

    /**
     * @return difficulty from this save state
     */
    public double getDifficulty() {
        return difficulty;
    }
}
