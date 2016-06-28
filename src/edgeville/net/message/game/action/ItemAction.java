package edgeville.net.message.game.action;

import edgeville.io.RSBuffer;
import edgeville.model.AttributeKey;
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
