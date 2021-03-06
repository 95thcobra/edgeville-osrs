package edgeville.net.message.game.encoders;

import edgeville.io.RSBuffer;
import edgeville.model.GroundItem;
import edgeville.model.entity.Player;
import io.netty.buffer.Unpooled;

/**
 * @author Simon
 */
public class RunEnergy implements Command {

	private int value;
	
	public RunEnergy(int value) {
		this.value = value;;
	}

	@Override
	public RSBuffer encode(Player player) {
		RSBuffer buffer = new RSBuffer(Unpooled.buffer());

		buffer.packet(115);
		buffer.writeByte(value);

		return buffer;
	}

}

