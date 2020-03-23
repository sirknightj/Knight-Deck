/**
 * Immutable representation of a card.
 */
public class Card {
    private String name;
    private int cost;
    private boolean playable;
    private int damage;
    private int hits;
    private int defense;
    private boolean attackAll;

    /**
     * Constructor. Defense cards should have hits = 0.
     *
     * @param name      Name of the card.
     * @param cost      Cost to play this card.
     * @param playable  True iff the player can play this card
     * @param damage    Amount of damage done to opponent
     * @param hits      Number of times this damage deals damage to opponent
     * @param defense   Amount of defense to add to the playing Being
     * @param attackAll True iff this does damage to all enemies.
     */
    public Card(String name, int cost, boolean playable, int damage, int hits, int defense, boolean attackAll) {
        this.name = name;
        this.cost = cost;
        this.playable = playable;
        this.damage = damage;
        this.hits = hits;
        this.defense = defense;
        this.attackAll = attackAll;
    }

    /**
     * @return Name of the card.
     */
    public String getName() {
        return name;
    }

    /**
     * @return Cost to play this card.
     */
    public int getCost() {
        return cost;
    }

    /**
     * @return Damage value of each hit.
     */
    public int getDamage() {
        return damage;
    }

    /**
     * @return Number of hits the card performs.
     */
    public int getHits() {
        return hits;
    }

    /**
     * @return Defense
     */
    public int getDefense() {
        return defense;
    }

    /**
     * @return True iff the card does damage to all enemies.
     */
    public boolean isAttackAll() {
        return attackAll;
    }

    /**
     * @param being Being to check against
     * @return True iff the card can be played by the given Being
     */
    public boolean isPlayableBy(Being being) {
        if (being instanceof Player) {
            return playable;
        }
        return !playable;
    }

    /**
     * Causes this card to be applied by the user against the opponent. Also prints out the damage forecast
     * and state of the beings after damage was taken.
     *
     * @param user     Being that uses the card
     * @param opponent Being that user uses the card against
     */
    public void play(Being user, Being opponent) {
        if (damage * hits != 0) {
            System.out.println("\t" + forecast(opponent));
            opponent.takeDamage(damage, hits);
            System.out.println(opponent.healthStatus());
        }
        if (defense != 0) {
            user.setDefense(user.getDefense() + defense);
            System.out.println("\t" + user.getName() + " now has " + user.getDefense() + " defense.");
        }
    }

    /**
     * @return all the stats about the card
     */
    public String toString() {
        return name + " [" + cost + "] [Att=" + damage + "x" + hits + ", Def=" + defense + ", AttackAll=" + attackAll + "]";
    }

    /**
     * @return a description of what this card does, ignoring the things it doesn't do.
     */
    public String getDescription() {
        String description = name + " [" + cost + "]";
        if (damage > 0) {
            description += " Deals " + damage + (hits != 1 ? "x" + hits : "") + " damage";
            if (attackAll) {
                description += " to all enemies.";
            } else {
                description += ".";
            }
        }
        if (defense > 0) {
            description += " Applies " + defense + " defense. ";
        }
        return description.trim();
    }

    /**
     * Forecasts the total damage dealt, based on the being's stats.
     *
     * @param being The intended target of the card.
     * @return a string forecast of the amount of damage the card will do.
     */
    public String forecast(Being being) {
        String output = name;
        if (damage > 0) {
            output += " deals " + Math.max(damage - being.getDefense(), 0);
        }
        if (hits > 1) {
            output += "x" + hits;
        }
        if (damage * hits > 0) {
            output += " damage to " + being.getName();
        }
        if (defense > 0) {
            if (damage * hits > 0) {
                output += " and";
            }
            output += " applies " + defense + " defense to self";
        }
        return output + ".";
    }
}
