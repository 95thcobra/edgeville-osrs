package edgeville.model.entity.player.skills;

import edgeville.aquickaccess.actions.EquipmentRequirement.Skill;
import edgeville.event.Event;
import edgeville.event.EventContainer;
import edgeville.model.entity.Player;
import edgeville.model.entity.player.Skills;
import edgeville.script.Timer;
import edgeville.util.Varp;

public class Prayer {
	private Player player;

	public Prayer(Player player) {
		this.player = player;
		prepare();
	}

	private void prepare() {
		player.varps().setVarp(Varp.PIETY_AND_CHILVALRY, 16);
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
		// TODO IF OVERHEADICON
		int varbit = 4100 + buttonId;

		Prayers prayer = Prayers.getPrayerForVarbit(varbit);
		if (prayer == null) {
			return;
		}

		if (player.skills().level(Skills.PRAYER) <= 0) {
			player.message("You have run out of prayer!");
			return;
		}

		if (!hasRequirements(prayer)) {
			player.message("Your prayer level is too low, you need " + prayer.getPrayerLevelReq() + " to use this prayer.");
			deactivatePrayer(prayer);
			return;
		}

		prayer.deactivatePrayers(player);
		player.varps().setVarbit(varbit, 1);
		if (prayer.getHeadIcon() > -1) {
			player.setPrayerHeadIcon(prayer.getHeadIcon());
		}

		player.world().getEventHandler().addEvent(player, prayer.getDrain(), false, new Event() {
			@Override
			public void execute(EventContainer container) {
				player.skills().alterSkill(Skills.PRAYER, -1, true);

				if (player.skills().level(Skills.PRAYER) <= 0) {
					deactivatePrayer(buttonId);
					container.stop();
				}
				if (!isPrayerOn(varbit)) {
					container.stop();
				}
			}
		});
	}

	public void deactivateAllPrayers() {
		for (Prayers prayer : Prayers.values()) {
			deactivatePrayer(prayer);
		}
	}

	private boolean hasRequirements(Prayers prayer) {
		// TODO chivalry and piety additional reqs.
		return player.skills().xpLevel(Skills.PRAYER) >= prayer.getPrayerLevelReq();
	}

	public void deactivatePrayer(Prayers prayer) {
		player.varps().setVarbit(prayer.getVarbit(), 0);
		if (prayer.getHeadIcon() > -1) {
			player.setPrayerHeadIcon(-1);
		}
	}

	public void deactivatePrayer(int buttonId) {
		int varbit = 4100 + buttonId;
		Prayers prayer = Prayers.getPrayerForVarbit(varbit);
		if (prayer == null) {
			return;
		}
		player.varps().setVarbit(varbit, 0);
		if (prayer.getHeadIcon() > -1) {
			player.setPrayerHeadIcon(-1);
		}
	}

	public boolean isPrayerOn(Prayers prayer) {
		return player.varps().getVarbit(prayer.getVarbit()) == 1;
	}

	public boolean isPrayerOn(int varbit) {
		return player.varps().getVarbit(varbit) == 1;
	}
}
