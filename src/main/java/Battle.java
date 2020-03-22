import java.util.List;
import java.util.Scanner;

/**
 * The driver class for battles.
 */
public class Battle {
    private Player player; // the player to battle.
    private List<Enemy> enemies; // the enemies to battle.
    private int turn; // the current turn number.

    /**
     * Constructor. Also sets the turn count to 1, and if there is more than 1 enemy,
     * assigns numbers to their names for player convenience when targeting.
     *
     * @param player  The player to battle.
     * @param enemies The enemies to battle.
     */
    public Battle(Player player, List<Enemy> enemies) {
        this.player = player;
        this.enemies = enemies;
        turn = 1;

        if (enemies.size() > 1) {
            for (int i = 1; i <= enemies.size(); i++) {
                enemies.get(i - 1).addToName(" (" + i + ")");
            }
        }
    }

    /**
     * Starts the battle. Driver for the battle sequence.
     */
    public void start() {
        System.out.println("=== Battle has started! ===");
        player.initializeDeck();
        while (!isBattleOver()) {
            displayStats();
            System.out.println();
            planEnemyAction();
            System.out.println();
            doPlayerAction();
            System.out.println();
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
            System.out.println("\t" + card.getDescription());
        }

        Scanner input = new Scanner(System.in);
        boolean firstTime = true;
        while (player.getActionPoints() > 0 && !player.isActionDeckEmpty() && !isBattleOver()) {
            // Card selection
            if (!firstTime) {
                System.out.println("\nYou still have the remaining cards:");
                for (Card card : player.getActionDeck()) {
                    System.out.println("\t" + card.getDescription());
                }
            }
            System.out.println("You have " + player.getActionPoints() + " action point(s) left this turn.");
            System.out.println("Enter the card name you want to play (e to end turn).");

            // Card selection process
            Card cardToPlay;
            while (true) {
                System.out.print("Card> ");
                String response = input.nextLine();
                if (response.toLowerCase().equals("e")) {
                    System.out.println("You have chosen to end your turn.");
                    player.finishTurn();
                    return;
                }

                cardToPlay = CardFactory.getCard(response);
                // Print error messages if card is illegal
                if (cardToPlay == null) {
                    System.out.println("Invalid card.");
                } else if (!player.actionDeckContains(cardToPlay)) {
                    System.out.println("You don't have that card in hand.");
                } else if (player.getActionPoints() < cardToPlay.getCost()) {
                    System.out.println("You do not have enough action points.");
                } else {
                    break;
                }
            }

            // Enemy selection process.
            Enemy target = null;
            if (enemies.size() == 1 || cardToPlay.getDamage() * cardToPlay.getHits() == 0) {
                target = enemies.get(0);
            } else {
                System.out.println("Which enemy number do you want to target?");
                while (target == null) {
                    System.out.print("Enemy> ");
                    if (input.hasNextInt()) {
                        String response = input.nextLine();
                        for (Enemy enemy : enemies) {
                            if (enemy.getName().toLowerCase().contains(response.toLowerCase())) {
                                target = enemy;
                            }
                        }
                    }
                    if (target == null) {
                        System.out.println("Invalid enemy.");
                    }
                }
            }

            assert (target != null);

            player.playCard(cardToPlay, target);
            System.out.println("You played " + cardToPlay.getName() + "!");
            System.out.println("\t" + cardToPlay.forecast(target));
            if (cardToPlay.getDamage() > 0) {
                System.out.println(target.healthStatus());
            }
            if (cardToPlay.getDefense() > 0) {
                System.out.println("You have " + player.getDefense() + " defense.");
            }
            firstTime = false;
        }
        if (isBattleOver()) {
            System.out.println("Your turn has automatically ended because you have defeated all the enemies.");
        } else if (player.getActionPoints() == 0) {
            System.out.println("Your turn has automatically ended because you have no more action points.");
        } else if (player.isActionDeckEmpty()) {
            System.out.println("Your turn has automatically ended because you have no more cards in your hand.");
        }
        player.finishTurn();
    }

    /**
     * Looks inside each enemy's deck and chooses random card they intend to play. Each card in the
     * enemy's deck has an equal chance of appearing. Repeats this process until the enemy
     * runs out of action points for the turn.
     */
    private void planEnemyAction() {
        for (Enemy enemy : enemies) {
            enemy.resetActionPoints();
            Card card = enemy.chooseCard();
            String also = "";
            // Enemy chooses cards until enemy's AP=0 or no valid options
            while (card != null) {
                enemy.intend(card);
                System.out.println(enemy.getName() + also + " plans to use " + card.getName() + ".");
                System.out.println("\t" + card.getDescription());
                card = enemy.chooseCard();
                also = " also";
            }
        }
    }

    /**
     * Checks if any enemy is dead and removes them from the battlefield.
     */
    private void checkForDeadEnemies() {
        enemies.removeIf(enemy -> enemy.getHealth() <= 0);
    }

    /**
     * Each enemy plays the cards that they intended to do.
     */
    private void doEnemyAction() {
        checkForDeadEnemies();
        for (Enemy enemy : enemies) {
            enemy.setDefense(0);

            // Play enemy's cards that it intended to play
            while (!enemy.isIntendEmpty() && !player.isDead()) {
                Card card = enemy.getIntendedCard();
                enemy.playCard(card, player);
                System.out.println(enemy.getName() + " plays " + card.getName() + "!");
                System.out.println("\t" + card.forecast(player));
                if (card.getDamage() > 0) {
                    System.out.println(player.healthStatus());
                }
                if (card.getDefense() > 0) {
                    System.out.println(enemy.getName() + " has " + enemy.getDefense() + " defense.");
                }
            }

            System.out.println(enemy.getName() + " has ended their turn.");
        }
    }

    /**
     * Prints out the turn count, and the health of every being on the battlefield.
     */
    private void displayStats() {
        System.out.println("--Turn " + turn + "--");
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
