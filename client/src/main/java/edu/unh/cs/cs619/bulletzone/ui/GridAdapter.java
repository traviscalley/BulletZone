package edu.unh.cs.cs619.bulletzone.ui;

import android.media.Image;
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
        int TID = (val / 10000) % 1000;
        int dir = val % 10;

        if (convertView instanceof ImageView) {
            synchronized (monitor) {
                int resource = 0;
                if (val > 0) {
                    if (val == 1000)
                        resource = R.drawable.wall;
                    else if (val>1000&&val<=2000)
                        resource = R.drawable.wall_breakable;
                    else if (val >= 2000000 && val <= 3000000)
                        resource = R.drawable.bullet;
                    else if (val >= 10000000 && val <= 20000000)
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
                } else {
                    resource = R.drawable.blank;
                }
                ((ImageView) convertView).setImageResource(resource);
            }
        }
    }

        return convertView;
}
}

