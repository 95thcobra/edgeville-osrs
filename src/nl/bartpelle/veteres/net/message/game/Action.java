package nl.bartpelle.veteres.net.message.game;

import io.netty.channel.ChannelHandlerContext;
import nl.bartpelle.veteres.io.RSBuffer;
import nl.bartpelle.veteres.model.entity.Player;

/**
 * Created by Bart Pelle on 8/22/2014.
 *
 * Represents an incoming action sent by the client to the server.
 */
public interface Action {

	public void decode(RSBuffer buf, ChannelHandlerContext ctx, int opcode, int size);

	public void process(Player player);

}
