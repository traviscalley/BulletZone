package tgc1002.gridsim.Model;

/** SimulationGrid - This is basically a collection that holds GridCells in array form
 *                  for quicker access.
 *
 * @author Travis Calley
 */
public class SimulationGrid
{
    // constants
    private final int NUM_ROWS = 16;
    private final int NUM_COLS = 16;
    private final int SIZE = 256;

    // class variables
    private GridCell[] cells;

    /** SimulationGrid - Default Constructor
     */
    public SimulationGrid()
    {
        cells = new GridCell[SIZE];
    }

    // returns the GridCell at a given linear index
    public GridCell getCell(int index)
    {
        return cells[index];
    }

    // returns the number of rows in the grid
    public int getNumRows()
    {
        return NUM_ROWS;
    }

    // returns the number of columns in the grid
    public int getNumCols()
    {
        return NUM_COLS;
    }

    // sets the GridCell at a given linear index
    public void setCell(int index, GridCell cell)
    {
            cells[index] = cell;
    }

    // returns the linear size of the grid
    public int size()
    {
        return SIZE;
    }
}
