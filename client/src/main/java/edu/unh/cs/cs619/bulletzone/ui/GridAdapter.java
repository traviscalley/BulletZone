package edu.unh.cs.cs619.bulletzone.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.SystemService;

import edu.unh.cs.cs619.bulletzone.R;
import edu.unh.cs.cs619.bulletzone.util.GridWrapper;

@EBean
public class GridAdapter extends BaseAdapter {

    private final Object monitor = new Object();
    @SystemService
    protected LayoutInflater inflater;
    private final int NUM_IMAGES = 16 * 16;
    private int[] imageId = new int[NUM_IMAGES];
    private Context context;
    //private int[][] mEntities = new int[16][16];

    public GridAdapter(Context c)
    {
        context = c;

        // by default set all grid values to 0
        for (int i = 0; i < NUM_IMAGES; i++)
            imageId[i] = R.drawable.blank;
    }

    public void updateList(int[] entities) {
        synchronized (monitor) {
            this.imageId = entities;
            this.notifyDataSetChanged();
        }
    }

    public void update(GridWrapper simGrid)
    {
        for (int i = 0; i < NUM_IMAGES; i++)
            imageId[i] = simGrid.getCell(i).getResourceID();
    }

    @Override
    public int getCount() {
        return NUM_IMAGES;
    }

    @Override
    public Object getItem(int position) {
        return imageId[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        //what is this
        /*if (convertView == null) {
            convertView = inflater.inflate(R.layout.field_item, null);
        }*/

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


