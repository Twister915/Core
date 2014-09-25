package net.cogzmc.core.player;

/**
 * Controls a player scoreboard more by line.
 */
public interface CPlayerScoreboardManager {

    /**
     * Returns the player that this Scoreboard manager is affiliated with
     * @return  Linked player
     */
    public CPlayer getPlayer();

    /**
     * Returns the title of the scoreboard
     * @return  Title of the scoreboard
     */
    public String getTitle();

    /**
     * Sets a new title for the scoreboard
     * @param title Scoreboard title
     */
    public void setTitle(String title);

    /**
     * Retrieves one of the lines from the scoreboard
     * @param i Index of the line to retrieve
     * @return  String being displayed
     */
    public String getLine(Integer i);

    /**
     * Sets a certain line on the scoreboard to the string
     * @param i Line to set
     * @param toSet String to set
     */
    public void setLine(Integer i, String toSet);

    /**
     * Sets the scoreboard length to this new size. Will trim if needed.
     * @param i New size of the scoreboard
     */
    public void setToSize(Integer i);

}
