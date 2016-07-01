package edgeville.combat.Magic;

public enum Spell {
	SMOKE_RUSH(1978, -1, 12, 385, 14, 4),
	SHADOW_RUSH(1978, -1, 12, 379, 15, 4),
	BLOOD_RUSH(1978, -1, 12, 373, 16, 4),
	ICE_RUSH(1978, 360, 12, 361, 17, 4),

	SMOKE_BURST(1979, -1, 12, 382, 19, 5),
	SHADOW_BURST(1979, -1, 12, 382, 19, 5),
	BLOOD_BURST(1979, -1, 12, 376, 21, 5),
	ICE_BURST(1979, -1, 12, 363, 22, 5, 1126),

	SMOKE_BLITZ(1978, -1, 12, 387, 23, 4),
	SHADOW_BLITZ(1978, -1, 12, 381, 24, 4),
	BLOOD_BLITZ(1978, -1, 12, 375, 25, 4),
	ICE_BLITZ(1978, 366, 12, 367, 26, 4),

	SMOKE_BARRAGE(1978, -1, 12, 391, 27, 5),
	SHADOW_BARRAGE(1979, -1, 12, 383, 28, 5),
	BLOOD_BARRAGE(1979, -1, 12, 377, 29, 5),
	ICE_BARRAGE(1979, 366, 12, 369, 30, 5, 1125);

	private int animation;
	private int projectileId;
	private int speed;
	private int gfx;
	private int maxHit;
	private int combatDelayTicks;
	private int soundIdOnImpact;

	Spell(int animation, int projectileId, int speed, int gfx, int maxHit, int combatDelayTicks) {
		this.animation = animation;
		this.projectileId = projectileId;
		this.speed = speed;
		this.gfx = gfx;
		this.maxHit = maxHit;
		this.combatDelayTicks = combatDelayTicks;
	}

	Spell(int animation, int projectileId, int speed, int gfx, int maxHit, int combatDelayTicks, int soundIdOnImpact) {
		this.animation = animation;
		this.projectileId = projectileId;
		this.speed = speed;
		this.gfx = gfx;
		this.maxHit = maxHit;
		this.combatDelayTicks = combatDelayTicks;
		this.soundIdOnImpact = soundIdOnImpact;
	}

	public int getAnimation() {
		return animation;
	}

	public int getProjectileId() {
		return projectileId;
	}

	public int getSpeed() {
		return speed;
	}

	public int getGfx() {
		return gfx;
	}

	public int getMaxHit() {
		return maxHit;
	}

	public int getCombatDelayTicks() {
		return combatDelayTicks;
	}
	
	public int getSoundId() {
		return soundIdOnImpact;
	}

}
