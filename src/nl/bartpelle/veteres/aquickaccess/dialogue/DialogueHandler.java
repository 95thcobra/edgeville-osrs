package nl.bartpelle.veteres.aquickaccess.dialogue;

import nl.bartpelle.veteres.model.entity.Player;
import nl.bartpelle.veteres.net.message.game.InterfaceSettings;
import nl.bartpelle.veteres.net.message.game.InvokeScript;

/**
 * Created by Sky on 21-6-2016.
 */
public class DialogueHandler {

    /**
     * Send an option dialogue.
     * @param player
     * @param title
     * @param options
     */
    public void sendOptionDialogue(Player player, String title, String... options) {
        String optionsString = "";
        for (int i = 0; i < options.length; i++) {
            optionsString += options[i];

            // If not last string, add seperator
            if (i != options.length - 1) {
                optionsString += "|";
            }
        }

        player.interfaces().send(219, 162, 546, false); // chatbox
        player.write(new InvokeScript(58, title, optionsString));
        player.write(new InterfaceSettings(219, 0, 1, 5, 1));
    }
}
