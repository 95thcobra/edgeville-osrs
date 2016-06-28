package nl.bartpelle.veteres.net.message.game.action;

import io.netty.channel.ChannelHandlerContext;
import nl.bartpelle.veteres.io.RSBuffer;
import nl.bartpelle.veteres.model.entity.Player;
import nl.bartpelle.veteres.net.message.game.Action;
import nl.bartpelle.veteres.net.message.game.PacketInfo;

/**
 * Created by Tom on 11/16/2015.
 */
@PacketInfo(size = -1)
public class StringInput implements Action {
    private String value;

    @Override
    public void decode(RSBuffer buf, ChannelHandlerContext ctx, int opcode, int size) {
        value = buf.readString();
    }

    @Override
    public void process(Player player) {
        if (player.inputHelper().input() != null) {
            player.inputHelper().input().execute(player, value);
        }
    }
}
