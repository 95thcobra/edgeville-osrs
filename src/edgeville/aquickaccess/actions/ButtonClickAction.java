package edgeville.aquickaccess.actions;

import edgeville.combat.Combat;
import edgeville.model.AttributeKey;
import edgeville.model.Entity;
import edgeville.model.Locations;
import edgeville.model.entity.Player;
import edgeville.model.entity.player.EquipSlot;
import edgeville.model.item.Item;
import edgeville.net.message.game.InvokeScript;
import edgeville.script.TimerKey;
import edgeville.util.CombatFormula;
import edgeville.util.SettingsBuilder;
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
                break;

            // Quest tab
            case 274:
                handleQuestTab();
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

    private void handleQuestTab() {
        switch (buttonId) {

            // Melee gear
            case 15:
                player.message("Spawned melee gear.");
                break;

            // Range gear
            case 16:
                player.message("Spawned range gear.");
                break;

            // Hybrid gear
            case 17:
                player.message("Spawned hybrid gear.");
                break;
        }
    }

    private void handleOptionsTabs() {
        switch (buttonId) {

            // Equipment stats
            case 17:
                player.interfaces().sendMain(84);
                player.equipment().refreshEquipmentStatsInterface(player);
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
        Item item = player.equipment().get(slot);
        if (!player.inventory().add(item).success()) {
            player.message("You don't have enough inventory space to do that.");
            return;
        }
        player.equipment().set(slot, null);
        player.equipment().refreshEquipmentStatsInterface(player);
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
                player.toggleSpecialAttack();
                break;
        }
    }

    private boolean isGmaulAttack() {
        Item weapon = player.equipment().get(EquipSlot.WEAPON);
        int weaponId = weapon == null ? -1 : weapon.id();

        if (weaponId != 4153) {
            return false;
        }

        Entity target = player.attrib(AttributeKey.TARGET);
        if (target != null) {
            Combat.handleGraniteMaul(player, target);
        }
        return true;
    }

    private void XPDropToggles() {
        switch (buttonId) {

            // Position
            case 50:
                player.varps().varbit(Varbit.XP_DROPS_POSITION, slot);
                break;

            // Size
            case 51:
                player.varps().varbit(Varbit.XP_DROPS_SIZE, slot);
                break;

            // Duration
            case 52:
                player.varps().varbit(Varbit.XP_DROPS_DURATION, slot);
                break;

            // Counter
            case 53:
                player.varps().varbit(Varbit.XP_DROPS_COUNTER, slot);
                break;

            // Progressbar
            case 54:
                player.varps().varbit(Varbit.XP_DROPS_PROGRESSBAR, slot);
                break;

            // Color
            case 55:
                player.varps().varbit(Varbit.XP_DROPS_COLOR, slot);
                break;

            // Group
            case 56:
                player.varps().varbit(Varbit.XP_DROPS_GROUP, slot);
                break;

            // Speed
            case 57:
                player.varps().varbit(Varbit.XP_DROPS_SPEED, slot);
                break;
        }
    }

    private void setupXPDrops() {
        // Toggle XP drops
        if (option == 0) {
            player.skills().toggleXPCounter();
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
        }
    }

    private void handleSpellBook() {
        if (option != 0) {
            return;
        }
        switch (buttonId) {

            // Edge teleport
            case 1:
                player.message("Teleporting to edgeville...");
                player.teleportWithAnimation(Locations.EDGEVILLE.getTile());
                break;

            // Varrock teleport
            case 16:
                player.message("Teleporting to varrock...");
                player.teleportWithAnimation(Locations.VARROCK.getTile());
                break;
        }
    }
}
