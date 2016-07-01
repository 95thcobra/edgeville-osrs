package edgeville.combat.Magic;

public enum Spell {
	ICE_BLITZ(1978, 366, 12, 367, 26, 3),
	ICE_BARRAGE(1979, 366, 12, 369, 30, 4);

	private int animation;
	private int projectileId;
	private int speed;
	private int gfx;
	private int maxHit;
	private int combatDelayTicks;

	Spell(int animation, int projectileId, int speed, int gfx, int maxHit, int combatDelayTicks) {
		this.animation = animation;
		this.projectileId = projectileId;
		this.speed = speed;
		this.gfx = gfx;
		this.maxHit = maxHit;
		this.combatDelayTicks = combatDelayTicks;
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
	
	
}
