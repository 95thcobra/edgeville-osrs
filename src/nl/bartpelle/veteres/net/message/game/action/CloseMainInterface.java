package nl.bartpelle.veteres.net.message.game.action;

import io.netty.channel.ChannelHandlerContext;
import nl.bartpelle.veteres.io.RSBuffer;
import nl.bartpelle.veteres.model.entity.Player;
import nl.bartpelle.veteres.net.message.game.Action;
import nl.bartpelle.veteres.net.message.game.PacketInfo;

/**
 * Created by Bart Pelle on sept 18 2015
 */
@PacketInfo(size = 0)
public class CloseMainInterface implements Action {

	@Override public void process(Player player) {
		player.interfaces().closeMain();
	}

	@Override public void decode(RSBuffer buf, ChannelHandlerContext ctx, int opcode, int size) { }

}
