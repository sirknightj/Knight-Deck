import java.util.*;

public class BattleManager {
    private Player player; // the player to battle.
    private List<Enemy> enemies; // the enemies to battle.
    private int turn; // the current turn number.
    private Set<Card> possibleCardDrops; // the card drops from all the enemies combined.

    // contains exactly one element: the Player. This is necessary/efficient due to the way ActionSummary is structured
    private final List<Being> listWithOnlyPlayer;

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

    public TurnStat getCurrentStats() {
        return new TurnStat(turn, player, new ArrayList<>(enemies));
    }

    public void prePlayerTurn() {
        player.turnStartStatReset();
        player.drawCards();
    }

    public List<Enemy> getEnemies() {
        return new ArrayList<>(enemies);
    }

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

        return new ActionSummary(cardPlayed, player, opponents, goldGained, isBattleOver());
    }

    public void preEnemyTurn() {
        for (Enemy enemy : enemies) {
            enemy.turnStartStatReset();
            enemy.drawCards();
        }
    }

    public List<ActionSummary> enemiesTurn() {
        List<ActionSummary> actionSummaries = new ArrayList<>();

        for (Enemy enemy : enemies) {
            // Play enemy's cards
            List<Card> move = enemy.getMove();
            for (Card card : move) {
//                enemy.playCard(card, player);
                actionSummaries.add(new ActionSummary(card, enemy, listWithOnlyPlayer, 0, false));
            }
        }

        return actionSummaries;
    }

    public void postTurn() {
        turn++;
    }

    public Set<Card> postGame() {
        // removes some cards from the possible drops.
        possibleCardDrops.removeIf(card -> Math.random() <= GameModel.DROP_CHANCE);
        return possibleCardDrops;
    }

    public static class ActionSummary {
        private Card cardPlayed;
        private Being user;
        private List<Being> opponents;
        private int goldGained;
        private boolean isGameOver;

        public ActionSummary(Card cardPlayed, Being user, List<Being> opponents, int goldGained, boolean isGameOver) {
            this.cardPlayed = cardPlayed;
            this.user = user;
            this.opponents = opponents;
            this.goldGained = goldGained;
            this.isGameOver = isGameOver;
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

        public boolean didEndGame() {
            return isGameOver;
        }
    }

    public static class TurnStat {
        private int turn;
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
