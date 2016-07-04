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
    	((Player)entity).message("gothere sigh");
        entity.world().getEventHandler().addEvent(entity, new Event() {
            @Override
            public void execute(EventContainer container) {
                if (entity.dead() || entity.locked() || target.dead() || target.locked()) {
                    container.stop();
                    return;
                }
                cycle(container);
            }
        });
    }

    public abstract void cycle(EventContainer container);
    
    public abstract void handleMeleeCombat(int weaponId);
    
    public abstract void handleRangeCombat(int weaponId, String ammoName, int weaponType, EventContainer container);

    public Tile moveCloser() {
        entity.pathQueue().clear();

        int steps = entity.pathQueue().running() ? 2 : 1;
        int otherSteps = target.pathQueue().running() ? 2 : 1;

        Tile otherTile = target.pathQueue().peekAfter(otherSteps) == null ? target.getTile() : target.pathQueue().peekAfter(otherSteps).toTile();
        entity.stepTowards(target, otherTile, 25);
        return entity.pathQueue().peekAfter(steps - 1) == null ? entity.getTile() : entity.pathQueue().peekAfter(steps - 1).toTile();
    }
}
