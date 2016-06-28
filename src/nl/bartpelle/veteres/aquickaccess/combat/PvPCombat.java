package nl.bartpelle.veteres.aquickaccess.combat;

import nl.bartpelle.veteres.event.Event;
import nl.bartpelle.veteres.event.EventContainer;
import nl.bartpelle.veteres.model.*;
import nl.bartpelle.veteres.model.entity.PathQueue;
import nl.bartpelle.veteres.model.entity.Player;
import nl.bartpelle.veteres.model.entity.player.EquipSlot;
import nl.bartpelle.veteres.model.entity.player.WeaponType;
import nl.bartpelle.veteres.model.item.Item;
import nl.bartpelle.veteres.script.TimerKey;
import nl.bartpelle.veteres.util.*;

/**
 * Created by Sky on 25-6-2016.
 */
public class PvPCombat extends Combat {
    private Player player;
    private Player target;

    public PvPCombat(Player player, Player target) {
        super(player, target);
        this.player = player;
        this.target = target;
    }

    public void cycle() {
        // Get weapon data.
        Item weapon = ((Player) getEntity()).equipment().get(EquipSlot.WEAPON);
        int weaponId = weapon == null ? -1 : weapon.id();
        int weaponType = getEntity().world().equipmentInfo().weaponType(weaponId);
        Item ammo = ((Player) getEntity()).equipment().get(EquipSlot.AMMO);
        int ammoId = ammo == null ? -1 : ammo.id();
        String ammoName = ammo == null ? "" : ammo.definition(getEntity().world()).name;

        // Check if players are in wilderness.
        if (!((Player) getEntity()).inWilderness() || !((Player) getTarget()).inWilderness()) {
            // stop();
            return;
        }

        // Combat type?
        if (weaponType == WeaponType.BOW || weaponType == WeaponType.CROSSBOW || weaponType == WeaponType.THROWN) {
            getEntity().message("ranging...");
            handleRangeCombat(weaponId, ammoName, weaponType);
        } else {
            getEntity().message("meleeeing...");
            handleMeleeCombat(weaponId);
        }

        getTarget().putattrib(AttributeKey.LAST_DAMAGER, getEntity());
        getTarget().putattrib(AttributeKey.LAST_DAMAGE, System.currentTimeMillis());
    }

    private void handleRangeCombat(int weaponId, String ammoName, int weaponType) {
        Tile currentTile = player.tile();

        // Are we in range?
        if (currentTile.distance(target.tile()) > 7 && !player.frozen() && !player.stunned()) {
            currentTile = moveCloser();
        }

        // Can we shoot?
        if (currentTile.distance(target.tile()) <= 7 && !player.timers().has(TimerKey.COMBAT_ATTACK)) {

            // Do we have ammo?
            if (ammoName.equals("")) {
                player.message("There's no ammo left in your quiver.");
                //container.stop();
                return;
            }

            // Check if ammo is of right type
            if (weaponType == WeaponType.CROSSBOW && !ammoName.contains(" bolts")) {
                player.message("You can't use that ammo with your crossbow.");
                //container.stop();
                return;
            }
            if (weaponType == WeaponType.BOW && !ammoName.contains(" arrow")) {
                player.message("You can't use that ammo with your bow.");
                //container.stop();
                return;
            }

            // Remove the ammo
            Item ammo = player.equipment().get(EquipSlot.AMMO);
            player.equipment().set(EquipSlot.AMMO, new Item(ammo.id(), ammo.amount() - 1));

            player.animate(EquipmentInfo.attackAnimationFor(player));
            int distance = player.tile().distance(target.tile());
            int cyclesPerTile = 5;
            int baseDelay = 32;
            int startHeight = 35;
            int endHeight = 36;
            int curve = 15;
            int graphic = 228;

            if (weaponType == WeaponType.CROSSBOW) {
                cyclesPerTile = 3;
                baseDelay = 40;
                startHeight = 40;
                endHeight = 40;
                curve = 2;
                graphic = 27;
            }

            if (player.varps().varp(Varp.SPECIAL_ENABLED) == 0 || !doRangeSpecial()) {
                player.world().spawnProjectile(player.tile(), target, graphic, startHeight, endHeight, baseDelay, cyclesPerTile * distance, curve, 105);

                long delay = Math.round(Math.floor(baseDelay / 30.0) + (distance * (cyclesPerTile * 0.020) / 0.6));
                boolean success = AccuracyFormula.doesHit(player, target, CombatStyle.RANGE);

                int maxHit = CombatFormula.maximumRangedHit(player);
                int hit = player.world().random(maxHit);

                // target.hit(player, success ? hit : 0, delay).combatStyle(CombatStyle.RANGE);

                target.hit(player, success ? hit : 0, (int) delay).combatStyle(CombatStyle.RANGE);

                // Timer is downtime.
                player.timers().register(TimerKey.COMBAT_ATTACK, player.world().equipmentInfo().weaponSpeed(weaponId));

                // After every attack, reset special.
                player.varps().varp(Varp.SPECIAL_ENABLED, 0);
            }
        }
    }

    private boolean doRangeSpecial() {
        int weaponId = ((Player) getEntity()).equipment().get(EquipSlot.WEAPON).id();

        switch (weaponId) {
            case 861:

                break;
        }

        return false;//TODO
    }

    private boolean doMeleeSpecial() {
        return player.isSpecialAttackEnabled();
    }
}
