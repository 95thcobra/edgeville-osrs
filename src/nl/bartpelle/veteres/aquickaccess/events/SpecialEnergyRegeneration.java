package nl.bartpelle.veteres.aquickaccess.events;

import nl.bartpelle.veteres.event.Event;
import nl.bartpelle.veteres.event.EventContainer;
import nl.bartpelle.veteres.model.entity.Player;
import nl.bartpelle.veteres.util.Varp;

/**
 * Created by Sky on 27-6-2016.
 */
public class SpecialEnergyRegeneration extends Event {

    private Player player;
    private int tick = 0;

    public SpecialEnergyRegeneration(Player player) {
        this.player = player;
    }

    @Override
    public void execute(EventContainer container) {

        player.message("TICK:"+tick);

        // On tick 50 replenish energy.
        if (tick == 50) {
            int currentEnergy = player.varps().varp(Varp.SPECIAL_ENERGY);
            player.varps().varp(Varp.SPECIAL_ENERGY, Math.min(1000, currentEnergy + 100));
            tick = 0;
        }
        tick++;
    }
}
