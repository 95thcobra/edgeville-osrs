package nl.bartpelle.veteres.net.message.game;

import nl.bartpelle.veteres.io.RSBuffer;
import nl.bartpelle.veteres.model.entity.Player;

/**
 * Created by Bart Pelle on 8/23/2014.
 */
public class UpdatePlayers implements Command {

	private RSBuffer buffer;

	public UpdatePlayers(RSBuffer payload) {
		buffer = payload;
	}

	@Override
	public RSBuffer encode(Player player) {
		return buffer;
	}
}
