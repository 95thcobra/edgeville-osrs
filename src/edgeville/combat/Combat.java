package edgeville.combat;

import edgeville.event.Event;
import edgeville.event.EventContainer;
import edgeville.model.Entity;
import edgeville.model.Tile;
import edgeville.model.entity.Player;
import edgeville.script.TimerKey;
import edgeville.util.AccuracyFormula;
import edgeville.util.CombatFormula;
import edgeville.util.CombatStyle;
import edgeville.util.EquipmentInfo;

/**
 * Created by Sky on 27-6-2016.
 */
public abstract class Combat {

    private Entity entity;
    private Entity target;

    public Combat(Entity entity, Entity target) {
        this.entity = entity;
        this.target = target;
    }

    public Entity getEntity() {
        return entity;
    }

    public Entity getTarget() {
        return target;
    }

    public void start() {
        entity.world().getEventHandler().addEvent(entity, new Event() {
            @Override
            public void execute(EventContainer container) {
                if (entity.dead() || entity.locked() || target.dead() || target.locked()) {
                    container.stop();
                    return;
                }
                cycle();
            }
        });
    }

    public abstract void cycle();


    public Tile moveCloser() {
        entity.pathQueue().clear();

        int steps = entity.pathQueue().running() ? 2 : 1;
        int otherSteps = target.pathQueue().running() ? 2 : 1;

        Tile otherTile = target.pathQueue().peekAfter(otherSteps) == null ? target.getTile() : target.pathQueue().peekAfter(otherSteps).toTile();
        entity.stepTowards(target, otherTile, 25);
        return entity.pathQueue().peekAfter(steps - 1) == null ? entity.getTile() : entity.pathQueue().peekAfter(steps - 1).toTile();
    }

    public void handleMeleeCombat(int weaponId) {
        Tile currentTile = getEntity().getTile();

        // Move closer if out of range.
        if (!entity.touches(getTarget(), currentTile) && !getEntity().frozen() && !getEntity().stunned()) {
            currentTile = moveCloser();
            return;
        }

        // Gmaul is handled in buttonclick now
      /*  if (entity instanceof Player && ((Player) entity).isSpecialAttackEnabled() && handleGraniteMaul((Player) entity, weaponId)) {
            return;
        }*/

        // Check timer.
        if (!getEntity().timers().has(TimerKey.COMBAT_ATTACK)) {

            if (entity instanceof Player && ((Player) entity).isSpecialAttackEnabled()) {
                doMeleeSpecial((Player) entity, weaponId);
                return;
            }

            boolean success = AccuracyFormula.doesHit(((Player) getEntity()), getTarget(), CombatStyle.MELEE);

            int max = CombatFormula.maximumMeleeHit(((Player) getEntity()));
            int hit = getEntity().world().random(max);

            getTarget().hit(getEntity(), success ? hit : 0);

            getEntity().animate(EquipmentInfo.attackAnimationFor(((Player) getEntity())));
            getEntity().timers().register(TimerKey.COMBAT_ATTACK, getEntity().world().equipmentInfo().weaponSpeed(weaponId));
        }
    }

    public static void handleGraniteMaul(Player player, Entity target) {
        if (player.getSpecialEnergyAmount() < 50 * 10) {
            player.message("You do not have enough special energy.");
            return;
        }

        if (!player.touches(target, player.getTile())) {
            return;
        }

        player.setSpecialEnergyAmount(player.getSpecialEnergyAmount() - (50 * 10));

        player.animate(1667);
        player.graphic(340, 92, 0);

        double max = CombatFormula.maximumMeleeHit(player);
        int hit = player.world().random().nextInt((int) Math.round(max));
        target.hit(player, hit);
        player.timers().register(TimerKey.COMBAT_ATTACK, target.world().equipmentInfo().weaponSpeed(4153));
    }

    /*public static boolean handleGraniteMaul(Player player, int weaponId) {
        if (weaponId != 4153) {
            return false;
        }

        if (!drainSpecialEnergy(player, 50)) {
            return false;
        }

        player.animate(1667);
        player.graphic(340, 92, 0);

        double max = CombatFormula.maximumMeleeHit(player);
        int hit = player.world().random().nextInt((int) Math.round(max));
        target.hit(player, hit);
        getEntity().timers().register(TimerKey.COMBAT_ATTACK, getEntity().world().equipmentInfo().weaponSpeed(weaponId));
        return true;
    }*/

    private void doMeleeSpecial(Player player, int weaponId) {
        switch (weaponId) {

            // dds
            case 5698:
                player.timers().register(TimerKey.COMBAT_ATTACK, 5);

                if (!drainSpecialEnergy(player, 25)) {
                    return;
                }

                player.animate(1062);
                player.graphic(252, 92, 0);

                double max = CombatFormula.maximumMeleeHit(player) * 1.15;
                int hit = player.world().random().nextInt((int) Math.round(max));
                int hit2 = player.world().random().nextInt((int) Math.round(max));

                target.hit(player, hit);
                target.hit(player, hit2);
                break;
        }


    }

    private boolean drainSpecialEnergy(Player player, int amount) {
        player.toggleSpecialAttack();
        if (player.getSpecialEnergyAmount() < amount * 10) {
            player.message("You do not have enough special energy.");
            return false;
        }
        player.setSpecialEnergyAmount(player.getSpecialEnergyAmount() - (amount * 10));
        return true;
    }
}
