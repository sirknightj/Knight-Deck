import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Enemy extends Being {

    private List<String> unofficialDeck; // used when loading cards from a file
    private Queue<Card> intent; // the cards the enemy intends to use this turn

    /**
     * Constructor.
     *
     * @param name            The name of the enemy.
     * @param maxHealth       The max health of the enemy.
     * @param maxActionPoints The max action points of the enemy.
     * @param deck            The deck the enemy has.
     */
    public Enemy(String name, int maxHealth, int maxActionPoints, List<Card> deck) {
        super(name, maxHealth, maxActionPoints, deck);

        assert (!deck.isEmpty());
        // make sure each card is a valid enemy card
        for (Card card : deck) {
            assert (card.isPlayableBy(this));
        }

        intent = new LinkedList<>();
    }

    /**
     * @return The max health of the enemy.
     */
    public int getMaxHealth() {
        return maxHealth;
    }

    /**
     * @param deck the new deck of the enemy.
     */
    public void setDeck(List<Card> deck) {
        this.deck = deck;
    }

    /**
     * @return the names of all the cards in the deck.
     */
    public List<String> getUnofficialDeck() {
        return unofficialDeck;
    }

    /**
     * Adds the intended card into the enemy's intent queue, and takes away the
     * appropriate number of action points away.
     *
     * @param card the card to be added into the queue.
     */
    public void intend(Card card) {
        actionPoints -= card.getCost();
        assert (actionPoints >= 0);
        intent.add(card);
    }

    /**
     * @return true if the enemy's intent queue is empty.
     */
    public boolean isIntendEmpty() {
        return intent.isEmpty();
    }

    /**
     * Returns true iff the card already exists in the intent queue.
     * @param card the card to be checked.
     * @return true iff the card exists in the intent queue.
     */
    public boolean intendContains(Card card) {
        System.out.println(card);
        System.out.println("intent" + intent);
        return intent.contains(card);
    }

    /**
     * Returns the card at the front of the queue, and removes it from the queue.
     *
     * @return the card at the front of the queue.
     */
    public Card getIntendedCard() {
        return intent.remove();
    }

    /**
     * @return the Card for the Enemy to play. Returns null if it cannot find one
     */
    public Card chooseCard() {
        if (actionPoints > 0) {
            for (int endTurn = 0; endTurn < 10; endTurn++) {
                Card card = getRandomCard();
                if (card.getCost() <= actionPoints) {
                    return card;
                }
            }
        }
        return null;
    }

    /**
     * Plays the given card against the given target. If card is solely defensive, the target does not matter.
     *
     * @param card   Card to play
     * @param target Target to attack (should be a Player?)
     */
    public void playCard(Card card, Being target) {
        card.play(this, target);
    }

    /**
     * @return a random card in this enemy's deck.
     */
    public Card getRandomCard() {
        return deck.get((int) (Math.random() * deck.size()));
    }

    @Override
    public void takeDamage(int damage, int hits) {
        assert (damage > 0);
        assert (hits > 0);
        damage = Math.max(damage - defense, 0);
        health = Math.max(health - damage * hits, 0);
    }
}
