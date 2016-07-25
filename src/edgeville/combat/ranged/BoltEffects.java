package edgeville.combat.ranged;

import edgeville.combat.Graphic;

public enum BoltEffects {
	SAPPHIRE_BOLTS(9240, new Graphic(750), 5/* may not be correct */);

	private int boltId;
	private Graphic enemyGraphic;
	private int extraDamage;

	BoltEffects(int boltId, Graphic enemyGraphic, int extraDamage) {
		this.boltId = boltId;
		this.enemyGraphic = enemyGraphic;
		this.extraDamage = extraDamage;
	}

	public void doSpecialAction() {
		switch (this) {
		case SAPPHIRE_BOLTS:
			// extra damage
			break;
		}
	}

	public int getExtraDamage() {
		return extraDamage;
	}

	public int getBoltId() {
		return boltId;
	}

	public Graphic getEnemyGraphic() {
		return enemyGraphic;
	}
}
