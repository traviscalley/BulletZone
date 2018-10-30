package edu.unh.cs.cs619.bulletzone.model;

import java.util.HashMap;

/** GridCellFactory - Implements the Simple Factory pattern. Exists for the life of MainActivity.
 *
 * @author Travis Calley
 */
public class GridCellFactory
{
    private static GridCellFactory _instance;
    private HashMap<Integer, TankItem> gItems = new HashMap<>();

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
            return new WallItem(val, index);
        else if(val >= 2000000 && val < 3000000)
            return new BulletItem(val, index);
        else if(val >= 10000000 && val < 20000000)
        {
            int key = val/10000;
            TankItem ti = gItems.get(key);

            if(ti == null)
            {
                ti = new TankItem(val, index);
                gItems.put(key, ti);
            }
            ti.setLocation(index/16, index%16);
            return ti;
        }
        else
            return new GridCell(val, index);
    }
}
