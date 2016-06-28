package nl.bartpelle.veteres.net.message.game;

import io.netty.buffer.Unpooled;
import nl.bartpelle.veteres.io.RSBuffer;
import nl.bartpelle.veteres.model.entity.Player;

/**
 * Created by Bart Pelle on 8/22/2014.
 */
public class ChangeMapMarker implements Command {

	private int x;
	private int z;

	public ChangeMapMarker(int x, int z) {
		this.x = x;
		this.z = z;
	}

	@Override
	public RSBuffer encode(Player player) {
		RSBuffer buffer = new RSBuffer(Unpooled.buffer());

		buffer.packet(24);

		buffer.writeByte(x);
		buffer.writeByte(z);

		return buffer;
	}
}
