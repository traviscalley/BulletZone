package edu.unh.cs.cs619.bulletzone.model;

import java.util.LinkedList;

import edu.unh.cs.cs619.bulletzone.R;

/** GardnerItem - Child class of GridCell represents all gardeners, shovels, and carts.
 *               The Gardener ID should be included in the info shown on screen.
 * @author Travis Calley
 */
public class TankItem extends GridCell
{
    private int TID; // GardenerID
    private int life;
    private int direction;
    private LinkedList<String> locHistory; // Location History

    /** GardenerItem - constructor for the GardenerItem
     *
     * @param val - raw server value passed to parent
     * @param r - row value passed to parent
     * @param c - column value passed to parent
     */
    public TankItem(int val, int index)
    {
        super(val, index);
        /*locHistory = new LinkedList<String>();

        String data = "(" + r + ", " + c + ") " + System.currentTimeMillis() + "\n";
        locHistory.add(data);*/

        // Set the resourceID of the GardenerItem GridCell
        if (val >= 10000000 && val < 20000000) // tank
        {
            super.resourceID = R.drawable.golfcart_icon; //tank
            cellType = "Golf cart";
            TID = val % 10000000;
            TID /=  10000;

            life = val % 10000;
            life /= 10;

            direction = val % 10;
        }
    }

    /** getCellInfo - returns the Gardener ID as well as the parents info.
     *
     * @return String - string containing the current cells info
     */
    public String getCellInfo()
    {
        return super.getCellInfo() + "\nGardener ID: " + TID;
    }

    /** getGID
     * @return int - return the GardenerID
     */
    public int getTID()
    {
        return TID;
    }

    /** setLocation - method to allow updating of GardenerItem since they
     *                are only created once in GridCellFactory.
     * @param r - row
     * @param c - column
     */
    public void setLocation(int r, int c)
    {
        String data = "(" + r + ", " + c + ") " + System.currentTimeMillis() + "\n";

        synchronized (this) // needs to be thread safe to create accurate list of history
        {
            if (!(row == r && col == c))
                locHistory.add(data);
        }

        row = r;
        col = c;
    }

    /** addLocationHistory - Add a string of data to the Location History LinkedList.
     *
     * @param data - String containing the location and timestamp
     */
    public void addLocationHistory(String data) { locHistory.add(data); }

    /** getLocationHistory - Prints all strings in locHistory.
     *
     * @return String - formatted string containing location history
     */
    public String getLocationHistory()
    {
        String data = "";

        Object arr[] = locHistory.toArray();
        for (int i = 0; i < locHistory.size(); i++)
            data += (String)arr[i];

        return data;
    }
}
