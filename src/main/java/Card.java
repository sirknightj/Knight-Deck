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
    private int shield;

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
    public Card(String name, int cost, boolean playable, int damage, int hits, int defense, boolean attackAll, int shield) {
        this.name = name;
        this.cost = cost;
        this.playable = playable;
        this.damage = damage;
        this.hits = hits;
        this.defense = defense;
        this.attackAll = attackAll;
        this.shield = shield;
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
     * @return Shield
     */
    public int getShield() {
        return shield;
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
     * @return The raw damage output of this card.
     */
    public int rawDamage() {
        return damage * hits;
    }

    /**
     * Causes this card to be applied by the user against the opponent. Also prints out the damage forecast
     * and state of the beings after damage was taken. Takes all of the opponent's defense and shield into account.
     *
     * @param user     Being that uses the card
     * @param opponent Being that user uses the card against
     */
    public void play(Being user, Being opponent) {
        if (damage * hits != 0) {
            printForecast(opponent);
            opponent.takeDamage(damage, hits);
            System.out.println("\t" + opponent.healthStatus());
        }
        if (defense != 0) {
            user.setDefense(user.getDefense() + defense);
            System.out.println("\t" + user.getName() + " now has " + user.getDefense() + " defense.");
        }
        if (shield != 0) {
            user.increaseShield(shield);
            System.out.println("\t" + user.getName() + " now has " + user.getShield() + " shield.");
        }
    }

    /**
     * @return all the stats about the card
     */
    public String toString() {
        return name + " [" + cost + "] [Att=" + damage + "x" + hits + ", Def=" + defense + ", AttackAll=" + attackAll + ", shield=" + shield + "]";
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
            description += " Applies " + defense + " defense.";
        }
        if (shield > 0) {
            description += " Applies " + shield + " shield.";
        }
        return description.trim();
    }

    /**
     * Forecasts the total damage dealt, based on the being's stats.
     *
     * @param being The intended target of the card.
     * @return a string forecast of the amount of damage the card will do.
     */
    public void printForecast(Being being) {
        if (being.getDefense() > 0) {
            if (being.getDefense() >= damage) {
                System.out.println("\t" + being.getName() + "'s defense mitigates all damage.");
            } else {
                System.out.println("\t" + being.getName() + "'s defense mitigates " + Math.min(being.getDefense(), damage) + ((hits > 1) ? "x" + hits : "") + " damage.");
            }
        }
        if (being.getShield() > 0 && being.getDefense() < damage) {
            System.out.println("\t" + being.getName() + "'s shield mitigates " + Math.min(being.getShield(), being.preShieldDamageCalculation(this)) + " damage.");
            if (being.preShieldDamageCalculation(this) >= being.getShield()) {
                System.out.println("\t" + name + " has broken " + being.getName() + "'s shields!");
            }
        }
        if (damage > 0 && hits > 0) {
            System.out.println("\t" + name + " deals " + being.damageCalculation(this) + " damage to " + being.getName() + ".");
        }
        if (defense > 0) {
            System.out.println("\t" + name + " applies " + defense + " defense to self.");
        }
        if (shield > 0) {
            System.out.println("\t" + name + " applies " + shield + " shield to self.");
        }
    }
}
