/**
 * Immutable representation of a card.
 */
public abstract class Card {
    private String name;
    private int cost;

    /**
     * Constructor. Creates a new card from its name and cost.
     * @param name          The name of the card.
     * @param cost          The cost to play this card.
     */
    public Card(String name, int cost) {
        this.name = name;
        this.cost = cost;
    }

    /**
     * Returns the name of the card.
     * @return  The name of the card.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the cost to play this card.
     * @return  The cost to play this card.
     */
    public int getCost() {
        return cost;
    }

    /**
     * Returns the description of the card.
     * @return  The description of the card.
     */
    public abstract String getDescription();

    /**
     * The card will perform its description against the given Being.
     * @param being     The being targeted by the card.
     */
    public abstract void play(Being being);

    public String toString() {
        return name + " [" + getCost() + "]";
    }
}
