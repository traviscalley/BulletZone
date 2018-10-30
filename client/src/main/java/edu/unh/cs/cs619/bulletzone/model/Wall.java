package edu.unh.cs.cs619.bulletzone.model;

import edu.unh.cs.cs619.bulletzone.R;

/** Plant - Child class of GridCell represents all trees, bushes, and small plants.
 *
 * @author Travis Calley
 */
public class Wall extends GridCell
{
    /** Plant - constructor for the Plant GridCell object.
     *
     * @param val - the raw server value passed to the parent
     * @param r - the row value passed to the parent
     * @param c = the column value passed to the parent
     */
    public Wall(int val, int index)
    {
        super(val, index);

        // Set the resource ID of the Plant GridCell
        if (val == 1000) // Tree
        {
            resourceID = R.drawable.tree; //indestructible wall
            cellType = "Tree";
        }
        else if (val > 1000 && val < 2000) // Bush
        {
            resourceID = R.drawable.bushes; //destrictible wall
            cellType = "Bush";
        }
        else // Catches all invalid values
        {
            resourceID = R.drawable.blank;
            cellType = "Blank";
        }
    }
}
