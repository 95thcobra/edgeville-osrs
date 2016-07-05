package edgeville.model.entity.player;

import java.util.ArrayList;
import java.util.List;

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

	// public ItemContainer completeBank;
	public List<Item> bankItems;

	public BankTab[] getBankTabs() {
		return bankTabs;
	}

	private int currentBankTab = 0;

	public Bank(Player player) {

		this.player = player;

		// This is good
		/*
		 * completeBank = new ItemContainer(player.world(), 800,
		 * Type.FULL_STACKING); for (int i = 0; i < 50; i++) {
		 * completeBank.add(new Item(100 + i, i + 1000)); }
		 */
		bankItems = new ArrayList<Item>();
		for (int i = 0; i < 50; i++) {
			bankItems.add(new Item(100 + i, i + 1000));
		}
		makeDirty();

		// this is old
		for (int i = 0; i < bankTabs.length; i++) {
			bankTabs[i] = new BankTab(i, Varbit.BANK_TAB - 1 + i);
		}
	}

	/**
	 * Inserts an item into slot 0.
	 * 
	 * @param item
	 */
	public void insertItem(Item item) {
		/*
		 * for (int i = completeBank.occupiedSlots() - 1; i >= 0; i--) { Item
		 * itemToShift = completeBank.getItems()[i]; completeBank.set(i + 1,
		 * itemToShift);
		 * 
		 * player.message("Setting slot %d to item %s", i + 1,
		 * itemToShift.definition(player.world()).name); }
		 * 
		 * completeBank.set(0, item);
		 */
		bankItems.add(0, item);
		makeDirty();
	}

	public int firstSlotOfMain() {
		int slot = 0;
		for (int i = 0; i < 9; i++) {
			int size = player.varps().getVarbit(Varbit.BANK_TAB + i);
			slot += size;
		}
		player.message("First slot of main: %d", slot);
		return slot;
	}

	/**
	 * Idk yet
	 */
	public void orderBankTabs() {
		int[] tabItemCounts = new int[9];
		for (int i = 0; i < tabItemCounts.length - 1; i++) {
			tabItemCounts[i] = player.varps().getVarbit(Varbit.BANK_TAB + i);
		}
	}

	/**
	 * When you move an item over an other item.
	 * 
	 * @param slotOther
	 * @param itemOther
	 * @param slot
	 * @param itemId
	 */
	private void moveItemOnItemNew(int itemId, int slot, int itemOther, int slotOther, int hashthing) {
		if (hashthing == 10 && slotOther >= 10 && slotOther <= 20) {
			draggingToTabs(slot, slotOther);
			return;
		}

		if (isInsertEnabled()) {
			shiftItemsNew(slot, slotOther/* itemId, itemOther */);
			return;
		}

		swapItemNew(slot, slotOther);
	}

	private void changeBankTabSize(int bankTab, int change) {
		int currentSize = getBankTabSize(bankTab);
		if (bankTab > -1) {
			int varbit = Varbit.BANK_TAB + bankTab;
			player.varps().setVarbit(varbit, currentSize + change);
			player.message("BankTab %d, size %d -> %d", bankTab, currentSize, currentSize + change);
		}
	}

	private int getBankTabSize(int bankTab) {
		return player.varps().getVarbit(Varbit.BANK_TAB + bankTab);
	}

	public int getBankTabOfSlot(int slot) {
		slot++;
		int[] sizes = new int[10];
		for (int i = 0; i < 9; i++) {
			sizes[i] = this.getBankTabSize(i);
			// System.out.println(i+" - "+sizes[i]);
		}

		int cnt = 0;
		for (int i = 0; i < sizes.length; i++) {
			if (slot >= cnt) {
				int size = sizes[i]; // size of tab i
				cnt += size;
				if (slot <= cnt) {
					return i;
				}
			}
		}

		return -1;
	}

	private void shiftItemsNew(int slot, int slotOther) {
		Item itemToInsert = bankItems.get(slot);
		// Item itemToInsertAt = bankItems.get(slotOther);
		// player.message("Item %s -> At %s", itemToInsert.toString(),
		// itemToInsertAt.toString());

		player.message("slot %d -> %d", slot, slotOther);

		int bankTabFrom = getBankTabOfSlot(slot);// this.getBankTab(slot);

		System.out.println("BANKTAB FROM " + bankTabFrom);
		// System.out.println("AND NEW FROM: " + getBankTabOfSlot(slot));

		int bankTabTo = getBankTabOfSlot(slotOther);// this.getBankTab(slotOther);

		changeBankTabSize(bankTabFrom, -1);
		changeBankTabSize(bankTabTo, 1);
		System.out.println("BANKTAB TO " + bankTabTo);

		// player.message("Banktab %d to %d", bankTabFrom, bankTabTo);

		// bankItems.remove(slot);

		bankItems.remove(slot);

		
		final boolean SAME_BANK_AND_MAIN = bankTabTo == bankTabFrom && bankTabTo ==-1;
			if (!SAME_BANK_AND_MAIN && bankTabTo == -1 ||(bankTabTo >= 1 && bankTabFrom < bankTabTo)){
				slotOther--;
				
		}
		bankItems.add(slotOther, itemToInsert);

		makeDirty();
	}

	private void swapItemNew(int slot, int slotOther) {
		Item item1 = bankItems.get(slot);
		Item item2 = bankItems.get(slotOther);
		bankItems.set(slot, item2);
		bankItems.set(slotOther, item1);
		makeDirty();
	}

	public void draggingToTabs(int slot, int slotOther) {
		if (slotOther == 10) {
			Item itemToPutToFirst = bankItems.get(slot);
			bankItems.remove(slot);

			int bankTabFrom = this.getBankTab(slot);
			player.message("BankTabFrom: %d", bankTabFrom);
			bankItems.add(itemToPutToFirst);

			int currentSize = player.varps().getVarbit(Varbit.BANK_TAB + bankTabFrom);
			player.varps().setVarbit(Varbit.BANK_TAB + bankTabFrom, currentSize - 1);
			makeDirty();
			return;
		}

		Item itemToPutToFirst = bankItems.get(slot);
		bankItems.remove(slot);

		int bankTab = slotOther - 11; // Starting from 0
		int slotToInsertAt = getSlotToInsertAt(bankTab);

		bankItems.add(slotToInsertAt, itemToPutToFirst);
		player.message("BankTab: %d, Slot to insert at: %d", bankTab, slotToInsertAt);

		int currentSize = player.varps().getVarbit(Varbit.BANK_TAB + bankTab);
		player.varps().setVarbit(Varbit.BANK_TAB + bankTab, currentSize + 1);

		makeDirty();
	}

	/**
	 * Gets the last slot of a bankTab.
	 * 
	 * @param bankTab
	 * @return
	 */
	public int getSlotToInsertAt(int bankTab) {
		int[] sizes = new int[9]; // Excluding main tab.
		int sizeCount = 0;
		for (int i = 0; i < sizes.length; i++) {
			sizes[i] = player.varps().getVarbit(Varbit.BANK_TAB + i);
		}

		for (int i = 0; i < sizes.length; i++) {
			sizeCount += sizes[i];
			if (i == bankTab) {
				return sizeCount;
			}
		}
		return -1;
	}

	/**
	 * Get the bank tab of slot.
	 * 
	 * @param slot
	 * @return
	 */
	public int getBankTab(int slot) {
		int[] sizes = new int[9]; // Excluding main tab.
		for (int i = 0; i < sizes.length; i++) {
			sizes[i] = player.varps().getVarbit(Varbit.BANK_TAB + i);
		}

		int slotAmt = 0;
		for (int i = sizes.length - 1; i >= 0; i--) {

			// player.message("Size of tab %d is %d", i, sizes[i]);

			slotAmt += sizes[i];// 0

			if (slot == 0)
				slot++;
			if (slotAmt >= slot) {// 0 == 0 true
				player.message("slotamt: %d slot: %d", slotAmt, slot);
				return i;
			}
		}
		return -1;
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
		if (!player.dead()) {
			moveItemOnItemNew(itemId, slot, itemOther, slotOther, hashthing);
			return;
		}

		if (hashthing == 10/* itemId == 65535 */ && slotOther >= 10 && slotOther <= 20) {// 10
																							// is
			handleBankTabs(itemOther, slotOther);
			return;
		}

		if (isInsertEnabled()) {
			shiftItems(itemId, itemOther, slot, slotOther/* itemId, itemOther */);
			return;
		}

		player.message("thisItem %d, otherItem %d", itemId, itemOther);
		swapItem(itemId, itemOther);
	}

	private void swapItem(int itemId, int itemOther) {

		BankTab myTab = this.getBankTabForItem(itemId);
		BankTab otherTab = this.getBankTabForItem(itemOther);

		int slot1 = myTab.getSlot(itemId);
		int slot2 = otherTab.getSlot(itemOther);
		player.message("Tab[%d](%d) to Tab[%d](%d)", myTab.getId(), slot1, otherTab.getId(), slot2);

		Item item1 = myTab.getItems().get(slot1);
		player.message("item1: %s", item1);
		Item item2 = otherTab.getItems().get(slot2);
		player.message("item2: %s", item2);

		myTab.getItems().set(slot1, item2);
		otherTab.getItems().set(slot2, item1);

		makeDirty();
	}

	private void handleBankTabs(int itemOther, int slotOther) {
		BankTab fromTab = getBankTabForItem(itemOther);
		BankTab targetBankTab = bankTabs[slotOther - 10];
		// player.message("Target bank tab : %d", targetBankTab.getId());

		int amount = fromTab.remove(itemOther);
		targetBankTab.add(new Item(itemOther, amount));

		makeDirty();
	}

	public ItemContainer getAllItems() {

		/*
		 * ItemContainer container = new ItemContainer(player.world(), 800,
		 * Type.FULL_STACKING); for (int i = 1; i < bankTabs.length; i++) {
		 * BankTab tab = bankTabs[i]; for (Item item : tab.getItems()) {
		 * container.add(item); } player.varps().setVarbit(tab.getVarbit(),
		 * tab.getItems().size()); }
		 * 
		 * // Add the main tab items. for (Item item : bankTabs[0].getItems())
		 * container.add(item);
		 * 
		 * return container;
		 */
		// return completeBank;
		return new ItemContainer(player.world(), 800, Type.FULL_STACKING);
	}

	public void handleClick(int buttonId, int slot, int option) {
		switch (buttonId) {
		case 10:
			currentBankTab = slot - 9;
			// player.message("currentbanktab:%d", currentBankTab);
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

		// tab options
		case 32:
			player.varps().setVarbit(Varbit.BANK_OPTIONS, slot + 1);
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
		player.write(new InvokeScript(917, -1, -2147483648)); // big screen
		player.interfaces().sendMain(12, false);
		player.interfaces().send(15, player.interfaces().activeRoot(), (player.interfaces().resizable() ? 58 : 60), false);

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
		player.messageDebug("Amount to withdraw: %d", amount);
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

		// player.message("Removing %d ... %d", id, amount);
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

		// player.message("amount:%d", amount);

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
			// player.message("ItemToInsert:" + itemToInsert);
			// player.message("ItemToInsertAt:" + itemoToRemove);
			int insertAtSlot = myTab.getSlot(itemoToRemove.getId());
			myTab.remove(itemToInsert);
			myTab.getItems().add(insertAtSlot, itemToInsert);
		} else {
			// player.message("Tab:%d -> Tab:%d", myTab.getId(),
			// otherTab.getId());
			// player.message("ItemToInsert:" + itemToInsert);
			int insertAtSlot = myTab.getSlot(itemoToRemove.getId());

			otherTab.remove(itemToInsert);
			myTab.getItems().add(insertAtSlot, itemToInsert);
		}
		makeDirty();
	}
}
