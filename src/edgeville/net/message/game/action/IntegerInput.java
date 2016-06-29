package edgeville.net.message.game.action;

import edgeville.io.RSBuffer;
import edgeville.model.entity.Player;
import edgeville.net.message.game.Action;
import edgeville.net.message.game.PacketInfo;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author Simon Pelle on 8/23/2014.
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
