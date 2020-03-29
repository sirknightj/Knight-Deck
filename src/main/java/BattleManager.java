import java.util.*;

/**
 * Represents a battle between the Player and one or more Enemies.
 *
 * Call methods in this order:
 * - BattleManager
 * - start
 * - loop while !isBattleOver
 * - [optional] getCurrentStats
 * - prePlayerTurn
 * - loop as needed:
 * - playerAction
 * - preEnemyTurn
 * - enemiesTurn
 * - postTurn
 * - postGame
 */
public class BattleManager {
    private Player player; // the player to battle.
    private List<Enemy> enemies; // the enemies to battle.
    private int turn; // the current turn number.
    private Set<Card> possibleCardDrops; // the card drops from all the enemies combined.

    // contains exactly one element: the Player. This is necessary/efficient due to the way ActionSummary is structured
    private final List<Being> listWithOnlyPlayer;

    /**
     * Constructor.
     *
     * @param player  Player
     * @param enemies List of Enemies on the battlefield
     */
    public BattleManager(Player player, List<Enemy> enemies) {
        this.player = player;
        this.enemies = enemies;
        assert player != null;
        assert !enemies.isEmpty();

        turn = 1;
        possibleCardDrops = new HashSet<>();

        if (enemies.size() > 1) {
            int n = 0;
            for (Enemy enemy : enemies) {
                enemy.addToName(" (" + (++n) + ")");
            }
        }

        listWithOnlyPlayer = new ArrayList<>(1);
        listWithOnlyPlayer.add(player);
    }

    /**
     * Sets up the player's and enemies' decks. Must be called after the constructor and before any other methods.
     */
    public void start() {
        player.initializeDeck();
        for (Enemy enemy : enemies) {
            enemy.initializeDeck();
        }
    }

    /**
     * The battle is over when the player has lost all of their health, or if
     * there are no more enemies remaining on the field.
     *
     * @return true iff the battle is over.
     */
    public boolean isBattleOver() {
        return player.isDead() || enemies.isEmpty();
    }

    /**
     * @return TurnStat representing battle stat at the beginning of this turn.
     */
    public TurnStat getCurrentStats() {
        return new TurnStat(turn, player, new ArrayList<>(enemies));
    }

    /**
     * @return List of alive enemies.
     */
    public List<Enemy> getEnemies() {
        return new ArrayList<>(enemies);
    }

    /**
     * Resets the player's stats and fills their action deck. Must be called before the player's turn.
     */
    public void prePlayerTurn() {
        player.turnStartStatReset();
        player.drawCards();
    }

    /**
     * Plays the given card against the given target (or all enemies if card.isAttackAll()).
     * Returns an ActionSummary representing the consequences of this single action.
     * IMPORTANT: the client of this method must NOT execute cardPlayed.playCard.
     *
     * @param cardPlayed Card in the player's action deck with sufficient action points
     * @param target     Enemy to attack (may be null for defensive or attack all cards)
     * @return ActionSummary representing result of this action
     */
    public ActionSummary playerAction(Card cardPlayed, Enemy target) {
        assert cardPlayed != null;
        assert target != null;

        List<Being> opponents = new ArrayList<>();

        if (cardPlayed.isAttackAll()) {
            player.playCard(cardPlayed, enemies);
            opponents.addAll(enemies);
        } else {
            player.playCard(cardPlayed, target);
            opponents.add(target);
        }

        // Remove dead enemies and reward user for them
        int goldGained = 0;
        for (Enemy enemy : enemies) {
            if (enemy.isDead()) {
                goldGained += enemy.getDroppedGold();
                possibleCardDrops.addAll(enemy.getCardDrops());
            }
        }
        enemies.removeIf(Being::isDead);
        player.addGold(goldGained);

        return new ActionSummary(cardPlayed, player, opponents, goldGained);
    }

    /**
     * Resets all enemies's stats and fills their action decks. Must be called before the enemies' turn.
     */
    public void preEnemyTurn() {
        for (Enemy enemy : enemies) {
            enemy.turnStartStatReset();
            enemy.drawCards();
        }
    }

    /**
     * Calculates and return a list of ActionSummaries of their intended move.
     * There may be more ActionSummaries than enemies since enemies may play multiple cards.
     * IMPORTANT: the cards in the action summaries must be executed by the client of this method.
     *
     * @return List of ActionSummaries representing the card actions of the alive enemies.
     */
    public List<ActionSummary> enemiesTurn() {
        List<ActionSummary> actionSummaries = new ArrayList<>();

        for (Enemy enemy : enemies) {
            // Play enemy's cards
            List<Card> move = enemy.getMove();
            for (Card card : move) {
                actionSummaries.add(new ActionSummary(card, enemy, listWithOnlyPlayer, 0));
            }
        }

        return actionSummaries;
    }

    /**
     * Increases turn count by 1. Must be called after every turn.
     */
    public void postTurn() {
        turn++;
    }

    /**
     * Must be called after every game, regardless of whether the player wins or not.
     *
     * @return Set of Cards representing the dropped cards by the killed enemies. May be empty.
     */
    public Set<Card> postGame() {
        // removes some cards from the possible drops.
        possibleCardDrops.removeIf(card -> Math.random() <= GameModel.DROP_CHANCE);
        return possibleCardDrops;
    }

    /**
     * Represents the consequences of a given play of a Card.
     */
    public static class ActionSummary {
        private Card cardPlayed;
        private Being user;
        private List<Being> opponents;
        private int goldGained;

        public ActionSummary(Card cardPlayed, Being user, List<Being> opponents, int goldGained) {
            this.cardPlayed = cardPlayed;
            this.user = user;
            this.opponents = opponents;
            this.goldGained = goldGained;
        }

        public Card getCardPlayed() {
            return cardPlayed;
        }

        public Being getCardUser() {
            return user;
        }

        public List<Being> getOpponents() {
            return opponents;
        }

        public int getGoldGained() {
            return goldGained;
        }
    }

    /**
     * Represents the battle state at any point in time.
     */
    public static class TurnStat {
        private int turn; // turn number
        private Player player;
        private List<Enemy> enemies;

        public TurnStat(int turn, Player player, List<Enemy> enemies) {
            this.turn = turn;
            this.player = player;
            this.enemies = enemies;
        }

        public int getTurn() {
            return turn;
        }

        public Player getPlayer() {
            return player;
        }

        public List<Enemy> getEnemies() {
            return enemies;
        }
    }
}
