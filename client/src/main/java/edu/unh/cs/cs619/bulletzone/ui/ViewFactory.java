package edu.unh.cs.cs619.bulletzone.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.View;
import android.widget.ImageView;

import edu.unh.cs.cs619.bulletzone.R;

public class ViewFactory {
    private int myID = -1;
    private Context context;

    private final int TANK = 1;
    private final int SOLDIER = 2;
    private final int SHIP = 3;
    private final int BULLET = 4;
    private final int WALL = 5;
    private final int ANTIGRAV = 6;
    private final int FUSION = 7;
    private final int RACK = 8;

    private final int HILL = 1;
    private final int DEBRIS = 2;
    private final int WATER = 3;
    private final int COAST = 4;

    private static ViewFactory instance;
    public static ViewFactory getInstance()
    {
        if(instance == null)
            instance = new ViewFactory();
        return instance;
    }

    public void setContext(Context c){
        this.context = c;
    }

    public void setMyID(int id){
        myID = id;
    }

    public View makeCellView(ImageView iv, int value){

        //int layers[] = new int[]{-1, -1, -1};
        Drawable[] layers = new Drawable[4];
        layers[0] = context.getDrawable(R.drawable.empty);
        layers[1] = context.getDrawable(R.drawable.empty);
        layers[2] = context.getDrawable(R.drawable.empty);
        layers[3] = context.getDrawable(R.drawable.empty);

        int dir = value % 10;
        double health = (value/10) % 1000;
        int tankID = (value/10000) % 1000;
        int objectType = (value/ 10000000) % 10;
        int terrainType = value / 100000000;

        //set terrain
        if(terrainType != 0) {
            int ter = R.drawable.empty;
            switch (terrainType){
                case HILL:
                    ter = R.drawable.hill;
                    break;
                case DEBRIS:
                    ter = R.drawable.debris_field;
                    break;
                case WATER:
                    ter = R.drawable.water;
                    break;
                case COAST:
                    ter = R.drawable.coast;
                    break;
            }
            layers[0] = context.getDrawable(ter);
        }

        //set object
        if(objectType != 0){
            int obj = R.drawable.empty;
            switch (objectType){
                case TANK:
                    //if(tankID == myID) {
                        if (dir == 0)
                            obj = R.drawable.user_tank_up;
                        else if (dir == 2)
                            obj = R.drawable.user_tank_right;
                        else if (dir == 4)
                            obj = R.drawable.user_tank_down;
                        else if (dir == 6)
                            obj = R.drawable.user_tank_left;
                    /*}
                    else {
                        if (dir == 0)
                            obj = R.drawable.enemy_tank_up;
                        else if (dir == 2)
                            obj = R.drawable.enemy_tank_right;
                        else if (dir == 4)
                            obj = R.drawable.enemy_tank_down;
                        else if (dir == 6)
                            obj = R.drawable.enemy_tank_left;
                    }*/
                    break;
                case SOLDIER:
                    if (dir == 0)
                        obj = R.drawable.soldier_up;
                    else if (dir == 2)
                        obj = R.drawable.soldier_right;
                    else if (dir == 4)
                        obj = R.drawable.soldier_down;
                    else if (dir == 6)
                        obj = R.drawable.soldier_left;
                case SHIP:
                    ///obj = R.drawable.ship_up;
                    //add more later
                    break;
                case BULLET:
                    obj = R.drawable.bullet;
                    break;
                case WALL:
                    if(health == 0)
                        obj = R.drawable.wall;
                    else
                        obj = R.drawable.wall_breakable;
                    break;
                case ANTIGRAV:
                    obj = R.drawable.antigrav;
                    break;
                case FUSION:
                    obj = R.drawable.fusion;
                    break;
                case RACK:
                    obj = R.drawable.powerrack;
                    break;
            }

            layers[1] = context.getDrawable(obj);
        }

        //set health
        if(health != 0) {
            int hea = R.drawable.empty;
            double total = -1;

            switch (objectType){
                case TANK:
                    total = 100;
                    break;
                case SOLDIER:
                    total = 25;
                    break;
                case SHIP:
                    total = 200;// TODO figure this out???
                    break;
                case WALL:
                    total = 999;//TODO figure this out
                    break;
                default:

            }

            double percentage = health/total;
            if(percentage == 0.0)
                hea = R.drawable.health_0;
            else if(percentage > 0.0 && percentage <= 0.3)
                hea = R.drawable.health_20;
            else if(percentage > 0.3 && percentage <= 0.5)
                hea = R.drawable.health_40;
            else if(percentage > 0.5 && percentage <= 0.7)
                hea = R.drawable.health_60;
            else if(percentage > 0.7 && percentage < 1.0)
                hea = R.drawable.health_80;
            else if(percentage == 1.0)
                hea = R.drawable.health_100;

            layers[2] = context.getDrawable(hea);
        }

        if(tankID == myID)
            layers[3] = context.getDrawable(R.drawable.mine);

        iv.setImageDrawable(new LayerDrawable(layers));
        return iv;
    }
}
