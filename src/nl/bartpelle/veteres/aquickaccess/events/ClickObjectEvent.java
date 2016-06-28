package nl.bartpelle.veteres.aquickaccess.events;

import nl.bartpelle.veteres.aquickaccess.actions.ObjectClick1Action;
import nl.bartpelle.veteres.event.Event;
import nl.bartpelle.veteres.event.EventContainer;
import nl.bartpelle.veteres.model.Tile;
import nl.bartpelle.veteres.model.entity.Player;
import nl.bartpelle.veteres.model.map.MapObj;

/**
 * Created by Sky on 21-6-2016.
 */
public class ClickObjectEvent extends Event {
    private Player player;
    private MapObj mapObject;
    private Tile targetTile;

    public ClickObjectEvent(Player player, MapObj mapObject, Tile targetTile) {
        this.player = player;
        this.targetTile = targetTile;
        this.mapObject = mapObject;
    }

    @Override
    public void execute(EventContainer container) {
        if (player.tile().distance(targetTile) <= 3) {
            container.stop();
        }
    }

    @Override
    public void stop() {
        new ObjectClick1Action().handleObjectClick(player, mapObject);
    }
}
