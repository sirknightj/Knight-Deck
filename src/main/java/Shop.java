import java.util.*;

/**
 * This manages the player's actions when they visit the shop.
 */
public class Shop {
    private static Map<Card, Integer> contents; // The cards which the shopkeeper will sell.
//    private List<Charm> charms; // The charms which the shopkeeper will sell. [Coming soon]
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
        if(visitable) {
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
        if(Main.DEBUGSTATS) {
            System.out.println("DEBUG: Shop contents have been refreshed.");
        }
        contents = new HashMap<>();
        Set<Integer> alreadySeen = new HashSet<>();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            int randomCard = random.nextInt(CardFactory.getPlayerCards().size());;
            while(alreadySeen.contains(randomCard)) {
                randomCard = random.nextInt(CardFactory.getPlayerCards().size());
            }
            alreadySeen.add(randomCard);
            int j = 0;
            for(Card card : CardFactory.getPlayerCards()) {
                if(j == randomCard) {
                    contents.put(card, random.nextInt((card.getDamage() * card.getHits() + card.getDefense() + card.getShield())/2) + ((card.getDamage() * card.getHits() + card.getDefense() + card.getShield())/2) + 5);
                    break;
                }
                j++;
            }
        }
    }

    /**
     * The driver and menu of the shop.
     */
    public void enter() {
        if(contents == null) {
            refreshContents();
        }
        System.out.println("Vendor: Welcome to my shop!");
        Main.textWait();
        System.out.println("Shady Dealer: Come over here to see my wares.");
        Main.textWait();
        System.out.println("Priest: If you need blessings, come to me.");
        Main.textWait();
        while(true) {
            System.out.println("\nWhat would you like to do?");
            System.out.println("\tv to go visit the vendor");
            System.out.println("\ts to visit the shady dealer");
            System.out.println("\tp to visit the priest");
            System.out.println("\tl to leave");
            Scanner input = new Scanner(System.in);
            System.out.print("> ");
            String response = input.nextLine().trim();
            if(response.equalsIgnoreCase("v")) {
                visitVendor();
            } else if(response.equalsIgnoreCase("s")) {
                System.out.println("Coming soon. You'll be able to get some super rare cards here.");
                Main.textWait();
            } else if(response.equals("p")) {
                System.out.println("Coming soon. You'll be able to upgrade your stats here.");
                Main.textWait();
            } else if(response.equals("l")) {
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
        if(contents.keySet().isEmpty()) {
            System.out.println("Vendor: Sorry, I'm out of goods at the moment. Please check back later.");
            return;
        }
        System.out.println("Vendor: Hello, take a look at my wares!");
        Main.textWait();
        for(Card card : contents.keySet()) {
            System.out.println("\t" + contents.get(card) + " gold: " + card.getDescription());
        }
        Main.textWait();
        System.out.println("Vendor: All sales final! No returns!");
        Main.textWait();
        while(true) {
            Scanner input = new Scanner(System.in);
            System.out.println("Vendor: Type in the name of the card you would like to purchase (l to leave).");
            System.out.println("\t(You have " + player.getGold() + " gold.)");
            Card card = null;
            while(true) {
                System.out.print("Card> ");
                String response = input.nextLine();
                if (response.toLowerCase().equals("l")) {
                    System.out.println("Vendor: Come back soon!");
                    return;
                }
                card = CardFactory.getCard(response);
                // Print error messages if card is illegal
                if (card == null) {
                    System.out.println("Invalid card.");
                } else if (!contents.keySet().contains(card)) {
                    System.out.println("I'm not selling any card with that name.");
                } else if (player.getGold() < contents.get(card)) {
                    System.out.println("You don't have enough gold.");
                } else {
                    break;
                }
            }
            player.takeGold(contents.get(card));
            player.deckAdd(card);
            contents.remove(card);
            System.out.println("Vendor: Thanks for your purchase.");
            Main.textWait();
            System.out.println("\t" + card.getName() + " has been added into your deck.");
            Main.textWait();
            if(contents.keySet().isEmpty()) {
                System.out.println("Vendor: Sorry, I'm out of goods. Check back later!");
                return;
            }
            System.out.println("Vendor: I still have the following cards:");
            for(Card card1 : contents.keySet()) {
                System.out.println(contents.get(card1) + " gold: " + card1.getDescription());
            }
        }
    }
}