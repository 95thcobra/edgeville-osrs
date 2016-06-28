package nl.bartpelle.veteres.net.message.game.action;

import io.netty.channel.ChannelHandlerContext;
import nl.bartpelle.veteres.event.Event;
import nl.bartpelle.veteres.event.EventContainer;
import nl.bartpelle.veteres.io.RSBuffer;
import nl.bartpelle.veteres.model.AttributeKey;
import nl.bartpelle.veteres.model.Tile;
import nl.bartpelle.veteres.model.entity.PathQueue;
import nl.bartpelle.veteres.model.entity.Player;
import nl.bartpelle.veteres.net.message.game.Action;
import nl.bartpelle.veteres.net.message.game.PacketInfo;
import nl.bartpelle.veteres.script.TimerKey;

/**
 * Created by Bart on 8/12/2015.
 */
@PacketInfo(size = 3)
public class PlayerAction2 implements Action {

    private boolean run;
    private int index;

    @Override
    public void decode(RSBuffer buf, ChannelHandlerContext ctx, int opcode, int size) {
        index = buf.readULEShort();
        run = buf.readByteS() == 1;
    }

    @Override
    public void process(Player player) {
        player.stopActions(true);

        Player other = player.world().players().get(index);
        if (other == null) {
            player.message("Unable to find player.");
        } else {
            if (!player.locked() && !player.dead() && !other.dead()) {
                player.face(other);

                //player.putattrib(AttributeKey.TARGET, index);
                //player.world().server().scriptExecutor().executeScript(player, PlayerFollowing.script);
                playerFollowing(player, other);
            }
        }
    }

    private void playerFollowing(Player player, Player other) {
        player.world().getEventHandler().addEvent(player, new Event() {
            @Override
            public void execute(EventContainer container) {
                if (player == null) {
                    container.stop();
                    return;
                }

                if (player.frozen() || player.stunned()) {
                    return;
                }

                Tile lastTile = other.pathQueue().lastStep();
                if (lastTile == null) {
                    //container.stop();
                    return;
                }

                player.walkTo(lastTile.x, lastTile.z, PathQueue.StepType.REGULAR);


                /*if (!player.touches(other)) {
                    player.moveCloser(other);
                }*/
            }
        });
    }

}
