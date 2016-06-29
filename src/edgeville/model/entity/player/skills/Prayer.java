package edgeville.model.entity.player.skills;

import edgeville.model.entity.Player;
import edgeville.model.entity.player.Skills;

public class Prayer {
	private Player player;

	public Prayer(Player player) {
		this.player = player;
	}

	public void togglePrayer(int buttonId) {
		int varbit = 4100 + buttonId;
		boolean enabled = isPrayerOn(varbit);
		
		if (enabled) {
			deactivatePrayer(buttonId);
		} else {
			activatePrayer(buttonId);
		}
	}

	public void activatePrayer(int buttonId) {
		int varbit = 4100 + buttonId;
		
		Prayers prayer = Prayers.getPrayerForVarbit(varbit);
		if (prayer == null) {
			return;
		}	
	
		if (!hasRequirements(prayer)) {
			player.message("Your prayer level is too low, you need " + prayer.getPrayerLevelReq() + " to use this prayer.");
			deactivatePrayer(prayer);
			return;
		}
		
		prayer.deactivatePrayers(player);
		player.varps().setVarbit(varbit, 1);
	}
	
	private boolean hasRequirements(Prayers prayer) {
		//TODO chivalry and piety additional reqs.
		return player.skills().level(Skills.PRAYER) >= prayer.getPrayerLevelReq();
	}
	
	public void deactivatePrayer(Prayers prayer) {
		player.varps().setVarbit(prayer.getVarbit(), 0);
	}

	public void deactivatePrayer(int buttonId) {
		int varbit = 4100 + buttonId;
		player.varps().setVarbit(varbit, 0);
	}
	
	public boolean isPrayerOn(Prayers prayer) {
		return player.varps().getVarbit(prayer.getVarbit()) == 1;
	}

	public boolean isPrayerOn(int varbit) {
		return player.varps().getVarbit(varbit) == 1;
	}
}
