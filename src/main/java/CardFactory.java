import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Creates Cards
 */
public class CardFactory {
    private static Map<String, Card> nameToCard = new HashMap<String, Card>();

    public static void addCard(Card card) {
        nameToCard.put(card.getName(), card);
    }

    /**
     * @return Set of all Cards
     */
    public static Set<Card> getAllCards() {
        return new HashSet<>(nameToCard.values());
    }

    /**
     * Returns the attack card with the given name.
     * @param name Name of the card to get
     * @return attack card
     * @throws RuntimeException if no attack card with the given name is found
     */
    public static AttackCard getAttackCard(String name) {
        if (!nameToCard.containsKey(name)) {
            throw new RuntimeException("Card " + name + " not found");
        }
        Card result = nameToCard.get(name);
        if (result instanceof AttackCard) {
            return (AttackCard) result;
        }
        throw new RuntimeException("Card " + name + " is not an AttackCard");
    }
}
