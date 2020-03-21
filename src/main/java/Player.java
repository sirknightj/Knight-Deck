import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * Immutable representation of the player.
 */
public class Player extends Being {
    private static final int CARDS_PER_DRAW = 3;

    private Stack<Card> drawPile; // the cards the player is yet to draw.
    private List<Card> actionDeck; // the cards in the player's hand.
    private Stack<Card> discardPile; // the cards the player has already seen.

    /**
     * Constructor. Also fills the Player's health and action points to full.
     */
    public Player(String name, int maxHealth, int maxActionPoints, List<Card> deck) {
        super(name, maxHealth, maxActionPoints, deck);

        // make sure each card is a valid player card
        for (Card card : deck) {
            assert (card.isPlayableBy(this));
        }

        drawPile = new Stack<>();
        actionDeck = new ArrayList<>();
        discardPile = new Stack<>();
    }

    /**
     * Should be called before starting a battle.
     * Action deck is empty. Draw pile contains all player's cards shuffled. Discard pile is empty.
     */
    public void initializeDeck() {
        discardPile.clear();
        actionDeck.clear();
        drawPile.addAll(deck);
    }

    /**
     * Draws cards from the draw pile equal to the player's draw capacity, and puts them in the player's hand.
     */
    public void drawCards() {
        actionDeck.clear();
        for (int i = 0; i < CARDS_PER_DRAW; i++) {
            if (drawPile.isEmpty()) {
                drawPile.addAll(discardPile);
                discardPile.clear();
                Collections.shuffle(drawPile);
            }

            Card card = drawPile.pop();
            actionDeck.add(card);
        }
    }

    /**
     * Plays the given card against the given enemy. Card must be in the action deck.
     * If card is solely defensive, the target does not matter.
     *
     * @param card   Card to play
     * @param target Enemy to attack
     */
    public void playCard(Card card, Enemy target) {
        assert (actionDeckContains(card));
        card.play(this, target);
        actionPoints -= card.getCost();
        assert (actionPoints >= 0);

        actionDeck.remove(card);
        discardPile.push(card);
    }

    /**
     * Moves the remaining unselected cards from the action deck to the discard pile.
     */
    public void finishTurn() {
        discardPile.addAll(actionDeck);
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
     * Tells the player to take damage
     *
     * @param damage Damage per hit against being
     * @param hits   Number of attacks
     */
    @Override
    public void takeDamage(int damage, int hits) {
        assert (damage > 0);
        assert (hits > 0);
        damage = Math.max(damage - defense, 0);
        health = Math.max(health - damage * hits, 0);
    }

    /**
     * @return True iff the player is dead.
     */
    public boolean isDead() {
        return health <= 0;
    }
}
