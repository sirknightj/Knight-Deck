import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * The driver class for battles.
 */
public class Battle {
    private Player player; // the player to battle.
    private List<Enemy> enemies; // the enemies to battle.
    private int turn; // the current turn number.
    private List<Card> possibleCardDrops; // the card drops from all the enemies combined.

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
        possibleCardDrops = new ArrayList<>();

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
            turn++;
        }
        System.out.print("=== Battle has finished! ===\n");
        doCardAdding();
    }

    /**
     * Asks the player what card they want to play, and then plays the card.
     */
    private void doPlayerAction() {
        player.drawCards();
        System.out.println("You drew the following cards:");
        for (Card card : player.getActionDeck()) {
            System.out.println("\t" + card.getDescription(player));
        }

        Scanner input = new Scanner(System.in);
        boolean firstTime = true;
        while (player.getActionPoints() > 0 && !player.isActionDeckEmpty() && !isBattleOver()) {
            // Card selection
            if (!firstTime) {
                System.out.println("\nYou still have the remaining cards:");
                for (Card card : player.getActionDeck()) {
                    System.out.println("\t" + card.getDescription(player));
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
            if (enemies.size() == 1 || cardToPlay.getDamage() * cardToPlay.getHits() == 0 || cardToPlay.isAttackAll()) {
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
            System.out.println("You played " + cardToPlay.getName() + "!");
            if (cardToPlay.isAttackAll()) {
                player.playCard(cardToPlay, enemies);
            } else {
                player.playCard(cardToPlay, target);
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
            Card card = enemy.chooseCard();
            String also = "";
            // Enemy chooses cards until enemy's AP=0 or no valid options
            while (card != null) {
                enemy.intend(card);
                System.out.println(enemy.getName() + also + " plans to use " + card.getName() + ".");
                System.out.println("\t" + card.getDescription(enemy));
                card = enemy.chooseCard();
                also = " also";
            }
        }
    }

    /**
     * Checks if any enemy is dead and removes them from the battlefield.
     */
    private void checkForDeadEnemies() {
        for (Enemy enemy : enemies) {
            if (enemy.getHealth() <= 0) {
                int amount = enemy.getGold(); // the gold amount is randomized.
                player.addGold(amount);
                System.out.println("You have gained " + amount + " gold!");
                possibleCardDrops.addAll(enemy.getCardDrops());
            }
        }
        enemies.removeIf(enemy -> enemy.getHealth() <= 0);
    }

    /**
     * Each enemy plays the cards that they intended to do.
     */
    private void doEnemyAction() {
        checkForDeadEnemies();
        for (Enemy enemy : enemies) {
            enemy.turnStartStatReset();
        }
        for (Enemy enemy : enemies) {
            // Play enemy's cards that it intended to play
            while (!enemy.isIntendEmpty() && !player.isDead()) {
                Card card = enemy.getIntendedCard();
                System.out.println(enemy.getName() + " plays " + card.getName() + "!");
                Main.textWait();
                enemy.playCard(card, player);
                Main.textWait();
            }
            System.out.println(enemy.getName() + " has ended their turn.\n");
            Main.textWait();
        }
    }

    /**
     * Prints out the turn count, and the health of every being on the battlefield.
     * Also clears effects from the player.
     */
    private void displayStats() {
        System.out.println("--Turn " + turn + "--");
        player.turnStartStatReset();
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

    /**
     * Lets the player choose between a few drops from the enemies who have perished on the battlefield.
     */
    private void doCardAdding() {
        possibleCardDrops.removeIf(card -> Math.random() <= Main.DROP_CHANCE); // removes some cards from the possible drops.
        if (Main.DEBUGSTATS) {
            System.out.println(possibleCardDrops);
        }
        Card cardToAdd = null;
        if (possibleCardDrops.isEmpty()) {
            System.out.println("The enemies didn't drop anything...");
        } else if (possibleCardDrops.size() == 1) {
            cardToAdd = possibleCardDrops.get(0);
            System.out.println("After inspecting the battlefield, you discover " + cardToAdd.getName() + ".");
            System.out.println("\t" + cardToAdd.getDescription(player));
            if (!Main.yesNoPrompt("Do you want to add this card into your deck","","Invalid input.")) {
                cardToAdd = null;
            }
        } else {
            Collections.shuffle(possibleCardDrops);
            if (Main.DEBUGSTATS) {
                System.out.println("Debug: Possible Card Drops");
                for (int i = 0; i < possibleCardDrops.size(); i++) {
                    System.out.println("\t" + possibleCardDrops.get(i));
                }
            }
            Card cardToAdd1 = possibleCardDrops.get(0);
            System.out.println("After inspecting the battlefield, you discover " + cardToAdd1.getName() + " (1).");
            System.out.println("\t" + cardToAdd1.getDescription(player));
            Card cardToAdd2 = possibleCardDrops.get(1);
            System.out.println("And you also discover " + cardToAdd2.getName() + " (2).");
            System.out.println("\t" + cardToAdd2.getDescription(player));
            System.out.println("You can only add one card per battle.");
            int number = Main.numberPrompt("Which card number do you want to add to your deck (any other number for none?)","Card#", "Invalid input.");
            if (number == 1) {
                cardToAdd = cardToAdd1;
            } else if (number == 2) {
                cardToAdd = cardToAdd2;
            }
        }
        if (cardToAdd != null) {
            player.deckAdd(cardToAdd);
            System.out.println("You have added " + cardToAdd.getName() + " to your deck.");
        }
    }
}
