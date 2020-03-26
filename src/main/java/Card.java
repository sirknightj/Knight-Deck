import java.util.ArrayList;

/**
 * Immutable representation of a card.
 */
public class Card implements Comparable<Card> {
    private String name;
    private int cost;
    private boolean playable;
    private int damage;
    private int hits;
    private int defense;
    private boolean attackAll;
    private int shield;
    private boolean singleUse;
    private int strength;

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
     * @param singleUse True iff this card is only usable once per combat.
     * @param strength  The amount of strength buffs given when played.
     */
    public Card(String name, int cost, boolean playable, int damage, int hits, int defense, boolean attackAll, int shield, boolean singleUse, int strength) {
        this.name = name;
        this.cost = cost;
        this.playable = playable;
        this.damage = damage;
        this.hits = hits;
        this.defense = defense;
        this.attackAll = attackAll;
        this.shield = shield;
        this.singleUse = singleUse;
        this.strength = strength;
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
     * @return The strength buff to give the being playing this card.
     */
    public int getStrength() {
        return strength;
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
     * @return True iff the card is single use (per combat).
     */
    public boolean isSingleUse() {
        return singleUse;
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
            printForecast(user, opponent);
            opponent.takeDamage(damage + user.getStrength(), hits);
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
        if (strength != 0) {
            user.increaseStrength(strength);
            System.out.println("\t" + user.getName() + " now has " + user.getStrength() + " strength.");
        }
        if (isSingleUse()) {
            System.out.println("\t" + name + " has temporarily been removed from " + user.getName() + "'s deck.");
        }
    }

    /**
     * @return all the stats about the card
     */
    public String toString() {
        return name + " [" + cost + "] [Att=" + damage + "x" + hits + ", Def=" + defense + ", AttackAll=" + attackAll + ", shield=" + shield + ", singleUse=" + singleUse + "]";
    }

    /**
     * @return The description of the card.
     */
    public String getDescription() {
        return getDescription(new Player("", 0, 0, new ArrayList<>(), 0));
    }

    /**
     * * @return a description of what this card does, ignoring the things it doesn't do.
     */
    public String getDescription(Being user) {
        String ANSI_RESET = "\u001B[0m";
        String ANSI_GREEN = "\u001B[32m";
        if (user.strength == 0) {
            ANSI_GREEN = "";
            ANSI_RESET = "";
        }
        String description = name + " [" + cost + "]";
        if (damage > 0) {
            description += " Deals " + ANSI_GREEN + damage + ANSI_RESET + (hits != 1 ? "x" + hits : "") + " damage";
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
        if (strength > 0) {
            description += " Grants " + strength + " strength.";
        }
        if (singleUse) {
            description += " Can only be used once per battle.";
        }
        return description.trim();
    }

    /**
     * Forecasts the total damage dealt, based on the being's stats.
     *
     * @param target The intended target of the card.
     */
    public void printForecast(Being user, Being target) {
        String ANSI_RESET = "\u001B[0m";
        String ANSI_GREEN = "\u001B[32m";
        if (user.getStrength() == 0) {
            ANSI_RESET = "";
            ANSI_GREEN = "";
        }
        if (target.getDefense() > 0) {
            if (target.getDefense() >= damage + target.getStrength()) {
                System.out.println("\t" + target.getName() + "'s defense mitigates all damage.");
            } else {
                System.out.println("\t" + target.getName() + "'s defense mitigates " + ANSI_GREEN + Math.min(target.getDefense(), damage) + ANSI_RESET + ((hits > 1) ? "x" + hits : "") + " damage.");
            }
        }
        if (target.getShield() > 0 && target.getDefense() < damage) {
            if (target.getShield() <= target.preShieldDamageCalculation(user, this)) {
                System.out.println("\t" + target.getName() + "'s shield mitigates " + ANSI_GREEN + Math.min(target.getShield(), target.preShieldDamageCalculation(user, this)) + ANSI_RESET + " damage.");
            } else {
                System.out.println("\t" + target.getName() + "'s shield mitigates all (" + ANSI_GREEN + target.preShieldDamageCalculation(user, this) + ANSI_RESET + ") damage.");
            }
            if (target.preShieldDamageCalculation(user, this) >= target.getShield()) {
                System.out.println("\t" + name + " has broken " + target.getName() + "'s shields!");
            }
        }
        if (damage > 0 && hits > 0) {
            System.out.println("\t" + name + " deals " + ANSI_GREEN + target.damageCalculation(user, this) + ANSI_RESET + " damage to " + target.getName() + ".");
        }
        if (defense > 0) {
            System.out.println("\t" + name + " applies " + defense + " defense to self.");
        }
        if (shield > 0) {
            System.out.println("\t" + name + " applies " + shield + " shield to self.");
        }
        if (strength > 0) {
            System.out.println("\t" + name + " applies " + strength + " strength to self.");
        }
    }

    /**
     * Compares this card to another card, returning an integer depending on the result.
     *
     * @param other the card to compare this card to
     * @return int > 1 if this card is greater than the other card, 0 if they are equal, and int < 0 if the other card is greater.
     */
    @Override
    public int compareTo(Card other) {
        return name.compareTo(other.getName());
    }
}
