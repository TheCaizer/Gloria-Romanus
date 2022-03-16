package unsw.gloriaromanus.backend.infrastructure;


/**
 * Interface for Wealth Generation Infrastructure.
 */
public interface WealthGenerating {

    /**
     * @return The scalar increase to province wealth.
     */
    public int getWealthBonus();

    /**
     * @return The scalar increase to province wealth growth.
     */
    public int getGrowthBonus();


    public default String getWealthInfo() {
        String s = "";
        s += "Growth Bonus: " + getGrowthBonus() + "\n";
        s += "Wealth Bonus: " + getWealthBonus() + "\n";
        return s;
    }
}