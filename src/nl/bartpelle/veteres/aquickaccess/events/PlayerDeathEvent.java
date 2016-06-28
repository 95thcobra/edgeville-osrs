package nl.bartpelle.veteres.aquickaccess.events;

import nl.bartpelle.veteres.aquickaccess.Locations;
import nl.bartpelle.veteres.event.Event;
import nl.bartpelle.veteres.event.EventContainer;
import nl.bartpelle.veteres.model.Entity;
import nl.bartpelle.veteres.model.entity.Npc;
import nl.bartpelle.veteres.model.entity.Player;
import nl.bartpelle.veteres.script.TimerKey;
import nl.bartpelle.veteres.util.ItemsOnDeath;
import nl.bartpelle.veteres.util.PkpSystem;
import nl.bartpelle.veteres.util.Varbit;
import nl.bartpelle.veteres.util.Varp;

/**
 * Created by Sky on 27-6-2016.
 */
public class PlayerDeathEvent extends Event {

    private Player player;
    private Entity killer;
    private int tick = 0;

    public PlayerDeathEvent(Player player) {
        this.player = player;
        this.killer = player.killer();
    }

    @Override
    public void execute(EventContainer container) {
        switch(tick) {
            case 0:
                player.lock();
                break;

            case 1:
                if (killer instanceof Player) {
                    PkpSystem.handleDeath((Player)killer, player);
                }
                break;

            case 2:
                player.message("Oh dear, you are dead!");
                player.animate(2304);
                break;

            case 5:
                if (killer instanceof Player) {
                    ItemsOnDeath.dropItems((Player)killer, player);
                }
                player.teleport(Locations.RESPAWN_LOCATION.getTile());
                break;

            case 6:
                player.skills().resetStats();
                player.timers().cancel(TimerKey.FROZEN);
                player.timers().cancel(TimerKey.STUNNED);
                player.varps().varp(Varp.SPECIAL_ENERGY, 1000);
                player.varps().varp(Varp.SPECIAL_ENABLED, 0);
                player.damagers().clear();
                player.face(null);

                player.varps().varbit(Varbit.PROTECT_FROM_MAGIC, 0);
                player.varps().varbit(Varbit.PROTECT_FROM_MELEE, 0);
                player.varps().varbit(Varbit.PROTECT_FROM_MISSILES, 0);

                player.graphic(-1);
                player.animate(-1);
                player.hp(100, 0);
                break;

            case 7:
                player.unlock();
                container.stop();
                break;
        }
        tick++;
    }
}
