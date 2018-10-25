package tgc1002.gridsim;

import org.json.JSONArray;

/** SimGridSubscriber - interface for ConcreteSubscribers to implement. This class allows
 *                      a class to receive JSON data sent from the Poller over the EventBus.
 *
 * @author Travis Calley
 */
public interface SimGridSubscriber
{
    void onStart();
    void onStop();
    void onEvent(JSONArray array);
}
