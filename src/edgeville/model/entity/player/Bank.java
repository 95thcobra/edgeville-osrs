package edgeville.model.entity.player;

import edgeville.model.World;
import edgeville.model.entity.Player;
import edgeville.model.item.Item;
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

	public int determineAmountToDeposit(int option, int totalAmount) {
		int amount = 1;

		switch (option) {
		case 1:
			amount = 1;
			break;
		case 2:
			amount = 5;
			break;
		case 3:
			amount = 10;
			break;
		case 5:
			// x TODO
			break;
		case 6:
			amount = totalAmount;
			break;
		}

		return amount;
	}

	public int determineAmountToWithdraw(int option, int totalAmount) {
		int amount = 1;

		switch (option) {
		case 0:
			amount = 1;
			break;
		case 1:
			amount = 5;
			break;
		case 2:
			amount = 10;
			break;
		case 4:
			// TODO:x
			break;
		case 5:
			amount = totalAmount;
			break;
		case 6:
			amount = totalAmount - 1;
			break;
		}

		return amount;
	}

	public void withdraw(int buttonId, int slot, int option) {
		slot++;
		Item item = bankItems.get(slot);
		int id = item.getId();
		int amount = determineAmountToWithdraw(option, item.getAmount());

		if (!player.getInventory().add(id, amount).success()) {
			return;
		}

		bankItems.remove(id, amount);
	}

	public boolean hasSpace(int itemId) {
		// If bank contains item, it can always be added.
		if (bankItems.has(itemId)) {
			return true;
		}

		// If item count equals total possible items, return false.
		if (bankItems.occupiedSlots() == bankItems.size()) {
			return false;
		}

		// Bank has space.
		return true;
	}

	public void deposit(int buttonId, int slot, int option) {
		slot++;
		player.message("Button %d, Slot %d, option %d", buttonId, slot, option);

		// The selected item.
		Item item = player.getInventory().get(slot);
		int id = item.getId();
		int amount;

		// Determine amount for option. 1,5,10,x,all. Option 6 is all.
		amount = determineAmountToDeposit(option, item.getAmount());

		if (!hasSpace(id)) {
			return;
		}

		// Remove the item from inventory
		if (player.getInventory().remove(id, amount).failed()) {
			return;
		}

		player.message("item " + item);
		bankItems.add(id, amount);
	}

	public void handleClick(int buttonId, int slot, int option) {
		switch (buttonId) {
		case 12:
			withdraw(buttonId, slot, option);
			break;
		}
	}
}
