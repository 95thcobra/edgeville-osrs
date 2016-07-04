package edgeville.combat.magic;

import edgeville.model.entity.Player;
import edgeville.model.item.Item;
import edgeville.util.TextUtil;

public enum RegularDamageSpell {
	WIND_STRIKE(1, 711, 91, 12, 90, 92, 2, 4, new Item[] { new Item(Runes.AIR_RUNE), new Item(Runes.MIND_RUNE) }),
	WATER_STRIKE(5, 711, 94, 12, 93, 95, 4, 4, new Item[] { new Item(Runes.WATER_RUNE),new Item(Runes.AIR_RUNE), new Item(Runes.MIND_RUNE) }),
	EARTH_STRIKE(5, 711, 97, 12, 96, 98, 6, 4, new Item[] { new Item(Runes.EARTH_RUNE,2),new Item(Runes.AIR_RUNE), new Item(Runes.MIND_RUNE) }),
	FIRE_STRIKE(5, 711, 100, 12, 99, 101, 8, 4, new Item[] { new Item(Runes.FIRE_RUNE,3),new Item(Runes.AIR_RUNE,2), new Item(Runes.MIND_RUNE) }),
	
	
	
	;

	private int levelReq;
	private int animation;
	private int projectileId;
	private int projectileSpeed;
	private int gfx;
	private int gfxOther;
	private int maxHit;
	private int combatDelayTicks;
	private Item[] runesRequired;

	RegularDamageSpell(int levelReq, int animation, int projectileId, int projectileSpeed, int gfx, int gfxOther, int maxHit, int combatDelayTicks, Item[] runesRequired) {
		this.levelReq = levelReq;
		this.animation = animation;
		this.projectileId = projectileId;
		this.projectileSpeed = projectileSpeed;
		this.gfx = gfx;
		this.gfxOther = gfxOther;
		this.maxHit = maxHit;
		this.combatDelayTicks = combatDelayTicks;
		this.runesRequired = runesRequired;
	}

	public int getLevelReq() {
		return levelReq;
	}

	public int getAnimation() {
		return animation;
	}

	public int getProjectileId() {
		return projectileId;
	}

	public int getProjectileSpeed() {
		return projectileSpeed;
	}

	public int getGfx() {
		return gfx;
	}

	public int getGfxOther() {
		return gfxOther;
	}

	public int getMaxHit() {
		return maxHit;
	}

	public int getCombatDelayTicks() {
		return combatDelayTicks;
	}

	public Item[] getRunesRequired() {
		return runesRequired;
	}

	public boolean hasRunes(Player player) {
		for (Item item : getRunesRequired()) {
			if (!player.getInventory().contains(item.getId(), item.getAmount())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return TextUtil.formatEnum(name());
	}

}
