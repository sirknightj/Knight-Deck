import java.util.*;

public class TextViewController {
    private GameModel model;
    private Player player;
    private Scanner input;

    public static final int TEXT_DELAY = 100; // the text delay in milliseconds.

    /**
     * Creates a new TextInterfaceController with the provided model and view
     * classes to manage.
     *
     * @param model A model to use for computation and data.
     */
    public TextViewController(GameModel model) {
        this.model = model;
        player = model.getPlayer();
        input = new Scanner(System.in);
    }

    public static void main(String[] args) {
        GameModel model = new GameModel();

        Scanner input = new Scanner(System.in);
        System.out.println("What is your name? ");
        System.out.print("Name> ");
        String name = input.nextLine().trim();

        model.loadPlayer(name);

        TextViewController viewController = new TextViewController(model);
        viewController.start();
    }

    public void start() {
        char response = ' ';
        while (response != 'q') {
            System.out.println(player.healthStatus());
            System.out.println("You have " + player.getGold() + " gold.");
            System.out.println("What would you like to do?");
            System.out.println("\tb to battle normally and get closer to saving the princess");
            System.out.println("\te to battle easier enemies and gain some more gold and cards");
            System.out.println("\th to visit the field hospital");
            System.out.println("\ts to check out the shop");
            System.out.println("\tq to quit");

            System.out.print("Action> ");
            response = input.nextLine().toLowerCase().charAt(0);
            System.out.println();

            handleMenuChoice(response);
            model.saveData();
            System.out.println();
        }


        model.saveData();
        System.out.println();
    }

    private void handleMenuChoice(char response) {
        switch (response) {
            case 'b':
                double difficulty = model.getDifficulty();
                if (difficulty > 16) {
                    startFinalBattle();
                } else {
                    BattleManager battle = model.startBattle(difficulty);
                    manageBattle(battle);
                }
                break;
            case 'e':
                double normalDifficulty = model.getDifficulty();
                BattleManager battle = model.startBattle(normalDifficulty < 2 ? normalDifficulty : normalDifficulty / 2);
                manageBattle(battle);
                break;
            case 'h':
                visitHospital();
                break;
            case 's':
                if (Shop.getInstance() == null) {
                    System.out.println("Shopkeepers: Sorry, we're currently sold out.");
                    textWait();
                    System.out.println("Shopkeepers: Could you come back in a bit? We'll have some more stock then.");
                    textWait();
                } else {
                    Shop.getInstance().enter(player);
                }
                break;
            case 'q':
                System.out.println("Thanks for playing Knight Deck!");
                break;
            default:
                System.out.println("Unrecognizable input.");
                break;
        }
    }

    private void manageBattle(BattleManager battle) {
        battle.start();
        System.out.println("=== Battle has started! ===");
        System.out.println();

        while (!battle.isBattleOver()) {
            // Print turn stats information
            BattleManager.TurnStat stats = battle.getCurrentStats();
            System.out.println("--Turn " + stats.getTurn() + "--");
            System.out.println(stats.getPlayer().healthStatus());
            for (Enemy enemy : stats.getEnemies()) {
                System.out.println(enemy.healthStatus());
            }
            System.out.println();

            battle.prePlayerTurn();

            // Print player's action deck
            System.out.println("You drew the following cards:");
            for (Card card : player.getActionDeck()) {
                System.out.println("\t" + card.getDescription(player));
            }

            getPlayerAction(battle);

            battle.preEnemyTurn();

            List<BattleManager.ActionSummary> enemyActions = battle.enemiesTurn();
            for (BattleManager.ActionSummary action : enemyActions) {
                Enemy enemy = (Enemy) action.getCardUser();
                Card card = action.getCardPlayed();
                System.out.println(enemy.getName() + " plays " + card.getName() + "!");
                textWait();
                enemy.playCard(card, player);
                textWait();
            }

            battle.postTurn();
        }

        System.out.print("=== Battle has finished! ===\n");

        // Add card drops
        Set<Card> cardDrops = battle.postGame();
        Card cardDropChosen = handleCardDropAdding(cardDrops);
        if (cardDropChosen != null) {
            player.deckAdd(cardDropChosen);
            System.out.println("You have added " + cardDropChosen.getName() + " to your deck.");
        }
    }

