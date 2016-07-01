package edgeville.model.entity.player.interfaces;

import edgeville.model.entity.Player;
import edgeville.model.entity.player.EquipSlot;
import edgeville.model.entity.player.Loadout;
import edgeville.model.item.Item;
import edgeville.net.message.game.InterfaceText;
import edgeville.util.CombatFormula;

public class QuestTab {
	private Player player;

	public QuestTab(Player player) {
		this.player = player;
	}

	public void sendQuestTabTitle() {
		player.interfaces().sendInterfaceString(274, 10, "Players online: " + player.world().getPlayersOnline());
	}
	
	public void updateMaxHit(int maxHit) {
		player.interfaces().sendInterfaceString(274, 25, "Max hit: " + maxHit);
	}

	public void updateKills() {
		player.interfaces().sendInterfaceString(274, 22, "Kills: " + player.getKills());
	}
	
	public void updateDeaths() {
		player.interfaces().sendInterfaceString(274, 23, "Deaths: " + player.getDeaths());
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
		player.interfaces().sendInterfaceString(questTabInterfaceId, 21, "");
		updateKills();//22
		updateDeaths();//23
		player.interfaces().sendInterfaceString(questTabInterfaceId, 24, "");
		updateMaxHit(-1);//25
		
		for (int child = 26; child < 143; child++) {
			player.write(new InterfaceText(questTabInterfaceId, child, ""));
		}
	}

	public void clickButton(int buttonId) {
		switch (buttonId) {

		// Melee
		case 15:
			player.getInventory().empty();
			player.getInventory().add(5698);
			player.getInventory().add(145);
			player.getInventory().add(157);
			player.getInventory().add(163);
			player.getInventory().add(4153);
			player.getInventory().add(new Item(385, 23));
			
			player.getEquipment().set(EquipSlot.HEAD, new Item(10828));
			player.getEquipment().set(EquipSlot.CAPE, new Item(6570));
			player.getEquipment().set(EquipSlot.AMULET, new Item(6585));
			player.getEquipment().set(EquipSlot.WEAPON, new Item(12006));
			player.getEquipment().set(EquipSlot.BODY, new Item(10551));
			player.getEquipment().set(EquipSlot.SHIELD, new Item(12954));
			player.getEquipment().set(EquipSlot.LEGS, new Item(4722));
			player.getEquipment().set(EquipSlot.HANDS, new Item(7462));
			player.getEquipment().set(EquipSlot.FEET, new Item(11840));
			player.getEquipment().set(EquipSlot.RING, new Item(11773));
			//player.getEquipment().set(EquipSlot.AMMO, item);
			
			player.message("Spawned some melee gear.");
			break;

		// Range
		case 16:
			player.message("TODO.");
			break;

		// Hybrid
		case 17:
			player.getInventory().empty();
			   player.getInventory().add(5698);
			   player.getInventory().add(157);
			   player.getInventory().add(163);
			   player.getInventory().add(145);
			   player.getInventory().add(6570);
			   player.getInventory().add(12006);
			   player.getInventory().add(12954);
			   player.getInventory().add(10551);
			   player.getInventory().add(4722);
			   player.getInventory().add(11840);
			   player.getInventory().add(11773);
			   player.getInventory().add(new Item(560, 10000));
			   player.getInventory().add(new Item(565, 10000));
			   player.getInventory().add(new Item(555, 10000));
			   player.getInventory().add(new Item(397, 14 ));
			   
			   player.getEquipment().set(EquipSlot.HEAD, new Item(10828));
			   player.getEquipment().set(EquipSlot.CAPE, new Item(2412));
			   player.getEquipment().set(EquipSlot.AMULET, new Item(6585));
			   player.getEquipment().set(EquipSlot.WEAPON, new Item(4675));
			   player.getEquipment().set(EquipSlot.BODY, new Item(4712));
			   player.getEquipment().set(EquipSlot.SHIELD, new Item(6889));
			   player.getEquipment().set(EquipSlot.LEGS, new Item(4714));
			   player.getEquipment().set(EquipSlot.HANDS, new Item(7462));
			   player.getEquipment().set(EquipSlot.FEET, new Item(6920));
			   player.getEquipment().set(EquipSlot.RING, new Item(6731));
			   //player.getEquipment().set(EquipSlot.AMMO, item);
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
