package edu.unh.cs.cs619.bulletzone.model;

import org.json.JSONArray;

/** SimGridSubscriber - interface for ConcreteSubscribers to implement. This class allows
 *                      a class to receive JSON data sent from the Poller over the EventBus.
 *
 * @author Travis Calley
 */
public interface GridSubscriber
{
    void onStart();
    void onStop();
    void onEvent(JSONArray array);
}
