package nl.bartpelle.veteres.net.message.game.action;

import io.netty.channel.ChannelHandlerContext;
import nl.bartpelle.veteres.io.RSBuffer;
import nl.bartpelle.veteres.model.entity.Player;
import nl.bartpelle.veteres.net.message.game.Action;
import nl.bartpelle.veteres.net.message.game.PacketInfo;

/**
 * Created by Bart Pelle on 8/23/2014.
 */
@PacketInfo(size = 4)
public class IntegerInput implements Action {

    private int value;

    @Override
    public void process(Player player) {
        if (player.inputHelper().input() != null) {
            player.inputHelper().input().execute(player, value);
        }
    }

    @Override
    public void decode(RSBuffer buf, ChannelHandlerContext ctx, int opcode, int size) {
        value = buf.readInt();
    }
}
