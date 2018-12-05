package edu.unh.cs.cs619.bulletzone.ui;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

public class HealthViewAdapter {
    Context context;
    LinearLayout linearLayout;

    public HealthViewAdapter(Context c, LinearLayout ll){
        this.context = c;
        this.linearLayout = ll;
    }

    //@Subscribe
    public void addHealthInfo(HealthInfoEvent event) {
        TextView valueTV = new TextView(context);
        valueTV.setText(event.healths);
        //valueTV.setId(5);
        valueTV.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        linearLayout.addView(valueTV);
    }

}
