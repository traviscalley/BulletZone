package edu.unh.cs.cs619.bulletzone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.WorkerThread;
import android.view.View;

import org.androidannotations.annotations.Background;
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
    @RestService
    BulletZoneRestClient restClient;

    private boolean isTank = true;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);
    }

    /** onButtonSelect - switches activity to ClientActivity and sends boolean to server.
     */
    @Click({R.id.tankButton, R.id.shipButton})
    protected void onButtonSelect(View v) {

        if (v.getId() == R.id.shipButton)
            isTank = false;

        Intent intent = new Intent(this, ClientActivity_.class);
        intent.putExtra("isTankOrShip", isTank);

        startActivity(intent);
    }

    @Background
    @Click(R.id.buttonReplay)
    protected void onButtonReplay(View view){
        //leave async
        //not sure if this is what to do
        //leaveAsync(tankId);
        //leaveGame();
        //try {
        //BackgroundExecutor.cancelAll("grid_poller_task", true);
        //}catch(Exception e){}
        startActivity(new Intent(this, DBActivity.class));

        /*List<GridEntity> idfk = gridRepo.getAll();//(List<GridEntity> list) -> {handlePast(list);});

        GridEntity unconverted = idfk.get(0);

        //get an entry
        //make a grid wrapper
        GridWrapper next = new GridWrapper();
        int[][] grid = new int[16][16];
        Log.d(null, unconverted.getGrid());
        String[] rows = unconverted.getGrid().split("\\],\\[");
        for(int r = 0; r < grid.length; r++)
        {
            //if(rows[r].charAt(0) == ',')
                //rows[r].replaceFirst(",","");
            String[] row = rows[r].split(",");
            //Log.d(null, rows[r]);
            for(int c = 0; c < grid.length; c++) {
                //Log.d(null, row[c]);
                //if(row[c] != "")
                grid[r][c] = Integer.valueOf(row[c].replace("[", ""));
            }

        }
        next.setGrid(grid);

        //update teh grid

        //hypothetically this should work, just for one frame though
        //eventually
        //busProvider.getEventBus().post(new GridUpdateEvent(next));
*/
    }
}
