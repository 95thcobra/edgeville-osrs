package edgeville.net.message.game;

import edgeville.io.RSBuffer;
import edgeville.model.entity.Player;

/**
 * @author Simon Pelle on 8/22/2014.
 *
 * Represents a command, or simply a message from the server to the user.
 */
public interface Command {
	public RSBuffer encode(Player player);
}
