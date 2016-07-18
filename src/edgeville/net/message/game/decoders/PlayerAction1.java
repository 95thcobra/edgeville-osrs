package edgeville.net.message.game.decoders;

import edgeville.combat.PvPCombat;
import edgeville.io.RSBuffer;
import edgeville.model.AttributeKey;
import edgeville.model.entity.Player;
import edgeville.net.message.game.encoders.Action;
import edgeville.net.message.game.encoders.PacketInfo;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author Simon on 8/12/2015.
 */
@PacketInfo(size = 3)
public class PlayerAction1 implements Action {

	private boolean run;
	private int index;

	@Override
	public void decode(RSBuffer buf, ChannelHandlerContext ctx, int opcode, int size) {
		run = buf.readByteN() == 1;
		index = buf.readULEShort();
	}

	@Override
	public void process(Player player) {
		player.stopActions(true);

		Player other = player.world().players().get(index);
		if (other == null) {
			player.message("Unable to find player.");
			return;
		}

		if (!player.locked() && !player.dead() && !other.dead()) {
			player.face(other);

			if (player.inWilderness() && !other.inWilderness()) {
				player.message("Your target is not in the wilderness.");
				return;
			} else if (other.inWilderness() && !player.inWilderness()) {
				player.message("You are not in the wilderness.");
				return;
			}
			
			if (Math.abs(player.skills().combatLevel() - other.skills().combatLevel()) > 5) {
				player.message("The difference in combat level should be 5 or lower.");
				return;
			}

			new PvPCombat(player, other).start();
		}
	}
}
