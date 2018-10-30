package edu.unh.cs.cs619.bulletzone.model;

import edu.unh.cs.cs619.bulletzone.R;

/** GridCell - The parent class of all basic Grid objects.
 *
 * @author Travis
 */
public class GridCell
{
    // class variables
    protected int row;
    protected int col;
    protected int serverVal;
    protected int resourceID;
    protected String cellType;

    /** GridCell - Constructor that takes 3 parameters required to create a GridCell object.
     *
     * @param val - integer containing raw server value of the item
     * @param r - integer containing the row of the item
     * @param c - integer containing the col of the item
     */
    public GridCell(int val, int index)
    {
        serverVal = val;
        row = index/16;
        col = index%16;

        // Empty Cell
        //if (val == 0 || (val > 4000 && val < 1000000))
        //{
            resourceID = R.drawable.blank;
            cellType = "Blank";
        //}
        //else
        //{
            //resourceID = R.drawable.blank;
            //cellType = "Blank";
        //}
    }

    /** getLocationHistory - gets the Location history of a GridCell. This method is
     *                       implemented so GardenerItem can override it and any other
     *                       GridCell will display an error.
     *
     * @return String - string containing location history
     */
    public String getLocationHistory()
    {
        String data = "";
        data += "Current GridCell is not a GardenerItem, no history to display!\n";
        return data;
    }

    /** getResourceID - returns the appropriate image resource identifier
     *
     * @return int - the integer representing the resourceID
     */
    public int getResourceID()
    {
        return resourceID;
    }

    /** getCellType - returns a string description of the object type in the cell
     *
     * @return String - the string with the current cells type
     */
    public String getCellType()
    {
        return cellType;
    }

    // returns a string describing other info about the object (such as location)

    /** getCellInfo - Returns a string describing other info about the object,
     *                  such as location and gardener object IDs.
     *
     * @return String - the string containing the cells information
     */
    public String getCellInfo()
    {
        return "Location: (" + row + ", " + col + ")";
    }

    public int getServerVal() { return serverVal; }
}
