package nl.bartpelle.veteres.aquickaccess.events;

import nl.bartpelle.veteres.event.Event;
import nl.bartpelle.veteres.event.EventContainer;
import nl.bartpelle.veteres.model.Tile;
import nl.bartpelle.veteres.model.entity.Player;

/**
 * Created by Sky on 21-6-2016.
 */
public class TeleportEvent extends Event {

    private Player player;
    private Tile targetTile;
    private int tick = 0;

    public TeleportEvent(Player player, Tile targetTile) {
        this.player = player;
        this.targetTile = targetTile;
        player.lock();
    }

    @Override
    public void execute(EventContainer container) {
        switch (tick) {
            case 0:
                player.graphic(111, 92, 0);
                player.animate(714);
                break;
            case 3:
                player.teleport(targetTile);
                break;
            case 4:
                player.animate(715);
                break;
            case 5:
                stop();
                container.stop();
                break;
        }
        tick++;
    }

    @Override
    public void stop() {
        player.unlock();
    }
}
