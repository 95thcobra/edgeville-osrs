package nl.bartpelle.veteres.event;

import nl.bartpelle.veteres.model.Entity;
import nl.bartpelle.veteres.model.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sky on 3-3-2016.
 */
public class EventHandler {

    /*private static EventHandler instance;

    public static EventHandler getSingleton() {
        if (instance == null) {
            instance = new EventHandler();
        }
        return instance;
    }*/

    private List<EventContainer> events;

    public EventHandler() {
        this.events = new ArrayList<EventContainer>();
    }

    public void addEvent(Entity entity, Event event) {
        addEvent(entity, 0, event);
    }

    public void addEvent(Entity entity, int ticks, Event event) {
        addEvent(entity, ticks, true, event);
    }

    public void addEvent(Entity entity, int ticks, boolean cancellable, Event event) {
        addEvent(new EventContainer(entity, ticks, cancellable, event));
    }

    public void addEvent(EventContainer event) {
        events.add(event);
    }

    public void process() {
        List<EventContainer> eventsCopy = new ArrayList<EventContainer>(events);
        List<EventContainer> remove = new ArrayList<EventContainer>();
        for (EventContainer c : eventsCopy) {
            if (c != null) {
                if (c.needsExecution())

                    c.execute();
                if (!c.isRunning()) {
                    remove.add(c);
                }
            }
        }
        for (EventContainer c : remove) {
            events.remove(c);
        }
    }

    public int getEventsCount() {
        return this.events.size();
    }

    public void stopEvents(Entity entity) {
        for (EventContainer c : events) {
            if (c.getEntity() == entity) {
                c.stop();
            }
        }
    }

    public void stopCancellableEvents(Entity entity) {
        for (EventContainer c : events) {
            if (c.getEntity() == entity && c.isCancellable()) {
                c.stop();
            }
        }
    }
}