    private void getPlayerAction(BattleManager battle) {
        boolean firstTime = true;
        while (player.getActionPoints() > 0 && !player.isActionDeckEmpty() && !battle.isBattleOver()) {
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
            List<Enemy> enemies = battle.getEnemies();
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
            assert target != null;
            System.out.println("You played " + cardToPlay.getName() + "!");
            BattleManager.ActionSummary actionSummary = battle.playerAction(cardToPlay, target);
            firstTime = false;
        }

        if (battle.isBattleOver()) {
            System.out.println("Your turn has automatically ended because you have defeated all the enemies.");
        } else if (player.getActionPoints() == 0) {
            System.out.println("Your turn has automatically ended because you have no more action points.");
        } else if (player.isActionDeckEmpty()) {
            System.out.println("Your turn has automatically ended because you have no more cards in your hand.");
        }
    }

    /**
     * Lets the player choose between a few drops from the enemies who have perished on the battlefield.
     *
     * @param cardDropSet set of cards dropped by the enemies.
     * @return Card chosen by the player to add. Can be null if no card is chosen.
     */
    private Card handleCardDropAdding(Set<Card> cardDropSet) {
        if (cardDropSet.isEmpty()) {
            System.out.println("The enemies didn't drop anything...");
            return null;
        }

        List<Card> cardDrops = new ArrayList<>(cardDropSet);

        // Ask if user wants to add the only card dropped
        if (cardDrops.size() == 1) {
            Card onlyCard = cardDrops.get(0);
            System.out.println("After inspecting the battlefield, you discover " + onlyCard.getName() + ".");
            System.out.println("\t" + onlyCard.getDescription(player));
            boolean addToDeck = yesNoPrompt("Do you want to add this card into your deck", "",
                    "Invalid input.");
            return addToDeck ? onlyCard : null;
        }

        // Make user choose between two of the dropped cards
        Collections.shuffle(cardDrops);
        Card card1 = cardDrops.get(0);
        Card card2 = cardDrops.get(1);

        System.out.println("After inspecting the battlefield, you discover " + card1.getName() + " (1).");
        System.out.println("\t" + card1.getDescription(player));
        System.out.println("And you also discover " + card2.getName() + " (2).");
        System.out.println("\t" + card2.getDescription(player));
        System.out.println("You can only add one card per battle.");
        int number = numberPrompt("Which card number do you want to add to your deck (any other number for none?)",
                "Card#", "Invalid input.");

        if (number == 1) {
            return card1;
        } else if (number == 2) {
            return card2;
        }
        return null;
    }

    /**
     * Pits the player against the final boss.
     *
     * @throws IllegalStateException if the player beats the boss.
     */
    private void startFinalBattle() {
        System.out.println("Narrator: You finally reach the castle where the princess is being locked up.");
        textWait();
        System.out.println("Narrator: The moment has come. This is the final battle!");
        textWait();

        // Confirmation for player readiness. Exit if not ready
        boolean isUserReady = yesNoPrompt("Narrator: Are you ready? (y/n)", ">",
                "Narrator: Sorry, I don't understand.");
        if (!isUserReady) {
            System.out.println("Narrator: The final moments await. Ladies and gentlemen, please wait as " +
                    player.getName() + " does their final preparations!");
            return;
        }


        BattleManager finalBattle = model.startFinalBattle();
        manageBattle(finalBattle);

        if (!player.isDead()) { // player won
            System.out.println("Narrator: And thus, " + player.getName() + " has defeated the beast and saved the princess.");
            textWait();
            System.out.println("Princess: Oh, " + player.getName() + "! Thank you for saving me!");
            textWait();
            System.out.println("Narrator: And so, the brave knight and the beautiful princess lived happily ever after.");
            textWait();
            System.out.println("Narrator: The end.");
            throw new IllegalStateException("We haven't planned this far yet.");
        } else { // player lost
            System.out.println("Narrator: Unfortunately, our brave knight " + player.getName() + " has been defeated by the beast.");
            textWait();
            System.out.println("Narrator: Our princess still needs a rescuer. Who will stop the beast now?");
            textWait();
        }
    }

