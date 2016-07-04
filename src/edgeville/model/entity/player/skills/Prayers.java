package edgeville.model.entity.player.skills;

import edgeville.model.entity.Player;

public enum Prayers {
	// First row
	THICK_SKIN(					4104, 					-1,-1,-1,new int[] {4107,4113 , 4128,4129}),
	BURST_OF_STRENGTH(			4105, 			-1,-1,-1,new int[] { 4108, 4114 ,4122, 4123,4124,4125,4126,4127, 4128,4129}),
	CLARITY_OF_THOUGHT(			4106, 			-1,-1,-1,new int[] { 4109,4115,4122, 4123,4124,4125,4126,4127, 4128,4129}),
	SHARP_EYE(					4122, 					-1,-1,-1,new int[] { 4125,4127,4123,4124,4126,4105, 4106,4108,4109,4114,4115, 4128,4129 }),
	MYSTIC_WILL(				4123, 					-1,-1,-1,new int[] { 4125,4127,4122,4124,4126 ,4105, 4106,4108,4109,4114,4115, 4128,4129}),

	// Second row
	ROCK_SKIN(					4107, -1,-1,-1,new int[] { 4104, 4113 , 4128,4129}),
	SUPERHUMAN_STRENGTH(		4108, -1,-1,-1,new int[] {4105, 4114,4122, 4123,4124,4125,4126,4127, 4128,4129 }),
	IMPROVED_REFLEXES(			4109, -1,-1,-1,new int[] {4106,4115 ,4122, 4123,4124,4125,4126,4127, 4128,4129}),
	
	RAPID_RESTORE(				4110, -1,-1,-1,new int[] { }),
	RAPID_HEAL(					4111, -1,-1,-1,new int[] { }),

	// Third row
	PROTECT_ITEM(				4112, -1,-1,-1,new int[] { }),
	HAWK_EYE(					4124, -1,-1,-1,new int[] { 4125,4127,4122,4123,4126 , 4105, 4106,4108,4109,4114,4115, 4128,4129}),
	MYSTIC_LORE(				4125, -1,-1,-1,new int[] { 4124,4127,4122,4123,4126   , 4105, 4106,4108,4109,4114,4115, 4128,4129}),
	STEEL_SKIN(					4113, -1,-1,-1,new int[] { 4104, 4107, 4128,4129 }),
	ULTIMATE_STRENGTH(			4114, -1,-1,-1,new int[] { 4108, 4105 ,4122, 4123,4124,4125,4126,4127, 4128,4129}),

	// Fourth row
	INCREDIBLE_REFLEXES(		4115, -1,-1,-1,new int[] { 4109,4106,4122, 4123,4124,4125,4126,4127, 4128,4129}),
	PROTECT_FROM_MAGIC(			4116, -1,2,-1,new int[] { /*prot missiles*/4117, 4118, 4119 ,4121 }),
	PROTECT_FROM_MISSILES(		4117, -1,1,-1,new int[] { 4116, 4118,4119 ,4121,4120}),
	PROTECT_FROM_MELEE(			4118, 30,0,-1,new int[] { 4116, 4117,4119 ,4121,4120}),
	EAGLE_EYE(					4126, -1,-1,-1,new int[] { 4125,4127,4122,4123,4124,4105, 4106,4108,4109,4114,4115, 4128,4129}),

	// Fifth row
	MYSTIC_MIGHT(				4127, -1,-1,-1,new int[] {4125,4124,4122,4123,4126 ,4105, 4106,4108,4109,4114,4115, 4128,4129}),
	RETRIBUTION(				4119, -1,3,-1,new int[] { 4116,4117,4118, 4121, 4120}),
	REDEMPTION(					4120, -1,5,-1,new int[] { 4119, 4121, 4116,4117,4118 }),
	SMITE(						4121, -1,4,-1,new int[] { 4116,4117,4118, 4119,4120}),
	
	CHILVAlRY(					4128, -1,-1,-1,new int[] {4129 ,4105, 4106,4108,4109,4114,4115,4122, 4123,4124,4125,4126,4127,4104, 4107, 4113}),

	PIETY(						4129, -1,-1,-1,new int[] {4128,4105, 4106,4108,4109,4114,4115,4122, 4123,4124,4125,4126,4127,4104, 4107, 4113});

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
