package edu.unh.cs.cs619.bulletzone.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.SystemService;

import edu.unh.cs.cs619.bulletzone.R;

@EBean
public class GridAdapter extends BaseAdapter {

    private final Object monitor = new Object();
    @SystemService
    protected LayoutInflater inflater;
    private int[][] mEntities = new int[16][16];
    private long playerID;

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
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.field_item, null);
        }

        int row = position / 16;
        int col = position % 16;

        int val = mEntities[row][col];

        if (convertView instanceof ImageView) {
            synchronized (monitor) {
                if (val > 0) {
                    if (val == 1000 || (val>1000&&val<=2000)) {
                        ((ImageView) convertView).setImageResource(R.drawable.wall);
                    } else if (val >= 2000000 && val <= 3000000) {
                        ((ImageView) convertView).setImageResource(R.drawable.bullet);
                    } else if (val >= 10000000 && val <= 20000000) {
                        if (val != playerID)
                            ((ImageView) convertView).setImageResource(R.drawable.enemy_tank_up);
                        else
                            ((ImageView) convertView).setImageResource(R.drawable.user_tank_up);
                    }
                } else {
                    ((ImageView) convertView).setImageResource(R.drawable.blank);
                }
            }
        }

        return convertView;
    }
}


