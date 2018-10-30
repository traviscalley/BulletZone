package edu.unh.cs.cs619.bulletzone.model;


// IMPORTS ================================================

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;

import edu.unh.cs.cs619.bulletzone.R;
import edu.unh.cs.cs619.bulletzone.ui.GridAdapter;
import edu.unh.cs.cs619.bulletzone.util.GridWrapper;

// imports for JSON parsing
// ========================================================

/** SimGridFacade - Class that implements the Facade pattern. It exists
 *                 for the life of the MainActivity. This class hides
 *                 ImageAdapter, GridView, TextView, GridCell, GridCellFactory,
 *                 SimulationGrid from the MainActivity.
 *
 * @author Travis Calley
 */
public class GridFacade implements GridSubscriber
{
    // class variables
    private GridView grid;
    private GridAdapter iAdapter;
    private GridCellFactory cellFactory;
    private GridCell[] cells;
    private GridWrapper simGrid;
    private TextView textView;

    private GridCell selectedCell;
    private int selectedPosition;
    private Boolean isPaused;

    /** SimGridFacade - Default constructor that initializes all of the
     *                  class variables declared above.
     *
     * @param context - Context of the MainActivity, allows initialization
     *                  of the ImageAdapter.
     * @param activity - The MainActivity object, allows to findViewById.
     */
    public GridFacade(Context context, Activity activity)
    {
        // handle a non-null activity and context
        if (context != null && activity != null)
        {
            // initialize textView
            /*textView = activity.findViewById(R.id.textViewCoordinates);
            textView.setText("Selected NULL\nLocation: (NULL, NULL)");*/

            // on creation initialize the GridView elements
            iAdapter = new GridAdapter(context);

            grid = activity.findViewById(R.id.gridView);
            grid.setAdapter(iAdapter);
            /*grid.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id)
                {
                    GridCell cell = simGrid.getCell(position);
                    selectedCell = cell;
                    selectedPosition = position;

                    String displayString = "Selected " + cell.getCellType() +
                            "\n" + cell.getCellInfo();

                    Log.d("gridView", "" + position);
                    Log.d("Raw Value", ""+cell.getServerVal());
                    textView.setText(displayString);
                }
            });*/
        }

        cellFactory = GridCellFactory.getInstance();
        cells = new GridCell[256];
        simGrid = new GridWrapper();
        isPaused = false;

        onStart();
    }

    /** showDetailsForSelected - returns a string already formatted to be logged.
     *
     * @return String - string of history information for current cell
     */
    /*public String showDetailsForSelected()
    {
        String data;

        data = selectedCell.getCellInfo() + " " + selectedCell.getCellType() + "\n";
        data += selectedCell.getLocationHistory();

        return data;
    }*/

    /** setUsingJSON - fills the active SimulationGrid with the appropriate
     *                 information from a JSONArray coming from the server.
     *
     * @param arr - the entire JSONArray of arrays grabbed from the server
     */
    public void setUsingJSON(JSONArray arr)
    {
        int index = 0; // current index in the imgId array
        GridCell curCell;

        try {
            // loop through all arrays in JSON request
            for (int i = 0; i < arr.length(); i++) {
                JSONArray intArray = arr.getJSONArray(i);

                // loop through each object in each array
                // write each object to the current index of imgId
                for (int j = 0; j < intArray.length(); j++) {
                    curCell = cellFactory.makeCell(intArray.getInt(j), index);
                    simGrid.setCell(index++, curCell);
                }
            }
        } catch (JSONException e) {
            Log.d("ERROR", "Could not fetch data from server");
            e.printStackTrace();
        }

        // update the adapter with the values grabbed from the JSON object
        // and invalidate the view to display the new values
        if (isPaused == false && iAdapter != null && grid != null)
        {
            iAdapter.update(simGrid);
            grid.invalidateViews();
        }
    }

    public Boolean togglePause()
    {
        isPaused = !isPaused;
        return isPaused;
    }

    // SUBSCRIBER METHODS ===========================================
    /** onStart - register the SimGridFacade to get data from EventBus. */
    @Override
    public void onStart()
    {
        EventBus.getDefault().register(this);
    }

    /** onStop - unregister the SimGridFacade to get data from EventBus. */
    @Override
    public void onStop()
    {
        EventBus.getDefault().unregister(this);
    }

    /** onEvent - handle the data received from the EventBus. */
    @Subscribe
    public void onEvent(JSONArray array)
    {
        // if the cell is not paused update;
        if (isPaused == false)
        {
            // update the gridView
            this.setUsingJSON(array);

            selectedCell = simGrid.getCell(selectedPosition);
            // update the textView
            String displayString = "Selected " + selectedCell.getCellType() +
                    "\n" + selectedCell.getCellInfo();
            textView.setText(displayString);
        }
    }
    // ==============================================================
}
