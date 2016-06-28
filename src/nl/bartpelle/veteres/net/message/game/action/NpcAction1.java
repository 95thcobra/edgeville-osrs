package nl.bartpelle.veteres.net.message.game.action;

import io.netty.channel.ChannelHandlerContext;
import nl.bartpelle.veteres.aquickaccess.events.ClickNpcEvent;
import nl.bartpelle.veteres.io.RSBuffer;
import nl.bartpelle.veteres.model.entity.Npc;
import nl.bartpelle.veteres.model.entity.Player;
import nl.bartpelle.veteres.net.message.game.Action;
import nl.bartpelle.veteres.net.message.game.PacketInfo;

/**
 * Created by Tom on 9/26/2015.
 * Modified by Sky on 1/3/2016.
 */
@PacketInfo(size = 3)
public class NpcAction1 implements Action {

    private int size = -1;
    private int opcode = -1;
    private boolean run;
    private int index;

    @Override
    public void decode(RSBuffer buf, ChannelHandlerContext ctx, int opcode, int size) {
        //run = buf.readByteA() == 1;
        //index = buf.readUShortA();

        index = buf.readULEShort();
        run = buf.readByteA() == 1;
    }

    @Override
    public void process(Player player) {
        player.stopActions(true);

        player.message("npcaction1 --- npcindex:" + index + " run:" + run);
        Npc other = player.world().npcs().get(index);
        int npcId = other.id();
        player.message("npcid:" + npcId + " run:" + run);

        if (other == null) {
            player.message("Unable to find npc.");
        } else {
            if (!player.locked() && !player.dead() && !other.dead()) {
                player.stepTowards(other, 20);
                player.face(other);

               // player.tile().distance(other.tile()) <= 2
                 player.world().getEventHandler().addEvent(player, new ClickNpcEvent(player, other));

                //handleNpcFirstClick(player, npcId);


                //player.putattrib(AttributeKey.TARGET_TYPE, 1);
                //player.putattrib(AttributeKey.TARGET, index);
                //player.world().server().scriptExecutor().executeScript(player, PvPCombat.script);
            }
        }
    }

    private void handleNpcFirstClick(Player player, int npcId) {
       // new NpcClick1Action().handleNpcClick(player, npcId);
       /* switch (npcId) {

            case 4400:
                // Wizard edgeville TODO: find options interface
                final int interfaceId = 140;
                final int parentInterfaceId = 162;
                final int positionId = 546;

                //player.se
                player.interfaces().send(interfaceId, parentInterfaceId, positionId, false);
                break;

        }*/
    }
}
