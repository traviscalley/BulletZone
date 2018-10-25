package tgc1002.gridsim;

// imports from standard android library
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

// imports for Volley library
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

// imports for JSON parsing
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/** MainActivity - The MainActivity of the program responsible for
 *                  initializing the creation of the facade and the UI elements.
 *
 * @author Travis Calley
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    // global variables
    private Button historyButton;
    private Button pauseButton;
    private SimGridFacade gridFacade;
    private Poller poller;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // on creation initialize the button elements
        historyButton = findViewById(R.id.button1);
        pauseButton = findViewById(R.id.button2);
        historyButton.setOnClickListener(this);
        pauseButton.setOnClickListener(this);

        // initialize the Facade
        gridFacade = new SimGridFacade(MainActivity.this, this);

        // get a reference to the Poller
        Poller.setContext(MainActivity.this);
        poller = Poller.getInstance();
    }

    /** onClick - handles click event for when a button is pressed.
     *
     * @param v - View, the current view being interacted with
     */
    public void onClick(View v)
    {
        // if history button was clicked open the HistoryActivity
        if (v == historyButton)
        {
            String historyData = gridFacade.showDetailsForSelected();

            Intent intent = new Intent(this, HistoryActivity.class);
            intent.putExtra("HistoryData", historyData);

            // Check if the grid is paused before starting a new activity
            if (gridFacade.togglePause() == true)
            {
                gridFacade.togglePause();
                pauseButton.setText("Pause");
            }

            startActivity(intent);
        }
        // Otherwise if pause button was clicked pause the updating of GridCell elements
        else if (v == pauseButton)
        {
            if (gridFacade.togglePause() == true)
                pauseButton.setText("Resume");
            else
                pauseButton.setText("Pause");
        }
    }
}
