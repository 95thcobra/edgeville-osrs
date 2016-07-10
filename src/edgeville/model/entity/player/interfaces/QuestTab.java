package edgeville.model.entity.player.interfaces;

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

	public void updateMaxHit(int maxHit) {
		player.interfaces().sendInterfaceString(274, 26, "Max hit: " + maxHit);
	}

	public void updateKills() {
		player.interfaces().sendInterfaceString(274, 23, "Kills: " + player.getKills());
	}

	public void updateDeaths() {
		player.interfaces().sendInterfaceString(274, 24, "Deaths: " + player.getDeaths());
	}

	public void prepareQuestTab() {
		final int questTabInterfaceId = 274;

		sendQuestTabTitle();
		player.interfaces().sendInterfaceString(questTabInterfaceId, 14, "Quick-gear"); // Second
																						// big
																						// string

		// Small strings start. COLORS <col=00AEDB>
		player.interfaces().sendInterfaceString(questTabInterfaceId, 15, "Melee gear");
		player.interfaces().sendInterfaceString(questTabInterfaceId, 16, "Range gear");
		player.interfaces().sendInterfaceString(questTabInterfaceId, 17, "Hybrid gear");
		player.interfaces().sendInterfaceString(questTabInterfaceId, 18, "Pure gear");
		player.interfaces().sendInterfaceString(questTabInterfaceId, 19, "");
		player.interfaces().sendInterfaceString(questTabInterfaceId, 20, "Save loadout");
		player.interfaces().sendInterfaceString(questTabInterfaceId, 21, "Load loadout");
		player.interfaces().sendInterfaceString(questTabInterfaceId, 22, "");
		updateKills();// 23
		updateDeaths();// 24
		player.interfaces().sendInterfaceString(questTabInterfaceId, 25, "");
		updateMaxHit(-1);// 26

		for (int child = 27; child < 143; child++) {
			player.write(new InterfaceText(questTabInterfaceId, child, ""));
		}
	}

	public void clickButton(int buttonId) {
		switch (buttonId) {

		// Melee
		case 15:
			if (player.inCombat()) {
				player.message("You cannot do this in combat!");
				return;
			}

			player.getPrayer().deactivateAllPrayers();
			player.spawnMelee();
			player.message("You have spawned some melee gear.");
			break;

		// Range
		case 16:
			if (player.inCombat()) {
				player.message("You cannot do this in combat!");
				return;
			}

			player.getPrayer().deactivateAllPrayers();
			player.spawnRanged();
			player.message("You have spawned some ranged gear.");
			break;

		// Hybrid
		case 17:
			if (player.inCombat()) {
				player.message("You cannot do this in combat!");
				return;
			}

			player.getPrayer().deactivateAllPrayers();
			player.spawnHybrid();
			player.message("You have spawned some hybrid gear.");
			break;

		// Pure
		case 18:
			if (player.inCombat()) {
				player.message("You cannot do this in combat!");
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

		// Save loadout
		case 20:
			player.getLoadout().save(player);
			player.message("Saved loadout.");
			break;

		// Load loadout
		case 21:
			if (player.inCombat()) {
				player.message("You cannot do this in combat!");
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
		}
	}
}
