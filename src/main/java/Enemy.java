import java.util.*;

public class Enemy extends Being {

    //    private List<Card> deck; // current deck of cards
    private double cost; // the cost to place this enemy on the battlefield
    private int gold; // the maximum gold this enemy drops on defeat
    private List<Card> cardDrops; // the cards this enemy drops on defeat

    // TODO: Implement draw and discard
    private List<Card> drawPile;
    private List<Card> discardPile;

    /**
     * Constructor.
     *
     * @param name            The name of the enemy.
     * @param maxHealth       The max health of the enemy.
     * @param maxActionPoints The max action points of the enemy.
     * @param deck            The deck the enemy has.
     */
    public Enemy(String name, int maxHealth, int maxActionPoints, List<Card> deck, double cost, int gold, List<Card> cardDrops) {
        super(name, maxHealth, maxActionPoints, deck);
        this.cost = cost;
        this.gold = gold;

        // make sure each card is not null
        for (Card card : cardDrops) {
            assert card != null;
        }
        this.cardDrops = cardDrops;

        assert gold > 0;

        assert !deck.isEmpty();
        // make sure each card is a valid enemy card
        for (Card card : deck) {
            assert card.isPlayableBy(this);
        }
        this.deck = deck;

        this.drawPile = new ArrayList<>();
        this.discardPile = new ArrayList<>();
    }

    /**
     * Appends a String to the enemy's name.
     *
     * @param nameAddition the string to append.
     */
    public void addToName(String nameAddition) {
        name += nameAddition;
    }

    /**
     * @return The cost to place this enemy on the battlefield.
     */
    public double getCost() {
        return cost;
    }

    /**
     * @return A random amount of gold equal to or less than the gold, but larger than half the gold.
     */
    public int getGold() {
        Random r = new Random();
        return r.nextInt(gold / 2) + (gold / 2);
    }

    /**
     * @return The card drops for this enemy
     */
    public List<Card> getCardDrops() {
        return cardDrops;
    }

    /**
     * @return the list of Cards for this Enemy to play. Returns an empty list if it cannot find any Card
     */
    public List<Card> getMove() {
        List<Card> res = new ArrayList<>();
        int totalAP = 0;
        for (int i = 0; i < deck.size() && totalAP <= maxActionPoints; i++) {
            Card card = deck.get(i);
            if (card.getCost() + totalAP <= maxActionPoints) {
                totalAP += card.getCost();
                res.add(card);
            }
        }
        return res;
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

}
