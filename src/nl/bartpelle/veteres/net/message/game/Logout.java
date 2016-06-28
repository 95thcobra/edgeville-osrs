package nl.bartpelle.veteres.net.message.game;

import nl.bartpelle.veteres.io.RSBuffer;
import nl.bartpelle.veteres.model.entity.Player;

/**
 * Created by Bart Pelle on 8/23/2014.
 */
public class Logout implements Command {

	@Override
	public RSBuffer encode(Player player) {
		return new RSBuffer(player.channel().alloc().buffer(1)).packet(136);
	}
}
