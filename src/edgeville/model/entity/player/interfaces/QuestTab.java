package edgeville.model.entity.player.interfaces;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import edgeville.database.ForumIntegration;
import edgeville.model.entity.Player;
import edgeville.model.entity.player.EquipSlot;
import edgeville.model.entity.player.Loadout;
import edgeville.model.item.Item;
import edgeville.net.message.game.encoders.InterfaceText;
import edgeville.util.CombatFormula;
import edgeville.util.Varbit;

public class QuestTab {
	private Player player;

	public QuestTab(Player player) {
		this.player = player;
	}

	public void sendQuestTabTitle() {
		player.interfaces().sendInterfaceString(274, 10, "Players online: " + player.world().getPlayersOnline());
	}

	public void updateKills() {
		player.interfaces().sendInterfaceString(274, 19, "Kills: " + player.getKills());
	}

	public void updateDeaths() {
		player.interfaces().sendInterfaceString(274, 20, "Deaths: " + player.getDeaths());
	}

	public void updateMaxHit(int maxHit) {
		player.interfaces().sendInterfaceString(274, 21, "Max hit: " + maxHit);
	}

	public void prepareQuestTab() {
		final int questTabInterfaceId = 274;

		sendQuestTabTitle();
		player.interfaces().sendInterfaceString(questTabInterfaceId, 14, "Information & Spawn"); // Second
		// big
		// string

		player.interfaces().sendInterfaceString(questTabInterfaceId, 15, "Save loadout");
		player.interfaces().sendInterfaceString(questTabInterfaceId, 16, "Load loadout");
		player.interfaces().sendInterfaceString(questTabInterfaceId, 17, "View hiscores");
		player.interfaces().sendInterfaceString(questTabInterfaceId, 18, "Update hiscores");
		updateKills();// 19
		updateDeaths();// 20
		updateMaxHit(-1);// 21

		// Small strings start. COLORS <col=00AEDB>
		player.interfaces().sendInterfaceString(questTabInterfaceId, 22, "");
		player.interfaces().sendInterfaceString(questTabInterfaceId, 23, "Melee gear");
		player.interfaces().sendInterfaceString(questTabInterfaceId, 24, "Range gear");
		player.interfaces().sendInterfaceString(questTabInterfaceId, 25, "Hybrid gear");
		player.interfaces().sendInterfaceString(questTabInterfaceId, 26, "Pure gear");
		player.interfaces().sendInterfaceString(questTabInterfaceId, 27, "Dharok's gear");

		for (int child = 28; child < 143; child++) {
			player.write(new InterfaceText(questTabInterfaceId, child, ""));
		}
		
		makeLinesYellow();
	}
	
	private void makeLinesYellow() {
		int[] varps = {29,31,62,71,107,122,130,144,222,273,176,32};
		for(int varp : varps) {
			player.getVarps().setVarp(varp, 1);
		}
	}

	public void clickButton(int buttonId) {
		switch (buttonId) {

		// Save loadout
		case 15:
			player.getLoadout().save(player);
			player.message("Saved loadout.");
			break;

		// Load loadout
		case 16:
			if (player.inCombat()) {
				player.message("You cannot do this in combat!");
				return;
			}

			if (!player.inSafeArea()) {
				player.message("You cannot do this in a PVP area!");
				return;
			}
			
			if (!player.getEquipment().isEmpty()) {
				player.message("Unequip your equipment!");
				return;
			}

			player.getPrayer().deactivateAllPrayers();
			player.getLoadout().load(player);
			player.message("Loaded loadout.");
			break;
			
			// view hiscores
		case 17:
			try {
				Desktop.getDesktop().browse(new URI("http://edgeville.org/hiscores"));
				//player.write(new InterfaceText());
			} catch (Exception e) {
				player.message("Something went wrong, is website down?");
			}
			break;
			
			// update hiscores
		case 18:
			//if (!ForumIntegration.insertHiscore(player)) {
			if (!ForumIntegration.updateHiscores(player)) {
				//player.message("1."+System.currentTimeMillis());
				//player.message("2."+ player.getLastHiscoresUpdate());
				int minutesLeft = (int)(10-((System.currentTimeMillis() - player.getLastHiscoresUpdate()) / 60000));
				player.message("You can update the hiscores in %d minutes!", minutesLeft);
				return;
			}
			player.message("You have updated the hiscores!");
			break;

		// Melee
		case 23:
			if (player.inCombat()) {
				player.message("You cannot do this in combat!");
				return;
			}
			if (!player.inSafeArea()) {
				player.message("You cannot do this in a PVP area!");
				return;
			}

			player.getPrayer().deactivateAllPrayers();
			player.spawnMelee();
			player.message("You have spawned some melee gear.");
			break;

		// Range
		case 24:
			if (player.inCombat()) {
				player.message("You cannot do this in combat!");
				return;
			}
			if (!player.inSafeArea()) {
				player.message("You cannot do this in a PVP area!");
				return;
			}

			player.getPrayer().deactivateAllPrayers();
			player.spawnRanged();
			player.message("You have spawned some ranged gear.");
			break;

		// Hybrid
		case 25:
			if (player.inCombat()) {
				player.message("You cannot do this in combat!");
				return;
			}
			if (!player.inSafeArea()) {
				player.message("You cannot do this in a PVP area!");
				return;
			}

			player.getPrayer().deactivateAllPrayers();
			player.spawnHybrid();
			player.message("You have spawned some hybrid gear.");
			break;

		// Pure
		case 26:
			if (player.inCombat()) {
				player.message("You cannot do this in combat!");
				return;
			}
			if (!player.inSafeArea()) {
				player.message("You cannot do this in a PVP area!");
				return;
			}

			if (!player.getEquipment().isEmpty()) {
				player.message("Unequip your equipment before spawning!");
				return;
			}

			player.getPrayer().deactivateAllPrayers();
			player.spawnPure();
			player.message("You have spawned some pure gear.");
			break;
		

		// Food
		/*case 27:
			if (player.inCombat()) {
				player.message("You cannot do this in combat!");
				return;
			}
			if (!player.inSafeArea()) {
				player.message("You cannot do this in a PVP area!");
				return;
			}

			player.getInventory().add(391, 28);
			player.message("You have spawned some food.");
			break;*/
			
			// dharoks
		case 27:
			if (player.inCombat()) {
				player.message("You cannot do this in combat!");
				return;
			}
			if (!player.inSafeArea()) {
				player.message("You cannot do this in a PVP area!");
				return;
			}

			player.getPrayer().deactivateAllPrayers();
			player.spawnDharoks();
			player.message("You have spawned dharok's gear.");
			break;
		}
	}
}
