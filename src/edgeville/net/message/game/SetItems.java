package edgeville.net.message.game;

import edgeville.io.RSBuffer;
import edgeville.model.entity.Player;
import edgeville.model.item.Item;
import edgeville.model.item.ItemContainer;

/**
 * @author Simon Pelle on 8/22/2014.
 */
public class SetItems implements Command {

	private int target;
	private int targetChild;
	private int key;
	private Item[] container;

	public SetItems(int target, int targetChild, ItemContainer container) {
		this.target = target;
		this.targetChild = targetChild;
		this.container = container.copy();
	}

	public SetItems(int key, int target, int targetChild, ItemContainer container) {
		this.key = key;
		this.target = target;
		this.targetChild = targetChild;
		this.container = container.copy();
	}

	public SetItems(int key, ItemContainer container) {
		this.key = key;
		this.container = container.copy();
	}

	@Override
	public RSBuffer encode(Player player) {
		RSBuffer buffer = new RSBuffer(player.channel().alloc().buffer(8));

		buffer.packet(235).writeSize(RSBuffer.SizeType.SHORT);

		buffer.writeInt(target == 0 ? -1 : ((target << 16) | targetChild));
		buffer.writeShort(key);
		buffer.writeShort(container.length);

		for (Item item : container) {
			if (item == null) {
				buffer.writeShort(0);
				buffer.writeByte(0);
			} else {
				buffer.writeShort(item.id() + 1);
				buffer.writeByte(Math.min(255, item.amount()));

				if (item.amount() >= 255)
					buffer.writeLEInt(item.amount());
			}
		}

		return buffer;
	}
}
