package nl.bartpelle.veteres.net.message.game.action;

import io.netty.channel.ChannelHandlerContext;
import nl.bartpelle.veteres.io.RSBuffer;
import nl.bartpelle.veteres.model.entity.Player;
import nl.bartpelle.veteres.net.message.game.Action;
import nl.bartpelle.veteres.net.message.game.PacketInfo;
import nl.bartpelle.veteres.util.GameCommands;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Bart Pelle on 8/23/2014.
 */
@PacketInfo(size = -1)
public class ConsoleAction implements Action {

	private static final Logger logger = LogManager.getLogger(ConsoleAction.class);

	private String command;

	@Override public void decode(RSBuffer buf, ChannelHandlerContext ctx, int opcode, int size) {
		command = buf.readString();
	}

	@Override public void process(Player player) {
		try {
			if (!player.dead())
				GameCommands.process(player, command);
		} catch (Exception e) {
			player.message("Error processing command %s: %s (%s).", command, e.getClass(), e.getMessage());
		}
	}
}
