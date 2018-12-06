package edu.unh.cs.cs619.bulletzone.ui;

import android.content.Context;
import android.media.Image;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.SystemService;

import java.util.ArrayList;

import edu.unh.cs.cs619.bulletzone.R;
import edu.unh.cs.cs619.bulletzone.database.GridRepo;
import edu.unh.cs.cs619.bulletzone.events.BusProvider;

/**
 * Bridges between UI components and the data source that fill data into UI Component
 */
@EBean
public class GridAdapter extends BaseAdapter {

    /**
     * Class Variables
     */
    private final Object monitor = new Object();
    //@SystemService
    protected LayoutInflater inflater;
    private int[][] mEntities = new int[16][16];
    private int[][] prevEntities = new int[16][16];
    //TODO implememnt a delta int list that sores last state so ou don't hvae to make a new view every time
    private long playerID;
    private Context context;
    private ViewFactory viewFactory = ViewFactory.getInstance();

    private BusProvider busProvider;


    public GridAdapter(Context c)
    {
        context = c;
        inflater = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        busProvider = new BusProvider();
        viewFactory.setContext(context);
    }

    public void updateList(int[][] entities) {
        synchronized (monitor) {
            this.prevEntities = this.mEntities;
            this.mEntities = entities;
            this.notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return 16 * 16;
    }

    @Override
    public Object getItem(int position) {
        return mEntities[(int) position / 16][position % 16];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setPlayerID(long tankID)
    {
        playerID = tankID;
        viewFactory.setMyID((int)tankID);
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file.
     *
     * @param position The position of the item within the adapter's data set of the item whose view
     *        we want.
     * @param convertView The old view to reuse, if possible.
     * @param parent The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.field_item, null);
        }

        int row = position / 16;
        int col = position % 16;

        int val = mEntities[row][col];

        //if it hasn't changed
        //if(val == prevEntities[row][col])
            //return convertView;

        int TID = (val / 10000) % 1000;
        int dir = val % 10;
        int health = (val / 10) % 1000; //i think so

        //convertView.locat


        if (convertView instanceof ImageView) {
            synchronized (monitor) {
                return viewFactory.makeCellView((ImageView) convertView, val);
                /*int resource = 0;
                if (val > 0) {
                    if (val == 1000)
                        resource = R.drawable.wall;
                    else if (val > 1000 && val < 2000)
                        resource = R.drawable.wall_breakable;
                    else if (val == 2000)
                        resource = R.drawable.hill;
                    else if (val == 3000)
                        resource = R.drawable.debris_field;
                    else if (val == 4000)
                        resource = R.drawable.coast;
                    else if (val == 5000)
                        resource = R.drawable.water;
                    else if (val == 6000)
                        resource = R.drawable.antigrav;
                    else if (val == 7000)
                        resource = R.drawable.fusion;
                    else if (val == 8000)
                        resource = R.drawable.powerrack;
                    else if (val >=  2000000 && val <  3000000)
                        resource = R.drawable.bullet;
                    else if (val >= 10000000 && val < 20000000)
                    {
                        // check if player tank or enemy tank
                        if (TID != playerID)
                        {
                            if (dir == 0)
                                resource = R.drawable.enemy_tank_up;
                            else if (dir == 2)
                                resource = R.drawable.enemy_tank_right;
                            else if (dir == 4)
                                resource = R.drawable.enemy_tank_down;
                            else if (dir == 6)
                                resource = R.drawable.enemy_tank_left;
                        }
                        else {
                            if (dir == 0)
                                resource = R.drawable.user_tank_up;
                            else if (dir == 2)
                                resource = R.drawable.user_tank_right;
                            else if (dir == 4)
                                resource = R.drawable.user_tank_down;
                            else if (dir == 6)
                                resource = R.drawable.user_tank_left;
                        }
                    }
                    else if (val >= 1000000 && val < 2000000)
                    {
                        if (dir == 0)
                            resource = R.drawable.soldier_up;
                        else if (dir == 2)
                            resource = R.drawable.soldier_right;
                        else if (dir == 4)
                            resource = R.drawable.soldier_down;
                        else if (dir == 6)
                            resource = R.drawable.soldier_left;
                    }
                }
                else
                    resource = R.drawable.blank;

                ((ImageView) convertView).setImageResource(resource);

                if ((val >= 10000000 && val < 20000000) || (val >= 1000000 && val < 2000000)){


                    //need to do somethign with health
                    //busProvider.getEventBus().post(new HealthInfoEvent(TID + ":\t"+health));

                }*/
            }
        }
        return convertView;
    }
}


