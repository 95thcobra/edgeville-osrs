package edgeville.net.message.game.action;

import io.netty.channel.ChannelHandlerContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edgeville.io.RSBuffer;
import edgeville.model.entity.PathQueue;
import edgeville.model.entity.Player;
import edgeville.model.entity.player.Privilege;
import edgeville.net.message.game.Action;
import edgeville.net.message.game.PacketInfo;
import edgeville.script.TimerKey;

/**
 * @author Simon Pelle on 8/23/2014.
 */
@PacketInfo(size = -1)
public class WalkMap implements Action {

	private static final Logger logger = LogManager.getLogger();

	private int x;
	private int z;
	private int mode;

	@Override public void decode(RSBuffer buf, ChannelHandlerContext ctx, int opcode, int size) {
		mode = buf.readByteN();
		z = buf.readUShortA();
		x = buf.readULEShortA();
	}

	@Override public void process(Player player) {
		// Mode 2 is ctrl-shift clicking, teleporting to the tile.
		if (mode == 2 && player.getPrivilege().eligibleTo(Privilege.ADMIN)) {
			player.move(x, z, player.getTile().level);
			return;
		}

		logger.info("Walking to [{}, {}], running: {}.", x, z, mode);
		if (!player.locked() && !player.dead()) {
			player.stopActions(true);
			player.walkTo(x, z, mode == 1 ? PathQueue.StepType.FORCED_RUN : PathQueue.StepType.REGULAR);
		}
	}
}
