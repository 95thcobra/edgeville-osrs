package edgeville.aquickaccess.actions;

import edgeville.combat.Combat;
import edgeville.combat.PlayerVersusAnyCombat;
import edgeville.combat.Magic.Runes;
import edgeville.model.AttributeKey;
import edgeville.model.Entity;
import edgeville.model.Locations;
import edgeville.model.entity.Player;
import edgeville.model.entity.player.EquipSlot;
import edgeville.model.entity.player.Skills;
import edgeville.model.entity.player.skills.Prayer;
import edgeville.model.item.Item;
import edgeville.net.message.game.encoders.InvokeScript;
import edgeville.script.TimerKey;
import edgeville.util.CombatFormula;
import edgeville.util.SettingsBuilder;
import edgeville.util.TextUtil;
import edgeville.util.Varbit;
import edgeville.util.Varp;

/**
 * Created by Sky on 21-6-2016.
 */
public class ButtonClickAction {
	private Player player;
	private int interfaceId;
	private int buttonId;
	private int slot;
	private int option;

	public ButtonClickAction(Player player, int interfaceId, int buttonId, int slot, int option) {
		this.player = player;
		this.interfaceId = interfaceId;
		this.buttonId = buttonId;
		this.slot = slot;
		this.option = option;
	}

	// Add buttons here
	public void handleButtonClick() {
		switch (interfaceId) {

		// Bank: used for withdrawing.
		case 12:
			player.getBank().handleClick(buttonId, slot, option);
			break;

		// Inventory: used for banking & more
		case 15:
			if (option <= 6) {
				player.getBank().deposit(buttonId, slot, option);
			}

			// Advanced settings.
		case 60:
			advancedSettings();
			break;

		// XP Drops settings
		case 137:
			XPDropToggles();
			break;

		case 160:
			// XP Drops
			if (buttonId == 1) {
				setupXPDrops();
			}

			// Toggle running
			if (buttonId == 22) {
				player.pathQueue().toggleRunning();
			}
			break;

		// Logout
		case 182:
			if (buttonId == 6) {
				player.logout();
			}
			break;

		// Spellbook
		case 218:
			handleSpellBook();
			break;

		// Run toggle, in settings
		case 261:
			if (buttonId == 66) {
				player.pathQueue().toggleRunning();
			}
			if (buttonId == 21) {
				player.interfaces().sendMain(60);
			}
			break;

		// activate prayer
		case 271:
			player.getPrayer().togglePrayer(buttonId);
			break;

		// Quest tab
		case 274:
			// handleQuestTab();
			player.getQuestTab().clickButton(buttonId);
			break;

		// Options: equipment stats, etc.
		case 387:
			handleOptionsTabs();
			break;

		// Combat style switching
		case 593:
			handleCombatStyleSwitch();
			break;
		}
	}

	////////////////

	private void advancedSettings() {
		switch (buttonId) {
		case 12:
			boolean enabled = player.varps().getVarbit(Varbit.TRANSPARENT_CHAT_BOX) == 1;
			player.varps().setVarbit(Varbit.TRANSPARENT_CHAT_BOX, enabled ? 0 : 1);
			break;
		case 14:
			boolean clickThroughEnabled = player.varps().getVarbit(Varbit.CLICKTHROUGH_CHAT_BOX) == 1;
			player.varps().setVarbit(Varbit.CLICKTHROUGH_CHAT_BOX, clickThroughEnabled ? 0 : 1);
			break;
		}
	}

	private void handleOptionsTabs() {
		switch (buttonId) {

		// Equipment stats
		case 17:
			player.interfaces().sendMain(84);
			player.getEquipment().refreshEquipmentStatsInterface(player);
			break;

		// Unequip
		case 6:
			unequip(EquipSlot.HEAD);
			break;
		case 7:
			unequip(EquipSlot.CAPE);
			break;

		case 8:
			unequip(EquipSlot.AMULET);
			break;

		case 9:
			unequip(EquipSlot.WEAPON);
			break;

		case 10:
			unequip(EquipSlot.BODY);
			break;

		case 11:
			unequip(EquipSlot.SHIELD);
			break;

		case 12:
			unequip(EquipSlot.LEGS);
			break;

		case 13:
			unequip(EquipSlot.HANDS);
			break;

		case 14:
			unequip(EquipSlot.FEET);
			break;
		case 15:
			unequip(EquipSlot.RING);
			break;
		case 16:
			unequip(EquipSlot.AMMO);
			break;

		// Items on death
		case 21:
			player.interfaces().sendMain(102);
			break;
		}
	}

	// REMOVE EQUIPMENT ITEM
	private void unequip(int slot) {
		if (option != 0) {
			return;
		}
		if (player.locked()) {
			return;
		}
		Item item = player.getEquipment().get(slot);
		if (!player.getInventory().add(item).success()) {
			player.message("You don't have enough inventory space to do that.");
			return;
		}
		player.getEquipment().set(slot, null);
		player.getEquipment().refreshEquipmentStatsInterface(player);
	}

	private void handleCombatStyleSwitch() {
		switch (buttonId) {

		// Attack styles
		case 3:
			player.varps().setVarp(Varp.ATTACK_STYLE, 0);
			break;
		case 7:
			player.varps().setVarp(Varp.ATTACK_STYLE, 1);
			break;
		case 11:
			player.varps().setVarp(Varp.ATTACK_STYLE, 2);
			break;
		case 15:
			player.varps().setVarp(Varp.ATTACK_STYLE, 3);
			break;

		// Special attack
		case 30:
			if (isGmaulAttack()) {
				return;
			}
			if (specialSpecialAttack()) {
				// varps.setVarp(Varp.SPECIAL_ENABLED, isSpecialAttackEnabled()
				// ? 0 : 1);
				return;
			}
			player.toggleSpecialAttack();
			break;
		}
	}

