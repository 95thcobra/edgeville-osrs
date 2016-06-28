package nl.bartpelle.veteres.net.message.game.action;

import io.netty.channel.ChannelHandlerContext;
import nl.bartpelle.veteres.aquickaccess.combat.Combat;
import nl.bartpelle.veteres.aquickaccess.combat.PvMCombat;
import nl.bartpelle.veteres.io.RSBuffer;
import nl.bartpelle.veteres.model.AttributeKey;
import nl.bartpelle.veteres.model.World;
import nl.bartpelle.veteres.model.entity.Npc;
import nl.bartpelle.veteres.model.entity.PathQueue;
import nl.bartpelle.veteres.model.entity.Player;
import nl.bartpelle.veteres.net.message.game.Action;
import nl.bartpelle.veteres.net.message.game.PacketInfo;

/**
 * Created by Tom on 9/26/2015.
 * Modified by Sky on 1/3/2016.
 */
@PacketInfo(size = 3)
public class NpcAttack implements Action {

    private int size = -1;
    private int opcode = -1;
    private boolean run;
    private int index;

    @Override
    public void decode(RSBuffer buf, ChannelHandlerContext ctx, int opcode, int size) {
        run = buf.readByteA() == 1;
        index = buf.readUShortA();
    }

    @Override
    public void process(Player player) {
        player.stopActions(true);

        player.message("npc attack --- npcindex:" + index + " run:" + run);
        Npc other = player.world().npcs().get(index);
        player.message("npcid:" + other.id() + " run:" + run);

        if (other == null) {
            player.message("Unable to find npc.");
        } else {
            if (!player.locked() && !player.dead() && !other.dead()) {
                player.stepTowards(other, 20);
                player.face(other);

                player.putattrib(AttributeKey.TARGET_TYPE, 1);
                player.putattrib(AttributeKey.TARGET, /*index*/ other);

                //player.world().server().scriptExecutor().executeScript(player, PlayerCombat.script);
                new PvMCombat(player, other).start();

            }
        }
    }
}
