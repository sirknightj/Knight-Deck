import java.util.*;

/**
 * Creates Cards
 */
public class CardFactory {
    private static Map<String, Card> nameToCard = new HashMap<>();
    private static Set<Card> playerCards = new HashSet<>();

    /**
     * @param card The card to be added into the database of cards.
     */
    public static void addCard(Card card) {
        nameToCard.put(card.getName().toLowerCase(), card);
        if (card.isPlayableBy(new Player("Example", 0, 0, new ArrayList<>()))) {
            playerCards.add(card);
        }
    }

    /**
     * @return Set of all Cards
     */
    public static Set<Card> getAllCards() {
        return new HashSet<>(nameToCard.values());
    }

    /**
     * @return A read-only set of all the player cards.
     */
    public static Set<Card> getPlayerCards() {
        return Collections.unmodifiableSet(playerCards);
    }

    /**
     * Returns the attack card with the given name.
     *
     * @param name Name of the card to get
     * @return Card with given name, null if card not found
     */
    public static Card getCard(String name) {
        return nameToCard.get(name.toLowerCase());
    }
}
