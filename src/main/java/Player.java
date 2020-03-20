import java.util.List;

/**
 * Immutable representation of a player.
 */
public class Player extends Being {
    protected int cardsPerDraw;

    /**
     * Constructor. Also fills the Player's health and action points to full.
     */
    public Player(String name, int maxHealth, int maxActionPoints, List<Card> deck, int cardsPerDraw) {
        super(name, maxHealth, maxActionPoints, deck);
        this.cardsPerDraw = cardsPerDraw;
    }

    /**
     * @return the number of cards per draw
     */
    public int getCardsPerDraw() {
        return cardsPerDraw;
    }

    /**
     * @param cardsPerDraw the number of cards per draw
     */
    public void setCardsPerDraw(int cardsPerDraw) {
        this.cardsPerDraw = cardsPerDraw;
    }
}
