import java.util.*;

/**
 * The driver class for battles.
 */
public class Battle {
    private Player player; // the player to battle.
    private List<Enemy> enemies; // the enemies to battle.
    private int turn; // the current turn number.

    /**
     * Constructor. Also sets the turn count to 1.
     *
     * @param player  The player to battle.
     * @param enemies The enemies to battle.
     */
    public Battle(Player player, List<Enemy> enemies) {
        this.player = player;
        this.enemies = enemies;
        turn = 1;
    }

    /**
     * Starts the battle. Driver for the battle sequence.
     */
    public void start() {
        System.out.println("=== Battle has started! ===");
        player.initializeDeck();
        while (!isBattleOver()) {
            displayStats();
            doPlayerAction();
            doEnemyAction();
            player.setDefense(0);
            turn++;
            System.out.println();
        }
        System.out.println("=== Battle has finished! ===");
        System.out.println(player.healthStatus());
    }

    /**
     * Asks the player what card they want to play, and then plays the card.
     */
    private void doPlayerAction() {
        player.drawCards();
        player.resetActionPoints();
        System.out.println("You drew the following cards:");
        for (Card card : player.getActionDeck()) {
            System.out.println("\t" + card.toString());
        }

        Scanner input = new Scanner(System.in);
        boolean firstTime = true;
        while (player.getActionPoints() > 0 && !player.isActionDeckEmpty() && !isBattleOver()) {
            // Card selection
            if (!firstTime) {
                System.out.println("You still have the remaining cards:");
                for (Card card : player.getActionDeck()) {
                    System.out.println("\t" + card.toString());
                }
            }
            System.out.println("Enter the card name you want to play (e to end turn).");
            System.out.println("You have " + player.getActionPoints() + " action point(s) left this turn.");

            // Card selection process
            Card cardToPlay;
            while (true) {
                System.out.print("Card> ");
                String response = input.nextLine();
                if (response.toLowerCase().equals("e")) {
                    System.out.println("You have chosen to end your turn.");
                    return;
                }

                cardToPlay = CardFactory.getCard(response);
                // Print error messages if card is illegal
                if (cardToPlay == null) {
                    System.out.println("Invalid card.");
                } else if (!player.actionDeckContains(cardToPlay)) {
                    System.out.println("Your action deck does not have that card.");
                } else if (player.getActionPoints() < cardToPlay.getCost()) {
                    System.out.println("You do not have enough action points.");
                } else {
                    break;
                }
            }

            Enemy target = enemies.get(0);
            assert (target != null);

            player.playCard(cardToPlay, target);
            System.out.println("You played " + cardToPlay + "!");
            System.out.println(target.healthStatus());
            firstTime = false;
        }
        if (isBattleOver()) {
            System.out.println("Your turn has automatically ended because you have defeated all the enemies.");
        } else if (player.getActionPoints() == 0) {
            System.out.println("Your turn has automatically ended because you have no more action points.");
        } else if (player.isActionDeckEmpty()) {
            System.out.println("Your turn has automatically ended because you have no more cards in your hand.");
        }
    }

    /**
     * Checks if any enemy is dead and removes them from the battlefield.
     */
    private void checkForDeadEnemies() {
        enemies.removeIf(enemy -> enemy.getHealth() <= 0);
    }

    /**
     * Looks inside each enemy's deck and chooses random card to play. Each card in the
     * enemy's deck has an equal chance of appearing. Repeats this process until the enemy
     * runs out of energy for the turn.
     */
    private void doEnemyAction() {
        checkForDeadEnemies();
        for (Enemy enemy : enemies) {
            enemy.resetActionPoints();
            enemy.setDefense(0);

            // Play enemy's cards until enemy's AP=0 or no valid options
            Card card = enemy.chooseCard();
            while (card != null && !player.isDead()) {
                enemy.playCard(card, player);
                System.out.println(enemy.getName() + " has played " + card);
                card = enemy.chooseCard();
            }

            System.out.println(enemy.getName() + " has ended their turn.");
        }
    }

    /**
     * Prints out the turn count, and the health of every being on the battlefield.
     */
    private void displayStats() {
        System.out.println("Turn " + turn);
        System.out.println(player.healthStatus());
        for (Enemy enemy : enemies) {
            System.out.println(enemy.healthStatus());
        }
    }

    /**
     * The battle is over when the player has lost all of their health, or if
     * there are no more enemies remaining on the field.
     *
     * @return true if the battle is over. Otherwise, returns false.
     */
    private boolean isBattleOver() {
        checkForDeadEnemies();
        return player.isDead() || enemies.isEmpty();
    }
}
