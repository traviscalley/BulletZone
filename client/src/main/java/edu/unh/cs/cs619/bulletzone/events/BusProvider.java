package edu.unh.cs.cs619.bulletzone.events;

import com.squareup.otto.Bus;

import org.androidannotations.annotations.EBean;

/**
 * Singleton that holds the app-wide eventbus
 * @author Stephen Asherson.
 */
@EBean(scope = EBean.Scope.Singleton)
public class BusProvider
{
    private Bus eventBus;

    /**
     * Lazy load the event bus
     */
    public synchronized Bus getEventBus()
    {
        if (eventBus == null)
        {
            eventBus = new Bus();
        }

        return eventBus;
    }
}