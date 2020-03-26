import java.util.*;

/**
 * Immutable representation of the player.
 */
public class Player extends Being {

    private int drawSize; // the number of cards the player starts off their turn with in the actionDeck.
    private Stack<Card> drawPile; // the cards the player is yet to draw.
    private List<Card> actionDeck; // the cards in the player's hand.
    private Stack<Card> discardPile; // the cards the player has already seen.
    private int gold; // the gold the player will stockpile and use

    /**
     * Constructor. Also fills the Player's health and action points to full.
     * @param name            The name of the player.
     * @param maxHealth       The maximum health of the player.
     * @param maxActionPoints The maximum actionPoints of the player.
     * @param deck            The deck the player starts with.
     * @param drawSize        the number of cards the player starts off their turn with in the actionDeck.
     */

    public Player(String name, int maxHealth, int maxActionPoints, List<Card> deck, int drawSize) {
        super(name, maxHealth, maxActionPoints, deck);

        // make sure each card is a valid player card
        for (Card card : deck) {
            assert (card.isPlayableBy(this));
        }

        this.drawSize = drawSize;
        gold = 0;
        drawPile = new Stack<>();
        actionDeck = new ArrayList<>();
        discardPile = new Stack<>();
    }

    /**
     * @return the gold in the player's inventory
     */
    public int getGold() {
        return gold;
    }

    /**
     * @param gold the amount of gold to add to the player's inventory, must be >= 0.
     */
    public void addGold(int gold) {
        assert gold >= 0;
        this.gold += gold;
    }

    /**
     * @param gold the amount of gold to be taken away from the player's inventory. The remaining
     *             gold must be >= 0.
     */
    public void takeGold(int gold) {
        this.gold -= gold;
        assert (gold >= 0);
        assert this.gold >= 0;
    }

    /**
     * Should be called before starting a battle.
     * Action deck is empty. Draw pile contains all player's cards shuffled. Discard pile is empty.
     */
    public void initializeDeck() {
        discardPile.clear();
        actionDeck.clear();
        drawPile.addAll(deck);
        Collections.shuffle(drawPile);
    }

    /**
     * Adds a card into the player's deck.
     *
     * @param card the card to be added into the player's deck.
     */
    public void deckAdd(Card card) {
        deck.add(card);
    }

    /**
     * Draws cards from the draw pile equal to the player's draw capacity, and puts them in the player's hand.
     * Or, if the player's action deck is not discarded, draw more cards up to the player's draw capacity.
     */
    public void drawCards() {
        for (int i = actionDeck.size(); i < drawSize; i++) {
            if (drawPile.isEmpty()) {
                // Reset this player's draw pile
                drawPile.addAll(discardPile);
                discardPile.clear();
                Collections.shuffle(drawPile);
            }

            Card card = drawPile.pop();
            actionDeck.add(card);
        }
    }

    /**
     * @return The number of cards the player should have in their action deck at the start of their turn.
     */
    public int getDrawSize() {
        return drawSize;
    }

    /**
     * Resets the player's draw pile.
     */
    private void resetDrawPile() {
        drawPile.addAll(discardPile);
        discardPile.clear();
        Collections.shuffle(drawPile);
    }

    /**
     * Increases the player's draw size by 1.
     */
    public void increaseDrawSize() {
        drawSize++;
    }

    /**
     * Increases the player's max action points by 1.
     */
    public void increaseActionPoints() {
        maxActionPoints++;
    }

    /**
     * Plays the given card against the given enemy. Card must be in the action deck.
     * If card is solely defensive, the target does not matter.
     *
     * @param card   Card to play
     * @param target Enemy to attack
     */
    public void playCard(Card card, Enemy target) {
        List<Enemy> enemies = new ArrayList<>();
        enemies.add(target);
        playCard(card, enemies);
    }

    /**
     * Plays the given card against the given enemies. Card must be in the action deck.
     * If card is solely defensive, the target does not matter.
     *
     * @param card    Card to play
     * @param targets Enemies to attack
     */
    public void playCard(Card card, List<Enemy> targets) {
        assert (actionDeckContains(card));
        for (Enemy target : targets) {
            card.play(this, target);
        }
        actionPoints -= card.getCost();
        assert actionPoints >= 0;

        actionDeck.remove(card);
        if (!card.isSingleUse()) {
            discardPile.push(card);
        }
    }

    /**
     * @return Copy of the player's action deck
     */
    public List<Card> getActionDeck() {
        return new ArrayList<>(actionDeck);
    }

    /**
     * @return True iff the action deck is empty
     */
    public boolean isActionDeckEmpty() {
        return actionDeck.isEmpty();
    }

    /**
     * Checks whether the given card is in the current action deck.
     *
     * @param card Card to check for
     * @return True iff player's action deck contains the current card. Returns false if card is null
     */
    public boolean actionDeckContains(Card card) {
        return (card != null) && actionDeck.contains(card);
    }

    /**
     * @return True iff the player is dead.
     */
    public boolean isDead() {
        return health <= 0;
    }

    /**
     * Heals the player, not allowing their health to go above max.
     *
     * @param health the amount of health to be restored.
     */
    public void heal(int health) {
        this.health = Math.min(this.health + health, maxHealth);
    }

    /**
     * Sorts the player's deck in alphabetical order
     */
    public void sortDeck() {
        Collections.sort(deck);
    }

    @Override
    public String toString() {
        return super.toString() + ". Gold: " + gold;
    }
}
