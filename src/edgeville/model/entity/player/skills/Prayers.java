package edgeville.model.entity.player.skills;

import edgeville.model.entity.Player;

public enum Prayers {
	// First row
	THICK_SKIN(					4104, 					-1,-1,-1,new int[] { }),
	BURST_OF_STRENGTH(			4105, 			-1,-1,-1,new int[] { }),
	CLARITY_OF_THOUGHT(			4106, 			-1,-1,-1,new int[] { }),
	SHARP_EYE(					4122, 					-1,-1,-1,new int[] { }),
	MYSTYIC_WILL(				4123, 					-1,-1,-1,new int[] { }),

	// Second row
	ROCK_SKIN(					4107, -1,-1,-1,new int[] { }),
	SUPERHUMAN_STRENGTH(		4108, -1,-1,-1,new int[] { }),
	IMPROVED_REFLEXES(			4109, -1,-1,-1,new int[] { }),
	RAPID_RESTORE(				4110, -1,-1,-1,new int[] { }),
	RAPID_HEAL(					4111, -1,-1,-1,new int[] { }),

	// Third row
	PROTECT_ITEM(				4112, -1,-1,-1,new int[] { }),
	HAWK_EYE(					4124, -1,-1,-1,new int[] { }),
	MYSTIC_LORE(				4125, -1,-1,-1,new int[] { }),
	STEEL_SKIN(					4113, -1,-1,-1,new int[] { }),
	ULTIMATE_STRENGTH(			4114, -1,-1,-1,new int[] { }),

	// Fourth row
	INCREDIBLE_REFLEXES(		4115, -1,-1,-1,new int[] { }),
	PROTECT_FROM_MAGIC(			4116, -1,-1,-1,new int[] { /*prot missiles*/4117 }),
	PROTECT_FROM_MISSILES(		4117, -1,-1,-1,new int[] { }),
	PROTECT_FROM_MELEE(			4118, 30,-1,-1,new int[] { }),
	EAGLE_EYE(					4126, -1,-1,-1,new int[] { }),

	// Fifth row
	MYSTIC_MIGHT(				4127, -1,-1,-1,new int[] { }),
	RETRIBUTION(				4119, -1,-1,-1,new int[] { }),
	REDEMPTION(					4120, -1,-1,-1,new int[] { }),
	SMITE(						4121, -1,-1,-1,new int[] { }),
	CHILVAlRY(					4128, -1,-1,-1,new int[] { }),

	PIETY(						4129, -1,-1,-1,new int[] { });

	private int varbit;
	private int drain;
	private int prayerLevelReq;
	private int headIcon;
	private int[] prayerVarbitsToDeactivate;

	Prayers(int varbit, int prayerLevelReq, int headIcon, int drain, int[] prayerVarbitsToDeactivate) {
		this.varbit = varbit;
		this.drain = drain;
		this.prayerLevelReq = prayerLevelReq;
		this.headIcon = headIcon;
		this.prayerVarbitsToDeactivate = prayerVarbitsToDeactivate;
	}

	public int getVarbit() {
		return varbit;
	}

	public int[] getPrayersToDeactivate() {
		return prayerVarbitsToDeactivate;
	}
	
	public int getDrain() {
		return drain;
	}

	public int getPrayerLevelReq() {
		return prayerLevelReq;
	}

	public int getHeadIcon() {
		return headIcon;
	}

	public static Prayers getPrayerForVarbit(int varbit) {
		for(Prayers prayer : values()) {
			if (prayer.getVarbit() == varbit) {
				return prayer;
			}
		}
		return null;
	}
	
	public void deactivatePrayers(Player player) {
		for(int varbit : prayerVarbitsToDeactivate) {
			player.varps().setVarbit(varbit, 0);
		}
	}
}
