package edgeville.bank;

import java.util.ArrayList;
import java.util.List;

import edgeville.model.item.Item;

public class BankTab {
	private int id;
	private int varbit;
	private List<Item> items;
	
	public BankTab(int id, int varbit) {
		this.id = id;
		this.varbit = varbit;
		this.items = new ArrayList<>();
	}
	
	public void add(Item item) {
		items.add(item);
	}
	
	public void remove(Item item) {
		for(int i = 0; i < items.size();i++) {
			if (items.get(i).getId() == item.getId()) {
				items.remove(i);
			}
		}
	}
	
	public void remove(int itemId) {
		for(int i = 0; i < items.size();i++) {
			if (items.get(i).getId() == itemId) {
				items.remove(i);
			}
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getVarbit() {
		return varbit;
	}

	public void setVarbit(int varbit) {
		this.varbit = varbit;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}
	
	
}
