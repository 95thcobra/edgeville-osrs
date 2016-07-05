package edgeville.combat.magic;

import edgeville.model.entity.Player;
import edgeville.model.item.Item;
import edgeville.util.TextUtil;

public enum RegularDamageSpell {
	WIND_STRIKE(1, 711, 91, 12, 90, 92, 2, 4, new Item[] { new Item(Runes.AIR_RUNE), new Item(Runes.MIND_RUNE) }),
	WATER_STRIKE(5, 711, 94, 12, 93, 95, 4, 4, new Item[] { new Item(Runes.WATER_RUNE),new Item(Runes.AIR_RUNE), new Item(Runes.MIND_RUNE) }),
	EARTH_STRIKE(9, 711, 97, 12, 96, 98, 6, 4, new Item[] { new Item(Runes.EARTH_RUNE,2),new Item(Runes.AIR_RUNE), new Item(Runes.MIND_RUNE) }),
	FIRE_STRIKE(13, 711, 100, 12, 99, 101, 8, 4, new Item[] { new Item(Runes.FIRE_RUNE,3),new Item(Runes.AIR_RUNE,2), new Item(Runes.MIND_RUNE) }),
	
	WIND_BOLT(17, 	711, 118, 12, 	117, 119, 9, 4, new Item[] { new Item(Runes.FIRE_RUNE,3),new Item(Runes.AIR_RUNE,2), new Item(Runes.MIND_RUNE) }),
	WATER_BOLT(23, 	711, 121, 12, 	120, 122, 10, 4, new Item[] { new Item(Runes.FIRE_RUNE,3),new Item(Runes.AIR_RUNE,2), new Item(Runes.MIND_RUNE) }),
	EARTH_BOLT(29, 	711, 124, 12, 	123, 125, 11, 4, new Item[] { new Item(Runes.FIRE_RUNE,3),new Item(Runes.AIR_RUNE,2), new Item(Runes.MIND_RUNE) }),
	FIRE_BOLT(35, 	711, 127, 12, 	126, 128, 12, 4, new Item[] { new Item(Runes.FIRE_RUNE,3),new Item(Runes.AIR_RUNE,2), new Item(Runes.MIND_RUNE) }),
	
	WIND_BLAST(41, 	711, 133, 12, 132, 134, 8, 4, new Item[] { new Item(Runes.FIRE_RUNE,3),new Item(Runes.AIR_RUNE,2), new Item(Runes.MIND_RUNE) }),
	WATER_BLAST(47, 711, 136, 12, 135, 137, 8, 4, new Item[] { new Item(Runes.FIRE_RUNE,3),new Item(Runes.AIR_RUNE,2), new Item(Runes.MIND_RUNE) }),
	EARTH_BLAST(53, 711, 139, 12, 138, 140, 8, 4, new Item[] { new Item(Runes.FIRE_RUNE,3),new Item(Runes.AIR_RUNE,2), new Item(Runes.MIND_RUNE) }),
	FIRE_BLAST(59, 	711, 130, 12, 129, 131, 8, 4, new Item[] { new Item(Runes.FIRE_RUNE,3),new Item(Runes.AIR_RUNE,2), new Item(Runes.MIND_RUNE) }),
	
	WIND_WAVE(62, 	711, 159, 12, 158, 160, 8, 4, new Item[] { new Item(Runes.FIRE_RUNE,3),new Item(Runes.AIR_RUNE,2), new Item(Runes.MIND_RUNE) }),
	WATER_WAVE(65, 	711, 162, 12, 161, 163, 8, 4, new Item[] { new Item(Runes.FIRE_RUNE,3),new Item(Runes.AIR_RUNE,2), new Item(Runes.MIND_RUNE) }),
	EARTH_WAVE(70, 	711, 165, 12, 164, 166, 8, 4, new Item[] { new Item(Runes.FIRE_RUNE,3),new Item(Runes.AIR_RUNE,2), new Item(Runes.MIND_RUNE) }),
	FIRE_WAVE(75, 	711, 156, 12, 155, 157, 8, 4, new Item[] { new Item(Runes.FIRE_RUNE,3),new Item(Runes.AIR_RUNE,2), new Item(Runes.MIND_RUNE) }),
	
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
