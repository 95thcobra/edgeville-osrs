package edgeville.net.message.game;

import edgeville.io.RSBuffer;
import edgeville.model.entity.Player;

/**
 * @author Simon Pelle on 8/23/2014.
 */
public class Logout implements Command {

	@Override
	public RSBuffer encode(Player player) {
		return new RSBuffer(player.channel().alloc().buffer(1)).packet(136);
	}
}
