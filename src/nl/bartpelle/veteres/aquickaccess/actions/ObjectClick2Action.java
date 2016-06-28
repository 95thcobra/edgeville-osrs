package nl.bartpelle.veteres.aquickaccess.actions;

import nl.bartpelle.veteres.aquickaccess.dialogue.DialogueHandler;
import nl.bartpelle.veteres.model.entity.Player;
import nl.bartpelle.veteres.model.map.MapObj;

/**
 * Created by Sky on 28-6-2016.
 */
public class ObjectClick2Action {
    public void handleObjectClick(Player player, MapObj mapObj) {
        switch (mapObj.id()) {
            // Unhandled objects
            default:
                player.message("Unhandled object: " + mapObj.id());
                break;
        }
    }
}
