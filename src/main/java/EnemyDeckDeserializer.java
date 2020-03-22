import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class EnemyDeckDeserializer implements JsonDeserializer<Enemy> {
    @Override
    public Enemy deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject obj = jsonElement.getAsJsonObject();
        List<Card> deck = deserializeDeck(obj.get("deck").getAsJsonArray());
        String name = obj.get("name").getAsString();
        int health = obj.get("health").getAsInt();
        int actionPoints = obj.get("actionPoints").getAsInt();
        return new Enemy(name, health, actionPoints, deck);
    }

    private List<Card> deserializeDeck(JsonElement jsonElement) {
        assert jsonElement.isJsonArray();
        JsonArray arr = jsonElement.getAsJsonArray();

        List<Card> deck = new ArrayList<>();
        for (JsonElement cardName : arr) {
            deck.add(CardFactory.getCard(cardName.getAsString()));
        }
        return deck;
    }
}
