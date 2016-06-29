package edgeville.model.entity.player.skills;

import edgeville.model.entity.Player;

public class Prayer {
	private Player player;
	
	public static final int VARBIT_THICK_SKIN = 4104;
	
	public Prayer(Player player) {
		this.player = player;
	}
	
	public void togglePrayer(int buttonId) {
		int varbit = 4100 + buttonId;
		player.message("varbit: varbit");
		boolean enabled = isPrayerOn(varbit);
		player.varps().setVarbit(varbit, enabled ? 0 : 1);
	}
	
	public void activatePrayer(int slot) {
		int varbit = 4100 + slot;
		player.varps().setVarbit(varbit, 1);
	}
	
	public void deactivatePrayer(int slot) {
		int varbit = 4100 + slot;
		player.varps().setVarbit(varbit, 0);
	}
	
	public boolean isPrayerOn(int id) {
		return player.varps().getVarbit(id) == 1;
	}
}