	private boolean specialSpecialAttack() {
		// player.varps().setVarp(Varp.SPECIAL_ENABLED, 0);
		Item weapon = player.getEquipment().get(EquipSlot.WEAPON);
		if (weapon == null) {
			return false;
		}
		switch (weapon.getId()) {
		// dragon battle axe
		case 1377:
			if (!player.drainSpecialEnergy(100)) {
				return true;
			}
			player.skills().alterSkill(Skills.ATTACK, 0.9);
			player.skills().alterSkill(Skills.DEFENCE, 0.9);
			player.skills().alterSkill(Skills.RANGED, 0.9);
			player.skills().alterSkill(Skills.MAGIC, 0.9);
			player.skills().alterSkill(Skills.STRENGTH, 1.1);
			player.graphic(246);
			break;
		// Not handled
		default:
			return false;
		}
		return true;
	}

	private boolean isGmaulAttack() {
		Item weapon = player.getEquipment().get(EquipSlot.WEAPON);
		int weaponId = weapon == null ? -1 : weapon.getId();

		if (weaponId != 4153) {
			return false;
		}

		Entity target = player.getAttribute(AttributeKey.TARGET);
		if (target != null) {
			PlayerVersusAnyCombat.handleGraniteMaul(player, target);
		}
		return true;
	}

	private void XPDropToggles() {
		switch (buttonId) {

		// Position
		case 50:
			player.varps().setVarbit(Varbit.XP_DROPS_POSITION, slot);
			break;

		// Size
		case 51:
			player.varps().setVarbit(Varbit.XP_DROPS_SIZE, slot);
			break;

		// Duration
		case 52:
			player.varps().setVarbit(Varbit.XP_DROPS_DURATION, slot);
			break;

		// Counter
		case 53:
			player.varps().setVarbit(Varbit.XP_DROPS_COUNTER, slot);
			break;

		// Progressbar
		case 54:
			player.varps().setVarbit(Varbit.XP_DROPS_PROGRESSBAR, slot);
			break;

		// Color
		case 55:
			player.varps().setVarbit(Varbit.XP_DROPS_COLOR, slot);
			break;

		// Group
		case 56:
			player.varps().setVarbit(Varbit.XP_DROPS_GROUP, slot);
			break;

		// Speed
		case 57:
			player.varps().setVarbit(Varbit.XP_DROPS_SPEED, slot);
			break;
		}
	}

	private void setupXPDrops() {
		// Toggle XP drops
		if (option == 0) {
			player.skills().toggleXPCounter();
			return;
		}

		// Setup XP drops
		if (option == 1) {
			player.write(new InvokeScript(917, -1, -1));
			player.interfaces().sendMain(137);

			SettingsBuilder settingsBuilder = new SettingsBuilder();
			player.interfaces().setting(137, 50, 1, 3, settingsBuilder.option(0));
			player.interfaces().setting(137, 51, 1, 3, settingsBuilder.option(0));
			player.interfaces().setting(137, 52, 1, 4, settingsBuilder.option(0));
			player.interfaces().setting(137, 53, 1, 32, settingsBuilder.option(0));
			player.interfaces().setting(137, 54, 1, 32, settingsBuilder.option(0));
			player.interfaces().setting(137, 55, 1, 8, settingsBuilder.option(0));
			player.interfaces().setting(137, 56, 1, 2, settingsBuilder.option(0));
			player.interfaces().setting(137, 57, 1, 3, settingsBuilder.option(0));
			player.interfaces().setting(137, 16, 0, 24, settingsBuilder.option(0));
			return;
		}
	}

	private void handleSpellBook() {
		if (option != 0) {
			return;
		}
		switch (buttonId) {

		// Edge teleport
		case 1:
		case 91:// ancient
		case 93:// lunar
			player.message("Teleporting to edgeville...");
			player.teleport(Locations.EDGEVILLE.getTile());
			break;

		// Varrock teleport
		case 16:
			player.message("Teleporting to varrock...");
			player.teleport(Locations.VARROCK.getTile());
			break;

		// veng
		case 132:
			castVeng();
			break;
		}
	}

	private void castVeng() {
		int levelReq = 94;
		if (levelReq > player.skills().level(Skills.MAGIC)) {
			player.message("You need a magic level of %d to cast %s.", levelReq, "Vengeance");
			return;
		}
		Item[] requiredRunes = new Item[] { new Item(Runes.ASTRAL_RUNE, 4), new Item(Runes.DEATH_RUNE, 2), new Item(Runes.EARTH_RUNE, 10) };
		for (Item item : requiredRunes) {
			if (player.getInventory().contains(item.getId(), item.getAmount())) {
				player.message("You do not have the required runes to cast Vengeance!");
				return;
			}
		}
		if (player.isVengOn()) {
			player.message("Vengeance is already enabled!");
			return;
		}
		if (player.timers().has(TimerKey.VENGEANCE_COOLDOWN)) {
			player.message("Vengeance is on cooldown, wait %i seconds.", (int) (player.timers().timers().get(TimerKey.VENGEANCE_COOLDOWN).ticks()) / 0.6);
			return;
		}
		player.graphic(726, 92, 0);
		player.animate(4410);
		player.timers().register(TimerKey.VENGEANCE_COOLDOWN, 50);
		player.setVengOn(true);
	}
}
