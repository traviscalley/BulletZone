package edu.unh.cs.cs619.bulletzone.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import edu.unh.cs.cs619.bulletzone.R;
import edu.unh.cs.cs619.bulletzone.database.DBGetEvent;
import edu.unh.cs.cs619.bulletzone.events.BusProvider;
import edu.unh.cs.cs619.bulletzone.rest.GridUpdateEvent;

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
    private long playerID;
    private Context context;
    private ViewFactory viewFactory = ViewFactory.getInstance();

    @Bean
    BusProvider busProvider;

    public GridAdapter(Context c)
    {
        context = c;
        inflater = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        viewFactory.setContext(context);
    }


    private Object gridEventHandler = new Object() {
        @Subscribe
        public void idk(GridUpdateEvent event) {
            updateList(event.gw.getGrid());
        }

        @Subscribe
        public void help(DBGetEvent event) {
            updateList(event.gw.getGrid());
        }
    };

    @AfterInject
    void afterInject() {
        busProvider.getEventBus().register(gridEventHandler);
    }

    public void updateList(int[][] entities) {
        synchronized (monitor) {
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

        if (convertView instanceof ImageView) {
            return viewFactory.makeCellView((ImageView) convertView, val);
        }
        return convertView;
    }
}


