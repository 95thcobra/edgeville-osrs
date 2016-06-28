package nl.bartpelle.veteres.net.message.game.action;

import io.netty.channel.ChannelHandlerContext;
import nl.bartpelle.veteres.io.RSBuffer;
import nl.bartpelle.veteres.model.entity.Player;
import nl.bartpelle.veteres.net.message.game.Action;
import nl.bartpelle.veteres.net.message.game.PacketInfo;

/**
 * Created by Bart Pelle on 8/23/2014.
 */
@PacketInfo(size = -2)
public class KeyHistory implements Action {

	@Override public void process(Player player) {

	}

	@Override public void decode(RSBuffer buf, ChannelHandlerContext ctx, int opcode, int size) {
		buf.skip(size);
	}
}
