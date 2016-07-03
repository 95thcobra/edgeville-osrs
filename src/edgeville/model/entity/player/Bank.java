package edgeville.model.entity.player;

import edgeville.model.World;
import edgeville.model.entity.Player;
import edgeville.model.entity.player.interfaces.inputdialog.NumberInputDialog;
import edgeville.model.item.Item;
import edgeville.model.item.ItemContainer;
import edgeville.net.message.game.encoders.InterfaceSettings;
import edgeville.net.message.game.encoders.InvokeScript;
import edgeville.util.Varbit;

public class Bank {
	private Player player;
	private ItemContainer bankItems;

	private int currentBankTab;
	public ItemContainer[] bankTabItems;
	private int[] bankTabCounts = new int[9];

	public BankNew bankNewwww;

	public Bank(Player player) {
		this.player = player;
		bankItems = new ItemContainer(player.world(), 800, ItemContainer.Type.FULL_STACKING);

		bankTabItems = new ItemContainer[9];
		// (player.world(), 200, ItemContainer.Type.FULL_STACKING)//kanker

		currentBankTab = 0;
		/*
		 * for(int i = 0 ; i < bankItems.size(); i++) { bankTabItems[0][i] =
		 * bankItems.get(i).getId(); }
		 */
		
		bankNewwww = new BankNew(player);
	}

	public void handleClick(int buttonId, int slot, int option) {
		if (!player.dead()) {
			bankNewwww.handleClick(buttonId, slot, option);
			return;
		}
		switch (buttonId) {
		case 10:
			currentBankTab = slot - 9;
			player.message("Currentbanktab: %d", currentBankTab);
			break;
		case 12:
			withdraw(buttonId, slot, option);
			break;
		case 16:
			player.varps().setVarbit(Varbit.BANK_INSERT, 0);
			break;
		case 18:
			player.varps().setVarbit(Varbit.BANK_INSERT, 1);
			break;
		case 21:
			player.varps().setVarbit(Varbit.BANK_WITHDRAW_NOTE, 0);
			break;
		case 23:
			player.varps().setVarbit(Varbit.BANK_WITHDRAW_NOTE, 1);
			break;
		}
	}

	public ItemContainer getBankItems() {
/*if (!player.dead()) {
	return bankNewwww.getBankItems();
}*/
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

	public int determineAmountToDeposit(int option, int totalAmount, int id) {
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
		case 6:
			amount = totalAmount;
			break;
		}

		return amount;
	}

	public void moveItemsToBank(int id, int amount) {
		int unnotedId = new Item(id).definition(player.world()).unnotedID;

		if (bankItems.occupiedSlots() >= bankItems.size() && !bankItems.has(id)) {
			player.message("Bank is full!");
			return;
		}
		if (player.getInventory().remove(id, amount).success()) {
			if (unnotedId > 0 && unnotedId < id) {
				bankItems.add(unnotedId, amount);
				return;
			}

			bankItems.add(id, amount);
			if (currentBankTab > 0) {
				int slot = getSlot(id);
				shiftItems(slot, 0);
				moveFirstItemOfBankToBankTab();
			}
		}
	}

	public int getSlot(int itemId) {
		for (int i = 0; i < bankItems.size(); i++) {
			if (bankItems.get(i).getId() == itemId) {
				return i;
			}
		}
		return -1;
	}

	public void moveFirstItemOfBankToBankTab() {
		// if currentbank tab ==0, TODO rest
		bankTabCounts[currentBankTab]++;
		player.varps().setVarbit(Varbit.BANK_TAB + currentBankTab + 1, bankTabCounts[currentBankTab]);
	}

	public void moveItemsToInventory(int id, int amount) {
		int unnotedId = new Item(id).definition(player.world()).unnotedID;

		int idToAdd;
		if (player.varps().getVarbit(Varbit.BANK_WITHDRAW_NOTE) == 1 && unnotedId > 0 && unnotedId > id) {
			idToAdd = unnotedId;
		} else {
			idToAdd = id;
		}

		if (player.getInventory().add(idToAdd, amount).success()) {
			bankItems.remove(id, amount);
		}
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
		if (!player.dead()) {
			bankNewwww.withdraw(buttonId, slot, option);
			return;
		}
		slot++;
		Item item = bankItems.get(slot);
		int id = item.getId();

		// X
		if (option == 4) {
			NumberInputDialog var = new NumberInputDialog(player) {
				@Override
				public void doAction(int value) {
					moveItemsToInventory(id, value);
				}
			};
			var.send();
			return;
		}

		int amount = determineAmountToWithdraw(option, item.getAmount());
		moveItemsToInventory(id, amount);
	}

	public void deposit(int buttonId, int slot, int option) {
		if (!player.dead()) {
			bankNewwww.deposit(buttonId, slot, option);
			return;
		}
		slot++;

		// The selected item.
		Item item = player.getInventory().get(slot);
		int id = item.getId();

		// X
		if (option == 5) {
			NumberInputDialog var = new NumberInputDialog(player) {
				@Override
				public void doAction(int value) {
					moveItemsToBank(id, value);
				}
			};
			var.send();
			return;
		}
		int amount = determineAmountToDeposit(option, item.getAmount(), id);
		moveItemsToBank(id, amount);
	}

	public boolean isInsertEnabled() {
		return player.varps().getVarbit(Varbit.BANK_INSERT) == 1;
	}

	public void shiftItems(int slotFrom, int slotTo) {
		int to = slotTo;
		int tempFrom = slotFrom;

		for (int tempTo = to; tempFrom != tempTo;) {
			if (tempFrom > tempTo) {
				switchItem(tempFrom, tempFrom - 1);
				tempFrom--;
			} else if (tempFrom < tempTo) {
				switchItem(tempFrom, tempFrom + 1);
				tempFrom++;
			}
		}
	}

	public void switchItem(int slotFrom, int slotTo) {
		Item from = bankItems.get(slotFrom);
		Item to = bankItems.get(slotTo);

		bankItems.set(slotFrom, to);
		bankItems.set(slotTo, from);
	}

	public void moveItemOnItem(int itemId, int slot, int itemOther, int slotOther, int hasthing) {
		if (!player.dead()) {
			bankNewwww.moveItemOnItem(itemId, slot, itemOther, slotOther, hasthing);
			return;
		}
		if (!player.dead() || (itemId == 65535 && slotOther == 11)) {// 10 is
			// TODO REMOVE THIS // first
			handleDraggingToBankTabs(slot, slotOther, itemId, itemOther);
			return;
		}

		if (isInsertEnabled()) {
			shiftItems(slot, slotOther);
			return;
		}
		Item original = bankItems.get(slot);
		Item other = bankItems.get(slotOther);

		bankItems.set(slotOther, original);
		bankItems.set(slot, other);
	}

	private void handleDraggingToBankTabs(int slotFrom, int slotOther, int itemId, int itemOther) {
		if (slotOther > 10) {
			int bankTabTo = slotOther - 11;
			player.message("Dragged into bank tab " + currentBankTab);
			bankTabCounts[bankTabTo]++;
			player.varps().setVarbit(Varbit.BANK_TAB + bankTabTo, bankTabCounts[bankTabTo]);
			player.message("Current bank tab count: %d", bankTabCounts[bankTabTo]);

			bankTabItems[bankTabTo].add(itemId);
		}
	}
}
