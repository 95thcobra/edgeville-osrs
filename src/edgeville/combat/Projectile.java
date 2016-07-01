package edgeville.combat;

import org.apache.commons.lang3.StringUtils;

public enum Projectile {
	TRAINING_ARROW(9706, "training arrow", 16, new Graphic(25, 92, 0)),
	BRONZE_ARROW(882, "bronze arrow", 10, new Graphic(19, 92, 0)),
	IRON_ARROW(884, "iron arrow", 9, new Graphic(18, 92, 0)),
	STEEL_ARROW(886, "steel arrow", 11, new Graphic(20, 92, 0)),
	// BLACK_ARROW(14),
	MITH_ARROW(888, "mithril arrow", 12, new Graphic(21, 92, 0)),
	ADDY_ARROW(890, "adamant arrow", 13, new Graphic(22, 92, 0)),
	DRAGON_ARROW(11212, "dragon arrow", 17, new Graphic(26, 92, 0)),
	RUNE_ARROW(892, "rune arrow", 15, new Graphic(24, 92, 0)),

	BRONZE_KNIFE(864, "bronze knife", 219, new Graphic(219, 92, 0)),
	IRON_KNIFE(863, "iron knife", 213, new Graphic(220, 92, 0)),
	STEEL_KNIFE(865, "steel knife", 214, new Graphic(221, 92, 0)),
	BLACK_KNIFE(869, "black knife", 215, new Graphic(222, 92, 0)),
	MITHRIL_KNIFE(866, "mithril knife", 216, new Graphic(223, 92, 0)),
	ADAMANT_KNIFE(867, "adamant knife", 217, new Graphic(224, 92, 0)),
	RUNE_KNIFE(868, "rune knife", 218, new Graphic(225, 92, 0)),

	;

	private int itemId;
	private String itemName;
	private int projectileId;
	private Graphic gfx;

	Projectile(int itemId, String itemName, int projectileId, Graphic gfx) {
		this.itemId = itemId;
		this.itemName = itemName;
		this.projectileId = projectileId;
		this.gfx = gfx;
	}

	/*
	 * Projectile(int itemId, String itemName, int projectileId) { this.itemId =
	 * itemId; this.itemName = itemName; this.gfx = gfx; }
	 */

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
	
	public int getProjectileId() {
		return projectileId;
	}
	
	public Graphic getGfx() {
		return gfx;
	}
}
