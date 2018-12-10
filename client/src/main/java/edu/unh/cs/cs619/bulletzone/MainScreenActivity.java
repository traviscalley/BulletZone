package edu.unh.cs.cs619.bulletzone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import edu.unh.cs.cs619.bulletzone.rest.BulletZoneRestClient;

/** MainScreenActivity - the main screen when first launching the BulletZone application.
 *                       Users are greeted with a screen that allows them to select playing
 *                       as either a tank or ship. As well as replaying the last game.
 */
@EActivity(R.layout.main_screen)
public class MainScreenActivity extends Activity
{
    private boolean isTank;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);
    }

    /** onButtonSelect - switches activity to ClientActivity and sends boolean to server for tank/ship.
     *
     * @param v - either the tankButton or shipButton to be clicked
     */
    @Click({R.id.tankButton, R.id.shipButton})
    protected void onButtonSelect(View v)
    {

        if (v.getId() == R.id.shipButton)
            isTank = false;
        else if (v.getId() == R.id.tankButton)
            isTank = true;

        Intent intent = new Intent(this, ClientActivity_.class);
        intent.putExtra("tankBoolean", isTank);
        startActivity(intent);
    }

    /** onButtonReplay - starts the new activity that shows the user a replay of the
     *              previous game.
     * @param view - button to be clicked
     */
    @Click(R.id.buttonReplay)
    protected void onButtonReplay(View view){
        startActivity(new Intent(this, DBActivity_.class));
    }
}
