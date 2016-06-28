package nl.bartpelle.veteres.net.message.game.action;

import io.netty.channel.ChannelHandlerContext;
import nl.bartpelle.veteres.io.RSBuffer;
import nl.bartpelle.veteres.model.entity.Player;
import nl.bartpelle.veteres.net.message.game.Action;
import nl.bartpelle.veteres.net.message.game.PacketInfo;

/**
 * Created by Bart Pelle on 8/23/2014.
 *
 */
@PacketInfo(size = 1)
public class WindowStateChanged implements Action {

	private boolean visible;

	@Override public void decode(RSBuffer buf, ChannelHandlerContext ctx, int opcode, int size) {
		visible = buf.readByte() == 1;
	}

	@Override public void process(Player player) {
		/* We could register this for antibotting :D */
	}
}
