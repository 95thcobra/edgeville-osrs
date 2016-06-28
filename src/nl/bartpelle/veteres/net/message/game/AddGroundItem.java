package nl.bartpelle.veteres.net.message.game;

import io.netty.channel.ChannelHandlerContext;
import nl.bartpelle.veteres.io.RSBuffer;
import nl.bartpelle.veteres.model.GroundItem;
import nl.bartpelle.veteres.model.entity.Player;

/**
 * Created by Bart on 8/22/2015.
 */
public class AddGroundItem implements Command {

	private GroundItem item;

	public AddGroundItem(GroundItem item) {
		this.item = item;
	}

	@Override
	public RSBuffer encode(Player player) {
		RSBuffer packet = new RSBuffer(player.channel().alloc().buffer(6)).packet(145);

		packet.writeLEShortA(item.item().id());
		int x = item.tile().x % 8;
		int z = item.tile().z % 8;
		packet.writeByteA((x << 4) | z);
		packet.writeLEShort(item.item().id());

		return packet;
	}

}

