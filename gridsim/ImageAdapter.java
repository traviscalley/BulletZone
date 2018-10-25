package tgc1002.gridsim;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import tgc1002.gridsim.Model.SimulationGrid;

/** ImageAdapter - responsible for displaying and updating the contents
 *                  displayed in the gridView object
 *
 * @author Travis Calley
 */
class ImageAdapter extends BaseAdapter
{
    private final int NUM_IMAGES = 16 * 16;
    private int[] imageId = new int[NUM_IMAGES];
    private Context context;

    /** ImageAdapter - takes a context in which to load the ImageAdapter and
                      by default sets all grid values to 0.
     *
     * @param c - Context
     */
    public ImageAdapter(Context c)
    {
        context = c;

        // by default set all grid values to 0
        for (int i = 0; i < NUM_IMAGES; i++)
            imageId[i] = R.drawable.blank;
    }

    /** update - Takes a SimulationGrid object and updates with the
     *           current values grabbed from the server.
     *
     * @param simGrid - the SimulationGrid object containing the GridCells.
     */
    public void update(SimulationGrid simGrid)
    {
        for (int i = 0; i < NUM_IMAGES; i++)
            imageId[i] = simGrid.getCell(i).getResourceID();
    }

    /** getCount
     *
     * @return int - the size of the array storing the imageIds
     */
    public int getCount()
    {
        return imageId.length;
    }

    /** getItem - had to implement for inheritance reasons. */
    public Object getItem(int position)
    {
        return null;
    }

    /** getItemId - had to implement for inheritance reasons. */
    public long getItemId(int position)
    {
        return imageId[position];
    }

    /** getView - initializes the imageView and sets the image resources.
     *
     * @param position - the current position in the GridCell array
     * @param convertView - the view in which the GridCells are displayed
     * @param parent - the parent of the imageView object
     * @return View - returns the newly made imageView
     */
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ImageView imageView;
        if (convertView == null)
        {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(80, 65));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(5, 5, 5, 5);
        }
        else
            imageView = (ImageView) convertView;

        imageView.setImageResource(imageId[position]);
        return imageView;
    }
}