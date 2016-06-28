package edgeville.aquickaccess.actions;

import edgeville.aquickaccess.dialogue.DialogueHandler;
import edgeville.model.entity.Player;
import edgeville.model.map.MapObj;

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
