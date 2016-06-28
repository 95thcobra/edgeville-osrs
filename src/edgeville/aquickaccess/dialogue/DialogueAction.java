package edgeville.aquickaccess.dialogue;

import edgeville.aquickaccess.Locations;
import edgeville.model.entity.Player;
import edgeville.model.entity.player.Privilege;

/**
 * Created by Sky on 21-6-2016.
 */
public class DialogueAction {

    private Player player;
    private int option;

    public DialogueAction(Player player, int option) {
        this.player = player;
        this.option = option;
    }

    public void handleDialog() {
        switch (player.getDialogueAction()) {
            // Teleport wizard
            case 1:
                handleTeleportWizard();
                break;

            // Spellbook altar
            case 2:
                handleSwitchSpellbook();
                break;
        }
        player.interfaces().closeChatDialogue();
    }

    ///////////// Add new dialogues below ///////////////////

    private void handleTeleportWizard() {
        switch (option) {
            case 1:
                player.teleportWithAnimation(Locations.EDGEVILLE.getTile());
                player.message("You have teleported to Edgeville.");
                break;
            case 2:
                player.teleportWithAnimation(Locations.VARROCK.getTile());
                player.message("You have teleported to Varrock.");
                break;
            case 3:
                player.teleportWithAnimation(Locations.FALADOR.getTile());
                player.message("You have teleported to Falador.");
                break;
        }
    }

    private void handleSwitchSpellbook() {
        switch (option) {
            case 1:
                player.message("You have switched to modern magic.");
                player.varps().varbit(4070, 0); // Modern
                break;
            case 2:
                player.message("You have switched to ancient magic.");
                player.varps().varbit(4070, 1); // Modern
                break;
            case 3:
                player.message("You have switched your to lunar magic.");
                player.varps().varbit(4070, 2); // Modern
                break;
        }
    }
}
