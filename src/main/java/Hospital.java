/**
 * This contains all of the hospital functionality.
 */
public class Hospital {

    private Player player;

    /**
     * Sets the player the hospital is observing. This has to be the first method called by the hospital.
     * @param player the player to be observed.
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Calculates and returns the cost of healing the player in the hospital.
     * @return the cost to heal the player to full.
     */
    public int getHealingCost() {
        assert player != null;
        return (int) Math.ceil(Math.log(player.getMaxHealth() - player.getHealth()) / Math.log(1.4));
    }

    /**
     * @return true iff the player has enough gold to be healed.
     */
    public boolean playerHasEnoughGold() {
        assert player != null;
        return getHealingCost() <= player.getGold();
    }

    /**
     * @return true iff the player needs to be healed.
     */
    public boolean playerNeedHealing() {
        assert player != null;
        return player.getHealth() < player.getMaxHealth();
    }

    /**
     * Heals the player to full and takes away the the appropriate amount of gold.
     */
    public void healPlayerToFull() {
        assert player != null;
        player.takeGold(getHealingCost());
        player.heal(player.getMaxHealth() - player.getHealth());
    }

    /**
     * Holds the hospital.
     */
    private static class HospitalHolder {
        private static final Hospital INSTANCE = new Hospital();
    }

    /**
     * @return The current instance of the hospital.
     */
    public static Hospital getInstance() {
        return HospitalHolder.INSTANCE;
    }
}
