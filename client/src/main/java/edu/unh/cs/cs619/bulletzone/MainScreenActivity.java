package edu.unh.cs.cs619.bulletzone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EView;
import org.androidannotations.rest.spring.annotations.RestService;

import edu.unh.cs.cs619.bulletzone.rest.BulletZoneRestClient;

@EActivity(R.layout.main_screen)
public class MainScreenActivity extends Activity
{
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);
    }

    /** onButtonSelect - switches activity to ClientActivity and sends boolean to server.
     */
    @Click({R.id.tankButton, R.id.shipButton})
    protected void onButtonSelect(View v) {
        Boolean isTank = true;

        if (v.getId() == R.id.shipButton)
            isTank = false;

        Intent intent = new Intent(this, ClientActivity_.class);
        intent.putExtra("isTankOrShip", isTank);
        startActivity(intent);
    }
}
