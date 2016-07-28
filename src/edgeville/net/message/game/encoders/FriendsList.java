package edgeville.net.message.game.encoders;

import java.util.ArrayList;
import java.util.List;

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

	//58 to set friends list to connect

	@Override
	public RSBuffer encode(Player player) {

		String guy1 = "Guy1";
		String guy2 = "Guy2";
		String guy3 = "Guy3";

		String guy4 = "Guy4";
		String guy5 = "Guy5";
		String guy6 = "Guy6";

		int size = guy1.getBytes().length + guy2.getBytes().length + guy3.getBytes().length;
		size = size + 1 + 2 + 1 + 1;

		RSBuffer buffer = new RSBuffer(player.channel().alloc().buffer(size));
		buffer.packet(195).writeSize(RSBuffer.SizeType.SHORT);

		buffer.writeByte(1);
		buffer.writeString(guy1);
		buffer.writeString(guy2);
		buffer.writeShort(0);
		buffer.writeByte(1);
		buffer.writeByte(1);
		buffer.writeString(guy3);

		return buffer;
	}

}
