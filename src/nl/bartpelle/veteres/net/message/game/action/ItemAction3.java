package nl.bartpelle.veteres.net.message.game.action;

import io.netty.channel.ChannelHandlerContext;
import nl.bartpelle.veteres.io.RSBuffer;
import nl.bartpelle.veteres.model.entity.Player;
import nl.bartpelle.veteres.model.entity.player.Privilege;
import nl.bartpelle.veteres.model.item.Item;
import nl.bartpelle.veteres.net.message.game.Action;
import nl.bartpelle.veteres.net.message.game.PacketInfo;

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
