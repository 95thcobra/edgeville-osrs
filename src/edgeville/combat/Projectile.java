package edgeville.combat;

import org.apache.commons.lang3.StringUtils;

public enum Projectile {
	TRAINING_ARROW(9706, "training arrow", 16),
	BRONZE_ARROW(882, "bronze arrow", 10),
	IRON_ARROW(884, "iron arrow", 9), // ???
	STEEL_ARROW(886, "steel arrow", 8),
	// BLACK_ARROW(14),
	MITH_ARROW(888, "mithril arrow", 12),
	ADDY_ARROW(890, "adamant arrow", 13),
	DRAGON_ARROW(11212, "dragon arrow", 17),
	RUNE_ARROW(892, "rune arrow", 15);

	private int itemId;
	private String itemName;
	private int gfx;

	Projectile(int itemId, String itemName, int gfx) {
		this.itemId = itemId;
		this.itemName = itemName;
		this.gfx = gfx;
	}

	public static Projectile getProjectileForAmmoId(int itemId) {
		for (Projectile projectile : values()) {
			if (projectile.itemId == itemId) {
				return projectile;
			}
		}
		return null;
	}

	public static Projectile getProjectileForAmmoName(String itemName) {
		for (Projectile projectile : values()) {
			if (StringUtils.containsIgnoreCase(itemName, projectile.itemName)) {
				return projectile;
			}
		}
		return null;
	}

	public int getItemId() {
		return itemId;
	}

	public int getGfx() {
		return gfx;
	}
}
