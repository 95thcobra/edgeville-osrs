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
 * Created by Bart on 5-2-2015.
 */
@PacketInfo(size = 8)
public abstract class ItemAction implements Action {

	protected int hash;
	protected int item;
	protected int slot;

	protected abstract int option();

	@Override
	public void process(Player player) {
		if (item == 0xFFFF)
			item = -1;
		if (slot == 0xFFFF)
			item = -1;

		if (player.privilege().eligibleTo(Privilege.ADMIN) && player.<Boolean>attrib(AttributeKey.DEBUG, false))
			player.message("Item option %d on [%d:%d], item: %d, slot: %d", option() + 1, hash>>16, hash&0xFFFF, item, slot);
	}

}
