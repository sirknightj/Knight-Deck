import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Creates Cards
 */
public class CardFactory {
    private static Map<String, Card> nameToCard = new HashMap<>();

    public static void addCard(Card card) {
        nameToCard.put(card.getName().toLowerCase(), card);
    }

    /**
     * @return Set of all Cards
     */
    public static Set<Card> getAllCards() {
        return new HashSet<>(nameToCard.values());
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
