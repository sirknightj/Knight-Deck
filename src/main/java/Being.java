import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * This contains the elements of a basic Being.
 */
public abstract class Being {
    protected String name;
    protected int maxHealth;
    protected int health;
    protected int actionPoints;
    protected int maxActionPoints;
    protected int defense;
    protected List<Card> deck;
    protected int shield;
    protected int strength;

    private static final int DEFAULT_DRAW_SIZE = 3;
    protected int drawSize; // the number of cards the being starts off their turn with in the actionDeck.
    protected List<Card> actionDeck; // the cards in the being's hand.
    protected List<Card> drawPile; // the cards the being is yet to draw.
    protected List<Card> discardPile; // the cards the being has already seen.

    /**
     * Constructor. Also sets action points and health to their respective maximum values.
     *
     * @param name            the name of the being.
     * @param maxHealth       the maximum health of the being.
     * @param maxActionPoints the maximum action points this being can perform, per turn.
     * @param deck            the deck that the being has
     */
    public Being(String name, int maxHealth, int maxActionPoints, List<Card> deck) {
        this.name = name;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.actionPoints = maxActionPoints;
        this.maxActionPoints = maxActionPoints;
        this.deck = new ArrayList<>(deck);
        this.defense = 0;
        this.shield = 0;
        this.strength = 0;

        this.drawSize = DEFAULT_DRAW_SIZE;

        drawPile = new LinkedList<>();
        actionDeck = new ArrayList<>();
        discardPile = new ArrayList<>();
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
     * Draws cards from the draw pile equal to the being's draw capacity, and puts them in the being's hand.
     * Or, if the being's action deck is not discarded, draw more cards up to the being's draw capacity.
     */
    public void drawCards() {
        for (int i = actionDeck.size(); i < drawSize; i++) {
            // exit if being has already drawn their entire deck
            if (actionDeck.size() == deck.size()) {
                return;
            }

            // fill draw pile from discard pile as necessary
            if (drawPile.isEmpty()) {
                // Reset this player's draw pile
                drawPile.addAll(discardPile);
                discardPile.clear();
                Collections.shuffle(drawPile);
            }

            Card card = drawPile.remove(0);
            actionDeck.add(card);
        }
    }

    public abstract void playCard(Card card, Being target);

    /**
     * @return The number of cards the being should have in their action deck at the start of their turn.
     */
    public int getDrawSize() {
        return drawSize;
    }

    /**
     * @return The name of the being.
     */
    public String getName() {
        return name;
    }

    /**
     * @return The health of the being.
     */
    public int getHealth() {
        return health;
    }

    /**
     * @return THe max health of the being.
     */
    public int getMaxHealth() {
        return maxHealth;
    }

    /**
     * @return The action points of the being.
     */
    public int getActionPoints() {
        return actionPoints;
    }

    /**
     * @return The maximum action points of the being.
     */
    public int getMaxActionPoints() {
        return maxActionPoints;
    }

    /**
     * Resets the being's action points to their max.
     */
    public void resetActionPoints() {
        actionPoints = maxActionPoints;
    }

    /**
     * @return The current defense this being has.
     */
    public int getDefense() {
        return defense;
    }

    /**
     * @param defense The current defense this being has.
     */
    public void setDefense(int defense) {
        this.defense = defense;
    }

    /**
     * @return The current shield this being has.
     */
    public int getShield() {
        return shield;
    }

    /**
     * @return The current strength this being has.
     */
    public int getStrength() {
        return strength;
    }

    /**
     * Adds shield to the being.
     *
     * @param shield The amount of shield to add.
     */
    public void increaseShield(int shield) {
        this.shield += shield;
    }

    /**
     * Adds strength to the being.
     *
     * @param strength The amount of strength to add.
     */
    public void increaseStrength(int strength) {
        this.strength += strength;
    }

    /**
     * Resets the defense, shield, and actionPoints of the being.
     */
    public void turnStartStatReset() {
        if (defense > 0) {
            System.out.println(name + "'s defense wears off.");
            defense = 0;
        }
        if (shield > 0) {
            System.out.println(name + "'s shield wears off.");
            shield = 0;
        }
        if (strength > 0) {
            if (strength == 1) {
                System.out.println(name + "'s strength wears off.");
                strength = 0;
            } else {
                System.out.println((int) Math.ceil((double) strength / 2) + " of " + name + "'s " + strength + " strength wears off.");
                strength /= 2;
            }
        }
        resetActionPoints();
    }

    /**
     * Calculates how much damage the card will do to this being. Does not change the being's status.
     *
     * @param opponent The opponent playing the card.
     * @param card     The card to be played.
     * @return The damage that the being should receive.
     */
    public int damageCalculation(Being opponent, Card card) {
        return Math.max(preShieldDamageCalculation(opponent, card) - shield, 0);
    }

    /**
     * Calculates how much damage the card will do to this being, without shields being considered.
     * Does not change this being's status.
     *
     * @param opponent The opponent playing this card.
     * @param card     The card to be played.
     * @return The damage that the being should receive before shields are factored in.
     */
    public int preShieldDamageCalculation(Being opponent, Card card) {
        return Math.max(((card.getDamage() - defense + opponent.getStrength()) * card.getHits()), 0);
    }

    /**
     * Tells the being to take damage, with defense and shield factored in.
     *
     * @param damage Damage per hit against being
     * @param hits   Number of attacks
     */
    public void takeDamage(int damage, int hits) {
        assert damage > 0;
        assert hits > 0;
        damage = Math.max((damage - defense) * hits, 0);
        if (shield > 0) {
            int oldDamage = damage;
            damage = Math.max(damage - shield, 0);
            shield = Math.max(shield - oldDamage, 0);
        }
        health = Math.max(health - damage, 0);
    }

    /**
     * @param health new health, must be non-negative
     */
    public void setHealth(int health) {
        assert health >= 0;
        this.health = health;
    }

    /**
     * @return The health / maxHealth of the being in String form.
     */
    public String healthStatus() {
        if (health <= 0) {
            return name + " is dead.";
        }

        String output = name + " has " + health + "/" + maxHealth + " health";
        output += (defense > 0) ? " and " + defense + " defense" : "";
        output += (shield > 0) ? " and " + shield + " shield" : "";
        output += (strength > 0) ? " and " + strength + " strength" : "";
        return output.trim() + ".";
    }

    /**
     * @return Copy of the player's action deck
     */
    public List<Card> getActionDeck() {
        return new ArrayList<>(actionDeck);
    }

    /**
     * Checks whether the given card is in the current action deck.
     *
     * @param card Card to check for
     * @return True iff being's action deck contains the current card. Returns false if card is null
     */
    public boolean actionDeckContains(Card card) {
        return (card != null) && actionDeck.contains(card);
    }

    /**
     * @return True iff the action deck is empty
     */
    public boolean isActionDeckEmpty() {
        return actionDeck.isEmpty();
    }

    /**
     * @return True iff the player is dead.
     */
    public boolean isDead() {
        return health <= 0;
    }

    /**
     * @return The deck of the current being.
     */
    public List<Card> getDeck() {
        return new ArrayList<>(deck);
    }

    /**
     * @return A string representation of the being.
     */
    @Override
    public String toString() {
        return name + ": " + health + "/" + maxHealth + ". Deck: " + deck;
    }
}
