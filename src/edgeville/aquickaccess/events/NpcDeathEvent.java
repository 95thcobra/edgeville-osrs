package edgeville.aquickaccess.events;

import edgeville.event.Event;
import edgeville.event.EventContainer;
import edgeville.model.Entity;
import edgeville.model.entity.Npc;
import edgeville.model.entity.Player;

/**
 * Created by Sky on 27-6-2016.
 */
public class NpcDeathEvent extends Event {

    private Npc npc;
    private int tick = 0;

    public NpcDeathEvent(Npc npc) {
        this.npc = npc;
    }

    @Override
    public void execute(EventContainer container) {
        switch(tick) {
            case 0:
                npc.lock();
                break;

            case 1:
                Entity killer = npc.killer();
                if (killer instanceof Player) {
                    killer.message("You have killed " + npc.def().name);
                }
                break;

            case 2:
                npc.animate(2304);
                break;

            case 5:
                npc.world().unregisterNpc(npc);
                stop();
                break;
        }
        tick++;
    }
}
