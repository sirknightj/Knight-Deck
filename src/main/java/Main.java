import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * This is the driver class of the program.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== Knight Deck ===");

        Gson gson = new Gson();
        try {
            loadAttackCards(gson);
        } catch (URISyntaxException | IOException e) {
            System.err.println("Failed to load attack cards");
        }

        System.out.println("List of cards: ");
        for (Card card : CardFactory.getAllCards()) {
            System.out.println("\t" + card.toString());
        }
    }

    /**
     * Loads all attack cards into CardFactory from data file
     * @param gson Gson instance
     * @throws URISyntaxException
     * @throws IOException
     */
    private static void loadAttackCards(Gson gson) throws URISyntaxException, IOException {
        // file containing attack card data
        Reader attackCardFile = Files.newBufferedReader(Paths.get(Main.class.getResource("attack.json").toURI()));

        List<AttackCard> attackCards = gson.fromJson(attackCardFile, new TypeToken<List<AttackCard>>() {}.getType());

        for (AttackCard card : attackCards) {
            CardFactory.addCard(card);
        }
    }
}
