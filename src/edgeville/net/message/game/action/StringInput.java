package edgeville.net.message.game.action;

import edgeville.io.RSBuffer;
import edgeville.model.entity.Player;
import edgeville.net.message.game.Action;
import edgeville.net.message.game.PacketInfo;
import io.netty.channel.ChannelHandlerContext;

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
