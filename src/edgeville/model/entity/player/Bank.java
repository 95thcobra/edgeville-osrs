package edgeville.model.entity.player;

import edgeville.model.World;
import edgeville.model.entity.Player;
import edgeville.model.item.ItemContainer;
import edgeville.net.message.game.InterfaceSettings;
import edgeville.net.message.game.InvokeScript;

public class Bank {
	private Player player;
	private ItemContainer bankItems;

	public Bank(Player player) {
		this.player = player;
		bankItems = new ItemContainer(player.world(), 800, ItemContainer.Type.FULL_STACKING);
	}

	public ItemContainer getBankItems() {
		return bankItems;
	}

	public void open() {
		if (player.inWilderness()) {
			player.message("You cannot do this while in the wilderness.");
			return;
		}
		player.write(new InvokeScript(917, -1, -2147483648));
		player.interfaces().sendMain(12, false);
		player.interfaces().send(15, player.interfaces().activeRoot(), (player.interfaces().resizable() ? 56 : 60), false);

		player.write(new InterfaceSettings(12, 12, 0, 799, 1311998));
		player.write(new InterfaceSettings(12, 12, 809, 817, 2));
		player.write(new InterfaceSettings(12, 12, 818, 827, 1048576));
		player.write(new InterfaceSettings(12, 10, 10, 10, 1048578));
		player.write(new InterfaceSettings(12, 10, 11, 19, 1179714));
		player.write(new InterfaceSettings(15, 3, 0, 27, 1181438));
		player.write(new InterfaceSettings(15, 12, 0, 27, 1054));
		player.write(new InterfaceSettings(12, 32, 0, 3, 2));
		
		player.getBank().getBankItems().makeDirty();
	}
}
