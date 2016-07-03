package edgeville.model.entity.player;

import edgeville.bank.BankTab;
import edgeville.model.entity.Player;
import edgeville.model.entity.player.interfaces.inputdialog.NumberInputDialog;
import edgeville.model.item.Item;
import edgeville.model.item.ItemContainer;
import edgeville.model.item.ItemContainer.Type;
import edgeville.net.message.game.encoders.InterfaceSettings;
import edgeville.net.message.game.encoders.InvokeScript;
import edgeville.util.Varbit;

public class Bank {
	private Player player;
	private BankTab[] bankTabs = new BankTab[10];

	public BankTab[] getBankTabs() {
		return bankTabs;
	}

	private int currentBankTab = 0;

	public Bank(Player player) {
		this.player = player;
		for (int i = 0; i < bankTabs.length; i++) {
			bankTabs[i] = new BankTab(i, Varbit.BANK_TAB - 1 + i);
		}
	}

	private BankTab getBankTabForItem(int itemId) {
		for (int i = 0; i < bankTabs.length; i++) {
			BankTab tab = bankTabs[i];
			if (tab.contains(itemId)) {
				return tab;
			}
		}
		return null;
	}

	public boolean isInsertEnabled() {
		return player.varps().getVarbit(Varbit.BANK_INSERT) == 1;
	}

	public void moveItemOnItem(int itemId, int slot, int itemOther, int slotOther, int hashthing) {
		if (hashthing == 10/* itemId == 65535 */ && slotOther >= 10 && slotOther <= 20) {// 10
																							// is
			handleBankTabs(itemOther, slotOther);
			return;
		}

		if (isInsertEnabled()) {
			shiftItems(itemId, itemOther, slot, slotOther/* itemId, itemOther */);
			return;
		}

		swapItem(itemId, itemOther);
	}

	private void swapItem(int itemId, int itemOther) {
		BankTab myTab = this.getBankTabForItem(itemId);
		BankTab otherTab = this.getBankTabForItem(itemOther);
		int slot1 = myTab.getSlot(itemId);
		int slot2 = otherTab.getSlot(itemOther);
		player.message("Tab[%d](%d) to Tab[%d](%d)", myTab.getId(), slot1, otherTab.getId(), slot2);

		Item item1 = myTab.getItems().get(slot1);
		Item item2 = otherTab.getItems().get(slot2);

		myTab.getItems().set(slot1, item2);
		otherTab.getItems().set(slot2, item1);

		makeDirty();
	}

	private void handleBankTabs(int itemOther, int slotOther) {
		BankTab fromTab = getBankTabForItem(itemOther);
		BankTab targetBankTab = bankTabs[slotOther - 10];
		player.message("Target bank tab : %d", targetBankTab.getId());

		int amount = fromTab.remove(itemOther);
		targetBankTab.add(new Item(itemOther, amount));

		makeDirty();
	}

	public ItemContainer getAllItems() {
		ItemContainer container = new ItemContainer(player.world(), 800, Type.FULL_STACKING);
		for (int i = 1; i < bankTabs.length; i++) {
			BankTab tab = bankTabs[i];
			for (Item item : tab.getItems()) {
				container.add(item);
			}
			player.varps().setVarbit(tab.getVarbit(), tab.getItems().size());
		}

		// Add the main tab items.
		for (Item item : bankTabs[0].getItems())
			container.add(item);

		return container;
	}

	public void handleClick(int buttonId, int slot, int option) {
		switch (buttonId) {
		case 10:
			currentBankTab = slot - 9;
			player.message("currentbanktab:%d", currentBankTab);
			break;
		case 12:
			if (option == 9) {
				Item item = getAllItems().get(slot + 1);
				player.message(player.world().examineRepository().item(item.getId()));
				return;
			}
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

		// bank all inv
		case 27:
			Item[] inv = player.getInventory().getItems();
			for (int i = 0; i < inv.length; i++) {
				Item item = inv[i];
				if (item == null)
					continue;
				player.getInventory().remove(item);
				bankTabs[currentBankTab].add(item);
			}
			break;

		// bank all equip
		case 29:
			Item[] equip = player.getEquipment().getItems();
			for (int i = 0; i < equip.length; i++) {
				Item item = equip[i];
				if (item == null)
					continue;
				player.getEquipment().remove(item);
				bankTabs[currentBankTab].add(item);
			}
			break;
		}
	}

	private boolean dirty;

	public void clean() {
		dirty = false;
	}

	public void makeDirty() {
		dirty = true;
	}

	// dont touch below

	public boolean isDirty() {
		return dirty;
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

		makeDirty();
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
			amount = player.getInventory().count(id);
			break;
		}

		return amount;
	}

	public void withdraw(int buttonId, int slot, int option) {
		slot++;
		Item item = getAllItems().get(slot);
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

	public void moveItemsToInventory(int id, int amount) {
		int unnotedId = new Item(id).definition(player.world()).unnotedID;

		int idToAdd;
		if (player.varps().getVarbit(Varbit.BANK_WITHDRAW_NOTE) == 1 && unnotedId > 0 && unnotedId > id) {
			idToAdd = unnotedId;
		} else {
			idToAdd = id;
		}

		player.message("Removing %d ... %d", id, amount);
		BankTab tab = getBankTabForItem(id);
		if (tab == null) {
			return;
		}
		if (tab.contains(id, amount) && player.getInventory().add(idToAdd, amount).success()) {
			getBankTabForItem(id).remove(id, amount);
		}
		makeDirty();
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

	public void deposit(int buttonId, int slot, int option) {
		slot++;

		// The selected item.
		Item item = player.getInventory().get(slot);

		if (item == null)
			return;

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

	private void moveItemsToBank(int id, int amount) {

		player.message("amount:%d", amount);

		int unnotedId = new Item(id).definition(player.world()).unnotedID;
		if (player.getInventory().remove(id, amount).success()) {

			int idToAdd;
			if (unnotedId > 0 && unnotedId < id) {
				idToAdd = unnotedId;
			} else {
				idToAdd = id;
			}
			if (bankTabs[currentBankTab].contains(idToAdd)) {
				int slot = bankTabs[currentBankTab].getSlot(idToAdd);
				bankTabs[currentBankTab].getItems().set(slot, new Item(idToAdd, bankTabs[currentBankTab].getItems().get(slot).getAmount() + amount));
			} else {
				bankTabs[currentBankTab].add(new Item(idToAdd, amount));
			}

			makeDirty();
		}
	}

	public void shiftItems(int itemId, int itemOther, int slot, int slotOther) {
		BankTab myTab = this.getBankTabForItem(itemId);
		BankTab otherTab = this.getBankTabForItem(itemOther);

		Item itemToInsert = this.getAllItems().get(slot);
		Item itemoToRemove = this.getAllItems().get(slotOther);

		if (myTab == otherTab) {
			player.message("ItemToInsert:" + itemToInsert);
			player.message("ItemToInsertAt:" + itemoToRemove);
			int insertAtSlot = myTab.getSlot(itemoToRemove.getId());
			myTab.remove(itemToInsert);
			myTab.getItems().add(insertAtSlot, itemToInsert);
		} else {
			player.message("Tab:%d -> Tab:%d", myTab.getId(), otherTab.getId());
			player.message("ItemToInsert:" + itemToInsert);
			int insertAtSlot = myTab.getSlot(itemoToRemove.getId());

			otherTab.remove(itemToInsert);
			myTab.getItems().add(insertAtSlot, itemToInsert);
		}
		makeDirty();
	}
}
