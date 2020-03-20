import java.util.*;

/**
 * The driver class for battles.
 */
public class Battle {
    private Player player; // the player to battle.
    private List<Enemy> enemies; // the enemies to battle.
    private List<Enemy> enemyNames; // the names of the enemies on the field.
    private int turnCount; // the current turn count.
    private Stack<Card> drawPile; // the cards the player is yet to draw.
    private List<Card> actionDeck; // the cards in the player's hand.
    private List<String> cardNames; // the name of the cards in the player's hand.
    private Stack<Card> discardPile; // the cards the player has already seen.

    /**
     * Constructor. Also sets the turn count to 1.
     *
     * @param player  The player to battle.
     * @param enemies The enemies to battle.
     */
    public Battle(Player player, List<Enemy> enemies) {
        this.player = player;
        this.enemies = enemies;
        turnCount = 1;

        cardNames = new ArrayList<>();
        enemyNames = new ArrayList<>();
        drawPile = new Stack<>();
        actionDeck = new ArrayList<>();
        discardPile = new Stack<>();
    }

    /**
     * Starts the battle. Driver for the battle sequence.
     */
    public void start() {
        System.out.println("=== Battle has started! ===");
        for (Card card : player.getDeck()) {
            discardPile.add(card);
        }
        while (!isBattleOver()) {
            displayStats();
            doPlayerAction();
            doEnemyAction();
            turnCount++;
            System.out.println();
        }
        System.out.println("=== Battle has finished! ===");
        System.out.println(player.healthStatus());
    }

    /**
     * Asks the player what card they want to play, and then plays the card.
     */
    private void doPlayerAction() {
        refillPlayerHand();
        player.setActionPoints(player.getMaxActionPoints());
        System.out.println("You drew the following cards:");
        for (Card card : actionDeck) {
            System.out.println("\t" + card.toString());
        }
//        System.out.println("Your draw pile is as follows:");
//        for(Card card : drawPile) {
//            System.out.println("\t" + card.toString());
//        }
//        System.out.println("Your discard pile is as follows:");
//        for(Card card : discardPile) {
//            System.out.println("\t" + card.toString());
//        }
        Scanner input = new Scanner(System.in);
        boolean firstTime = true;
        while (player.getActionPoints() > 0 && actionDeck.size() > 0 && !isBattleOver()) {
            // Card selection
            if (!firstTime) {
                System.out.println("You still have the remaining cards:");
                for (Card card : actionDeck) {
                    System.out.println("\t" + card.toString());
                }
            }
            System.out.println("Enter the card name you want to play (e to end turn).");
            System.out.println("You have " + player.getActionPoints() + " action point(s) left this turn.");
            String response = input.nextLine();
            while (!cardNames.contains(response)) {
                if (response.equals("e")) {
                    break;
                }
                System.out.println("Invalid card name. Please input a valid card name.");
                response = input.nextLine();
            }
            if (response.equals("e")) {
                player.setActionPoints(-1);
                break;
            }

            // Target selection
            Being target = null;
            if (enemies.size() > 1) {
                System.out.println("The enemies are as follows:");
                for (Enemy enemy : enemies) {
                    System.out.println("\t" + enemy.toString());
                }
                System.out.println("Which enemy do you want to target?");
                String response2 = input.nextLine();
                while (!enemyNames.contains(response2)) {
                    System.out.println("Invalid enemy name. Please input a valid enemy name.");
                    response2 = input.nextLine();
                }
                for (int i = 0; i < enemies.size(); i++) {
                    if (enemies.get(i).getName().equals(response2)) {
                        target = enemies.get(i);
                    }
                }
            } else {
                target = enemies.get(0);
            }
            assert (target != null);
            playCard(CardFactory.getCard(response), target);
            System.out.println(target.healthStatus());
            firstTime = false;
        }
        if (isBattleOver()) {
            System.out.println("Your turn has automatically ended because you have defeated all the enemies.");
        } else if (player.getActionPoints() == 0) {
            System.out.println("Your turn has automatically ended because you have no more action points.");
        } else if (actionDeck.size() == 0) {
            System.out.println("Your turn has automatically ended because you have no more cards in your hand.");
        } else if (player.getActionPoints() == -1) {
            System.out.println("You have chosen to end your turn.");
        }
    }

    /**
     * Checks if the draw pile is empty. If so, refills the cards from the discard pile after shuffling.
     */
    private void refillCards() {
        if (drawPile.isEmpty()) {
            Collections.shuffle(discardPile);
            while (!discardPile.isEmpty()) {
                drawPile.add(discardPile.pop());
            }
        }
    }

    /**
     * Draws cards from the draw pile equal to the player's draw capacity, and puts them in the player's hand.
     */
    private void refillPlayerHand() {
        cardNames.clear();
        actionDeck.clear();
        for (int i = 0; i < player.getCardsPerDraw(); i++) {
            refillCards();
            Card card = drawPile.pop();
            cardNames.add(card.getName());
            actionDeck.add(card);
        }
    }

    /**
     * Called when the player plays a card.
     */
    private void playCard(Card card, Being target) {
        card.play(target);
        actionDeck.remove(card);
        player.setActionPoints(player.getActionPoints() - card.getCost());
        discardPile.add(card);
        cardNames.remove(card.getName());
    }

    /**
     * Checks if any enemy is dead and removes them from the battlefield.
     */
    private void checkForDeadEnemies() {
        Iterator<Enemy> i = enemies.iterator();
        while (i.hasNext()) {
            Enemy enemy = i.next();
            if (enemy.getHealth() <= 0) {
                i.remove();
            }
        }
    }

    /**
     * Looks inside each enemy's deck and chooses random card to play. Each card in the
     * enemy's deck has an equal chance of appearing. Repeats this process until the enemy
     * runs out of energy for the turn.
     */
    private void doEnemyAction() {
        checkForDeadEnemies();
        for (Enemy enemy : enemies) { // Refills the enemy's energy if necessessary.
            if (enemy.getActionPoints() < enemy.getMaxActionPoints()) {
                enemy.setActionPoints(enemy.getMaxActionPoints());
            }
            int endTurn = 0; // Ends the turn if it can't find a card to play.
            while (enemy.getActionPoints() > 0 && endTurn < 5) {
                Card card = enemy.getRandomCard();
                endTurn++;
                if (card.getCost() <= enemy.getActionPoints()) {
                    System.out.println(enemy.getName() + " has chosen to play " + card);
                    card.play(player);
                    enemy.setActionPoints(enemy.getActionPoints() - card.getCost());
                    endTurn = 0;
                }
            }
            System.out.println(enemy.getName() + " has ended their turn.");
        }
    }

    /**
     * Prints out the turn count, and the health of every being on the battlefield.
     */
    private void displayStats() {
        System.out.println("Turn " + turnCount);
        System.out.println(player.healthStatus());
        for (int i = 0; i < enemies.size(); i++) {
            System.out.println(enemies.get(i).healthStatus());
        }
    }

    /**
     * The battle is over when the player has lost all of their health, or if
     * there are no more enemies remaining on the field.
     *
     * @return true if the battle is over. Otherwise, returns false.
     */
    private boolean isBattleOver() {
        if (player.getHealth() <= 0) {
            return true;
        }
        checkForDeadEnemies();
        return enemies.isEmpty();
    }
}
