import java.util.*;

/**
 * This manages the player's actions when they visit the shop.
 */
public class Shop {
    private static Map<Card, Integer> vendorContents; // The cards which the shopkeeper will sell.
    private static Map<Card, Integer> shadyContents; // The cards which the shady dealer will sell.
    private static Player player = Main.player;
    private static boolean visitable = false;

    /**
     * Holds the shop.
     */
    private static class ShopHolder {
        private static final Shop INSTANCE = new Shop();
    }

    /**
     * @return The current instance of the shop.
     */
    public static Shop getInstance() {
        if (visitable) {
            return ShopHolder.INSTANCE;
        } else {
            return null;
        }
    }

    /**
     * Allows the player to visit the shop.
     */
    public static void nowVisitable() {
        visitable = true;
    }

    /**
     * Refreshes the content in the shops.
     */
    public static void refreshContents() {
        if (Main.DEBUGSTATS) {
            System.out.println("DEBUG: Shop contents have been refreshed.");
        }
        vendorContents = new HashMap<>();
        Set<Integer> alreadySeen = new HashSet<>();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            int randomCard = random.nextInt(CardFactory.getPlayerCards().size());
            while (alreadySeen.contains(randomCard)) {
                randomCard = random.nextInt(CardFactory.getPlayerCards().size());
            }
            alreadySeen.add(randomCard);
            int j = 0;
            for (Card card : CardFactory.getPlayerCards()) {
                if (j == randomCard) {
                    vendorContents.put(card, (random.nextInt(1 + (card.getDamage() * card.getHits() + card.getDefense() * 3 + 2 * card.getShield()) / 2)) + ((card.getDamage() * card.getHits() + card.getDefense() + card.getShield()) / 2) + 5);
                    break;
                }
                j++;
            }
        }
        shadyContents = new HashMap<>();
        shadyContents.put(CardFactory.getCard("Strength Potion"), (int) (Math.random() * 10) + 5);
        shadyContents.put(CardFactory.getCard("Motivational Photo"), (int) (Math.random() * 10) + 5);
        shadyContents.put(CardFactory.getCard("Relentless Beatdown"), (int) (Math.random() * 10) + 10);
    }

    /**
     * The driver and menu of the shop.
     */
    public void enter() {
        if (vendorContents == null) {
            refreshContents();
        }
        System.out.println("Vendor: Welcome to my shop!");
        Main.textWait();
        System.out.println("Shady Dealer: Come over here to see my wares.");
        Main.textWait();
        while (true) {
            System.out.println("\nWhat would you like to do?");
            System.out.println("\tv to go visit the vendor");
            System.out.println("\ts to visit the shady dealer");
            System.out.println("\tl to leave");
            Scanner input = new Scanner(System.in);
            System.out.print("> ");
            String response = input.nextLine().trim();
            if (response.equalsIgnoreCase("v")) {
                visitVendor();
            } else if (response.equalsIgnoreCase("s")) {
                visitShadyMerchant();
            } else if (response.equals("l")) {
                System.out.println("Everyone: Come back again soon!");
                Main.textWait();
                return;
            } else {
                System.out.println("Invalid input.");
            }
        }
    }

    /**
     * Driver for when the player visits the vendor.
     */
    private void visitVendor() {
        if (vendorContents.keySet().isEmpty()) {
            System.out.println("Vendor: Sorry, I'm out of goods at the moment. Please check back later.");
            return;
        }
        System.out.println("Vendor: Hello, take a look at my wares!");
        Main.textWait();
        for (Card card : vendorContents.keySet()) {
            System.out.println("\t" + vendorContents.get(card) + " gold: " + card.getDescription(player));
        }
        Main.textWait();
        System.out.println("Vendor: All sales final! No returns!");
        Main.textWait();
        while (true) {
            Scanner input = new Scanner(System.in);
            System.out.println("Vendor: Type in the name of the card you would like to purchase (l to leave).");
            System.out.println("\t(You have " + player.getGold() + " gold.)");
            Card card = null;
            while (true) {
                System.out.print("Card> ");
                String response = input.nextLine();
                if (response.toLowerCase().equals("l")) {
                    System.out.println("Vendor: Come back soon!");
                    return;
                }
                card = CardFactory.getCard(response);

                // Print error messages if card is illegal
                if (card == null) {
                    System.out.println("Vendor: Invalid card.");
                } else if (!vendorContents.keySet().contains(card)) {
                    System.out.println("Vendor: I'm not selling any card with that name.");
                } else if (player.getGold() < vendorContents.get(card)) {
                    System.out.println("You don't have enough gold.");
                } else {
                    break;
                }
                Main.textWait();
            }
            player.takeGold(vendorContents.get(card));
            player.deckAdd(card);
            vendorContents.remove(card);
            System.out.println("Vendor: Thanks for your purchase.");
            Main.textWait();
            System.out.println("\t" + card.getName() + " has been added into your deck.");
            Main.textWait();
            if (vendorContents.keySet().isEmpty()) {
                System.out.println("Vendor: Sorry, I'm out of goods. Check back later!");
                return;
            }
            System.out.println("Vendor: I still have the following cards:");
            for (Card card1 : vendorContents.keySet()) {
                System.out.println("\t" + vendorContents.get(card1) + " gold: " + card1.getDescription(player));
            }
        }
    }

    private void visitShadyMerchant() {
        if (shadyContents.keySet().isEmpty()) {
            System.out.println("Shady Dealer: Can't you see thatI'm busy? Please check back later!");
            return;
        }
        System.out.println("Shady Dealer: Sup. Today on the black market, we have:");
        Main.textWait();
        for (Card card : shadyContents.keySet()) {
            System.out.println("\t" + shadyContents.get(card) + " gold: " + card.getDescription(player));
        }
        System.out.println("\t100 gold: Mind Training [/] ActionPoints +1 Per Turn.");
        System.out.println("\t50 gold: Slight of Hand [/] Draw +1 Cards Per Turn.");
        Main.textWait();
        System.out.println("Shady Dealer: No returns. Tell me whatcha want. Hurry up.");
        Main.textWait();
        while (true) {
            Scanner input = new Scanner(System.in);
            System.out.println("Shady Dealer: I said hurry up!! (l to leave).");
            System.out.println("\t(You have " + player.getGold() + " gold.)");
            Card card = null;
            while (true) {
                System.out.print("Item> ");
                String response = input.nextLine();
                if (response.toLowerCase().equals("l")) {
                    System.out.println("Shady Dealer: You'd better keep quiet.");
                    return;
                } else if (response.equalsIgnoreCase("Mind Training")) {
                    if (player.getGold() >= 100) {
                        player.increaseActionPoints();
                        player.takeGold(100);
                        System.out.println("Shady Dealer: Done. You now start each turn with " + player.getMaxActionPoints() + " action points.");
                        System.out.println("\t(You have " + player.getGold() + " gold.)");
                        Main.textWait();
                        System.out.println("Shady Dealer: Anything else? (l to leave)");
                    } else {
                        System.out.println("Shady Dealer: You don't have enough gold.");
                        Main.textWait();
                    }
                } else if (response.equalsIgnoreCase("Slight of Hand")) {
                    if (player.getGold() >= 50) {
                        player.increaseDrawSize();
                        player.takeGold(50);
                        System.out.println("Shady Dealer: Done. You now start each turn with " + player.getDrawSize() + " cards.");
                        System.out.println("\t(You have " + player.getGold() + " gold.)");
                        Main.textWait();
                        System.out.println("Shady Dealer: Anything else? (l to leave)");
                    } else {
                        System.out.println("Shady Dealer: You don't have enough gold.");
                        Main.textWait();
                    }
                } else {
                    card = CardFactory.getCard(response);
                    // Print error messages if card is illegal
                    if (card == null) {
                        System.out.println("Shady Dealer: Invalid item.");
                    } else if (!shadyContents.keySet().contains(card)) {
                        System.out.println("Shady Dealer: I'm not selling any item with that name.");
                    } else if (player.getGold() < shadyContents.get(card)) {
                        System.out.println("Shady Dealer: You don't have enough gold.");
                    } else {
                        break;
                    }
                }
            }
            player.takeGold(shadyContents.get(card));
            player.deckAdd(card);
            shadyContents.remove(card);
            System.out.println("Shady Dealer: Dun deal.");
            Main.textWait();
            System.out.println("\t" + card.getName() + " has been added into your deck.");
            Main.textWait();
            if (vendorContents.keySet().isEmpty()) {
                System.out.println("Shady Dealer: Sorry, limited stock. Check back later!");
                return;
            }
            System.out.println("Shady Dealer: I still have the following cards:");
            for (Card card1 : shadyContents.keySet()) {
                System.out.println(shadyContents.get(card1) + " gold: " + card1.getDescription(player));
            }
        }
    }
}