    /**
     * Driver for when the player visits the hospital.
     */
    private void visitHospital() {
        System.out.println("Cleric: Welcome to the field hospital.");
        textWait();
        if (player.getHealth() < player.getMaxHealth()) {
            if (player.getGold() < 1) {
                System.out.println("Cleric: I'm sorry, but I'm greedy. I need gold to heal you.");
                textWait();
            } else {
                System.out.println("Cleric: Let me take a look at your wounds...");
                textWait();
                System.out.println("Cleric: ...");
                textWait();
                System.out.println("Cleric: It looks like you're injured pretty badly.");
                textWait();
                int goldToHeal = (int) Math.ceil(Math.log(player.getMaxHealth() - player.getHealth()) / Math.log(1.4));
                if (player.getGold() >= goldToHeal) {
                    if (yesNoPrompt("Cleric: I can heal you all the way to full, but it'll cost you " +
                                    goldToHeal + " gold (y/n).\n\t(You have " + player.getGold() + " gold.)",
                            "", "Cleric: Sorry, I don't understand.")) {
                        System.out.println("Cleric: Get ready!");
                        textWait();
                        System.out.println("\t" + "Cleric used heal!");
                        textWait();
                        player.takeGold(goldToHeal);
                        player.heal(player.getMaxHealth() - player.getHealth());
                        System.out.println("\t" + player.healthStatus());
                        textWait();
                    }
                } else {
                    System.out.println("Cleric: Sorry, but you don't have enough gold to cover treatment.");
                    textWait();
                    System.out.println("Cleric: I really would like to heal you, but I'm poor and don't have any extra supplies.");
                    textWait();
                }
            }
        } else {
            System.out.println("Cleric: You don't appear to have any injuries.");
            textWait();
        }
        if (player.getHealth() == player.getMaxHealth()) {
            System.out.println("Cleric: Not saying you should get yourself injured, but come back when you need healing!");
        } else {
            System.out.println("Cleric: Come back soon!");
        }
        textWait();
    }

    public static void textWait() {
        try {
            Thread.sleep(TEXT_DELAY);
        } catch (InterruptedException e) {
            System.out.println("Interrupted Exception!");
        }
    }

    /**
     * Prompts the player for a yes or no (y/n) answer, repeating until they give a valid answer.
     *
     * @param prompt       The question in which to ask, excluding the " (y/n)?" afterwards.
     * @param enter        The prompt line the user types after (i.e "Card"), excluding the right bracket ">" and ending space.
     * @param errorMessage The message to play in case the user does not type in y or n.
     * @return True if yes. False if no.
     */
    public static boolean yesNoPrompt(String prompt, String enter, String errorMessage) {
        System.out.println(prompt + " (y/n)?");
        System.out.print(enter.trim() + "> ");
        Scanner input = new Scanner(System.in);
        String response = input.nextLine().trim().toLowerCase();
        while (!response.equals("y") && !response.equals("n")) {
            System.out.println(errorMessage);
            System.out.print(enter.trim() + "> ");
            response = input.nextLine().trim().toLowerCase();
        }
        return response.equalsIgnoreCase("y");
    }

    /**
     * Prompts the player to enter a number, repeating until they give a valid number.
     *
     * @param prompt       The question in which to ask.
     * @param enter        The prompt line the user types after (i.e. "Card"), excluding the right bracket ">" and ending space.
     * @param errorMessage The message to play in case the user does not type in a valid number.
     * @return The integer the user typed in.
     */
    public static int numberPrompt(String prompt, String enter, String errorMessage) {
        System.out.println(prompt);
        System.out.print(enter.trim() + "> ");
        Scanner input = new Scanner(System.in);
        while (!input.hasNextInt()) {
            input.nextLine();
            System.out.println(errorMessage);
            System.out.print(enter.trim() + "> ");
        }
        return input.nextInt();
    }
}
