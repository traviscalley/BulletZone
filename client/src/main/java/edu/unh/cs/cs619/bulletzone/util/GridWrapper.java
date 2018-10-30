package edu.unh.cs.cs619.bulletzone.util;

import edu.unh.cs.cs619.bulletzone.model.GridCell;

/**
 * Created by simon on 10/1/14.
 */
public class GridWrapper {
    // constants
    private final int NUM_ROWS = 16;
    private final int NUM_COLS = 16;
    private final int SIZE = 256;

    private GridCell[] grid;

    private long timeStamp;

    public GridWrapper() {
        grid = new GridCell[SIZE];
    }

    public GridWrapper(GridCell[] grid) {
        this.grid = grid;
    }

    public GridCell[] getGrid() {
        return this.grid;
    }

    public int[] getResourceGrid() {
        int[] res = new int[SIZE];
        for(int i = 0; i < SIZE; i++)
            res[i] = grid[i].getResourceID();
        return res;
    }

    public void setGrid(GridCell[] grid) {
        this.grid = grid;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    // returns the GridCell at a given linear index
    public GridCell getCell(int index)
    {
        return grid[index];
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
        grid[index] = cell;
    }

    // returns the linear size of the grid
    public int size()
    {
        return SIZE;
    }
}
