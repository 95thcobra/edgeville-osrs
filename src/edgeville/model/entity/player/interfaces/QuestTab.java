package edgeville.model.entity.player.interfaces;

import edgeville.model.entity.Player;
import edgeville.model.entity.player.Loadout;
import edgeville.net.message.game.InterfaceText;

public class QuestTab {
	private Player player;

	public QuestTab(Player player) {
		this.player = player;
	}

	public void sendQuestTabTitle() {
		player.interfaces().sendInterfaceString(274, 10, "Players online: " + player.world().getPlayersOnline());
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
		player.interfaces().sendInterfaceString(questTabInterfaceId, 18, "");
		player.interfaces().sendInterfaceString(questTabInterfaceId, 19, "Save loadout");
		player.interfaces().sendInterfaceString(questTabInterfaceId, 20, "Load loadout");
		
		for (int child = 21; child < 143; child++) {
			player.write(new InterfaceText(questTabInterfaceId, child, ""));
		}
	}

	public void clickButton(int buttonId) {
		switch (buttonId) {

		// Melee
		case 15:
			player.getInventory().add(12006);
			player.getInventory().add(12954);
			player.message("Spawned some melee gear.");
			break;

		// Range
		case 16:
			player.message("TODO.");
			break;

		// Hybrid
		case 17:
			player.message("TODO.");
			break;

		// Save loadout
		case 19:
			player.getLoadout().save(player);
			player.message("Saved loadout.");
			break;

		// Load loadout
		case 20:
			player.getLoadout().load(player);
			player.message("Loaded loadout.");
			break;
		}
	}
}
