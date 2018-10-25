package tgc1002.gridsim.Model;

import tgc1002.gridsim.R;

/** Plant - Child class of GridCell represents all trees, bushes, and small plants.
 *
 * @author Travis Calley
 */
public class Plant extends GridCell
{
    /** Plant - constructor for the Plant GridCell object.
     *
     * @param val - the raw server value passed to the parent
     * @param r - the row value passed to the parent
     * @param c = the column value passed to the parent
     */
    public Plant(int val, int r, int c)
    {
        super(val, r, c);

        // Set the resource ID of the Plant GridCell
        if (val == 1000) // Tree
        {
            resourceID = R.drawable.tree;
            cellType = "Tree";
        }
        else if (val > 1000 && val < 2000) // Bush
        {
            resourceID = R.drawable.bushes;
            cellType = "Bush";
        }
        else if (val == 2002) // Clover
        {
            resourceID = R.drawable.clover;
            cellType = "Clover";
        }
        else if (val == 2003) // Mushroom
        {
            resourceID = R.drawable.mushroom;
            cellType = "Mushroom";
        }
        else if (val == 3000) // Sunflower
        {
            resourceID = R.drawable.sunflower;
            cellType = "Sunflower";
        }
        else // Catches all invalid values
        {
            resourceID = R.drawable.blank;
            cellType = "Blank";
        }
    }
}
