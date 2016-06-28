package nl.bartpelle.veteres.net.message.game.action;

import io.netty.channel.ChannelHandlerContext;
import nl.bartpelle.veteres.io.RSBuffer;
import nl.bartpelle.veteres.model.AttributeKey;
import nl.bartpelle.veteres.model.entity.Player;
import nl.bartpelle.veteres.model.entity.player.Privilege;
import nl.bartpelle.veteres.model.item.Item;
import nl.bartpelle.veteres.net.message.game.Action;
import nl.bartpelle.veteres.net.message.game.PacketInfo;

/**
 * Created by Bart on 8/11/2015.
 */
@PacketInfo(size = 9)
public class ItemDragAction implements Action {

	private int from;
	private int to;
	private boolean insert;
	private int hash;

	@Override
	public void decode(RSBuffer buf, ChannelHandlerContext ctx, int opcode, int size) {
		from = buf.readUShort();
		insert = buf.readUByte() == 1;
		to = buf.readULEShort();
		hash = buf.readIntV2();
	}

	@Override
	public void process(Player player) {
		if (player.privilege().eligibleTo(Privilege.ADMIN) && player.<Boolean>attrib(AttributeKey.DEBUG, false))
			player.message("Drag: from=%d to=%d inter=%d child=%d insert=%b", from, to, hash>>16, hash&0xFFFF, insert);

		int inter = hash >> 16;
		if (inter == 149) {
			Item old = player.inventory().get(to);
			player.inventory().set(to, player.inventory().get(from));
			player.inventory().set(from, old);
		}
	}

}
