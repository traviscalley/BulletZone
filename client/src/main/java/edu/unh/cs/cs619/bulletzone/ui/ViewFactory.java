package edu.unh.cs.cs619.bulletzone.ui;

import android.content.Context;
import android.content.res.Resources;
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
        Drawable[] layers = new Drawable[3];
        layers[0] = context.getDrawable(R.drawable.empty);
        layers[1] = context.getDrawable(R.drawable.empty);
        layers[2] = context.getDrawable(R.drawable.empty);

        int dir = value % 10;
        int health = (value/10) % 1000;
        int tankID = (value/10000) % 1000;
        int objectType = (value/ 10000000) % 10;
        int terrainType = value / 100000000;

        //set terrain
        //set object
        //set health



        iv.setImageDrawable(new LayerDrawable(layers));
        return iv;
    }
}
