package edgeville.combat.Magic;

import edgeville.model.entity.Player;
import edgeville.model.item.Item;
import edgeville.util.TextUtil;

public enum RegularDamageSpell {
	WIND_STRIKE(1, 711, 91, 12, 90, 92, 2, 4, new Item[] { new Item(Runes.AIR_RUNE), new Item(Runes.MIND_RUNE) });

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
