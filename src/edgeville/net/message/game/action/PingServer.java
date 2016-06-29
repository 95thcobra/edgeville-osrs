package edgeville.net.message.game.action;

import edgeville.io.RSBuffer;
import edgeville.model.entity.Player;
import edgeville.net.message.game.Action;
import edgeville.net.message.game.PacketInfo;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author Simon Pelle on 8/23/2014.
 */
@PacketInfo(size = 0)
public class PingServer implements Action {

	@Override
	public void process(Player player) {
		/* Pinged, wow. */
	}

	@Override
	public void decode(RSBuffer buf, ChannelHandlerContext ctx, int opcode, int size) {
		/* Empty packet */
	}
}
