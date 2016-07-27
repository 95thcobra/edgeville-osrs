package edgeville.net.message.game.encoders;

import edgeville.io.RSBuffer;
import edgeville.model.GroundItem;
import edgeville.model.entity.Player;
import io.netty.buffer.Unpooled;

/**
 * @author Simon
 */
public class FriendsList implements Command {

	
	public FriendsList() {
	}

	@Override
	public RSBuffer encode(Player player) {
		RSBuffer buffer = new RSBuffer(Unpooled.buffer());

		buffer.packet(195);
		
		buffer.writeByte(1);
		buffer.writeString("Test1");
		buffer.writeString("Test2");
		buffer.writeShort(0);
		buffer.writeByte(1);
		buffer.writeByte(1);
		
		buffer.writeString("Test3");
		return buffer;
	}

}

