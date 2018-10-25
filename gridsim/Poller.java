package tgc1002.gridsim;

// imports ======================================
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
// ==============================================

/** Poller - Responsible for sending a query to the server to GET a new grid
 *           every 500 milliseconds. The Poller class must use a ScheduledThreadPoolExecutor
 *           to cause requests to be sent to the server at a fixed rate of once every half second.
 *
 * @author Travis Calley
 */
public class Poller
{
    // class variables
    private static Poller _instance; // singleton instance of Poller
    private ScheduledThreadPoolExecutor sch = (ScheduledThreadPoolExecutor)
                                                Executors.newScheduledThreadPool(5);
    RequestQueue rQueue;
    private static Context context;
    private String url = "http://stman1.cs.unh.edu:6191/games";

    /** Poller - private constructor to initialize the Poller singleton.
     */
    private Poller()
    {
        rQueue = Volley.newRequestQueue(context);

        JsonObjectRequest post = new JsonObjectRequest(Request.Method.POST, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {}
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR", "Could not POST");
                error.printStackTrace();
            }
        });

        sch.scheduleAtFixedRate(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        rQueue.add(getJsonObjectRequest());
                                        getJsonObjectRequest();
                                    }
                                }, 0, 500, TimeUnit.MILLISECONDS);
    }

    /** parseJSON - parses JSON from url.
     *
     * @return request - JsonObjectRequest to add to RequestQueue
     */
    private JsonObjectRequest getJsonObjectRequest()
    {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                            JSONArray jsonArray = response.getJSONArray("grid");
                            Log.d("Poller", "Response Received");
                            EventBus.getDefault().post(jsonArray); // send jsonArray on EventBus
                        }
                        catch (JSONException e) {
                            Log.d("ERROR", "Could not fetch data from JSON");
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR", "Could not fetch data from JSON");
                error.printStackTrace();
            }
        });

        return request;
    }

    /** getInstance - getter to return the reference to the Singleton Poller object.
     *
     * @return Poller - singleton Poller object
     */
    public static Poller getInstance()
    {
        if (_instance == null)
            _instance = new Poller();
        return _instance;
    }

    /** setContext - sets the context of the Poller so it can create a RequestQueue.
     *
     * @param c - MainActivity Context
     */
    public static void setContext(MainActivity c)
    {
        context = c;
    }
}
