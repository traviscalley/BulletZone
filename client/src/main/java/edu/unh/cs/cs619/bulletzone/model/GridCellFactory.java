package edu.unh.cs.cs619.bulletzone.model;

import java.util.HashMap;

/** GridCellFactory - Implements the Simple Factory pattern. Exists for the life of MainActivity.
 *
 * @author Travis Calley
 */
public class GridCellFactory
{
    private static GridCellFactory _instance;
    private HashMap<Integer, GardenerItem> gItems = new HashMap<Integer, GardenerItem>();

    /** GridCellFactory - default constructor needs not to be implemented. */
    private GridCellFactory() { }

    /** getInstance - get the Singleton instance of GridCellFactory. */
    public static GridCellFactory getInstance()
    {
        if (_instance == null)
            _instance = new GridCellFactory();
        return _instance;
    }

    public GridCell getGardenerItem(int key)
    {
        return gItems.get(key);
    }

    /** makeCell - makes a given cell from the raw input value taken from the server
     *              and is placed in the cells array at the given index.
     *
     * @param val - the raw value obtained from the server
     * @param index - the index of the current cell in the cell array
     * @return GridCell - the cell that was just created will be returned
     */
    public GridCell makeCell(int val, int index)
    {
        if(val >= 1000 && val < 2000)
            return new Wall(val, index);

        GridCell cell = null;

        // Empty Cell, Reserved
        if (val == 0 || (val > 4000 && val < 1000000))
            cell = new GridCell(val, row, col);

            // Plant: Tree, Clover, Mushroom, Sunflower
        else if (val == 1000 || (val > 1000 && val < 2000) ||
                val == 2002 || val == 2003 || val == 3000)
            cell = new Plant(val, row, col);

            // Gardener, Shovel, Cart
        else if ((val >= 1000000 && val < 2000000) || (val >= 2000000 && val < 3000000) ||
                (val >= 10000000 && val < 20000000))
        {
            int ser_val = (val / 1000);
            cell = gItems.get(ser_val);

            // checks if GardenerItem exists, if not create the
            // GardenerItem and add it to HashMap
            if (cell == null)
            {
                cell = new GardenerItem(val, row, col);
                gItems.put(ser_val, (GardenerItem)cell);
            }
            // GardenerItem exists so simply update the location
            else
                ((GardenerItem)cell).setLocation(row, col);
        }
        // Invalid
        else
            cell = new GridCell(val, row, col);

        return cell;
    }
}
