package edgeville.net.message.game.action;

import edgeville.io.RSBuffer;
import edgeville.model.entity.Player;
import edgeville.model.entity.player.Privilege;
import edgeville.model.item.Item;
import edgeville.net.message.game.Action;
import edgeville.net.message.game.PacketInfo;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by Bart on 5-2-2015.
 */
@PacketInfo(size = 8)
public class ItemAction3 extends ItemAction {

	@Override
	public void decode(RSBuffer buf, ChannelHandlerContext ctx, int opcode, int size) {
		slot = buf.readUShort();
		hash = buf.readIntV1();
		item = buf.readULEShort();
	}

	@Override
	protected int option() {
		return 2;
	}

	@Override
	public void process(Player player) {
		super.process(player);
	}
}
