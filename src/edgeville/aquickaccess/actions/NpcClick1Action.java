package edgeville.aquickaccess.actions;

import edgeville.aquickaccess.dialogue.DialogueHandler;
import edgeville.model.entity.Player;

/**
 * Created by Sky on 21-6-2016.
 */
public class NpcClick1Action {
    public void handleNpcClick(Player player, int npcId) {
        switch (npcId) {
        
        // make over mage
        case 1306:
        	player.interfaces().sendMain(269);
        	break;
        
        // teleport wizard edgeville
            case 4400:
                new DialogueHandler().sendOptionDialogue(
                        player,
                        "Where would you like to teleport to?",
                        "Edgeville",
                        "Varrock",
                        "Falador",
                        "Nowhere");
                player.setDialogueAction(1);
                break;
            
                // Healer nurse
            case 3343:
            	if (player.inCombat()) {
            		player.message("You cannot do this in combat!");
            		return;
            	}
            	player.resetSpecialEnergy();
            	player.skills().resetStats();
            	player.message("Your stats have been reset and special energy has been restored!");
                break;
               
        }
    }
}
