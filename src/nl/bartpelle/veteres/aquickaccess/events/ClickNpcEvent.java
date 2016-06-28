package nl.bartpelle.veteres.aquickaccess.events;

import nl.bartpelle.veteres.aquickaccess.actions.NpcClick1Action;
import nl.bartpelle.veteres.event.Event;
import nl.bartpelle.veteres.event.EventContainer;
import nl.bartpelle.veteres.model.entity.Npc;
import nl.bartpelle.veteres.model.entity.Player;

/**
 * Created by Sky on 21-6-2016.
 */
public class ClickNpcEvent extends Event {
    private Player player;
    private Npc npc;

    public ClickNpcEvent(Player player, Npc npc) {
        this.player = player;
        this.npc = npc;
    }

    @Override
    public void execute(EventContainer container) {
        if (player.tile().distance(npc.tile()) <= 3) {
            container.stop();
        }
    }

    @Override
    public void stop() {
        new NpcClick1Action().handleNpcClick(player, npc.id());
    }
}
