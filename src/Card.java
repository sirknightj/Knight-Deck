/**
 * This contains the basic elements of a card.
 */
public abstract class Card {
    private String name;
    private String description;
    private int cost;

    /**
     * Constructor. Creates a new card from its name, description, and cost.
     * @param name          The name of the card.
     * @param description   The description of the card.
     * @param cost          The cost to play this card.
     */
    public Card(String name, String description, int cost) {
        this.name = name;
        this.description = description;
        this.cost = cost;
    }

    /**
     * The card will perform its description.
     * @param being     The being targeted by the card.
     */
    public abstract void play(Being being);

    /**
     * Returns the name of the card.
     * @return  The name of the card.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the description of the card.
     * @return  The description of the card.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the cost to play this card.
     * @return  The cost to play this card.
     */
    public int getCost() {
        return cost;
    }
}
