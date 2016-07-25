package edgeville.combat.ranged;

import edgeville.combat.Graphic;
import edgeville.model.Entity;
import edgeville.model.entity.Player;
import edgeville.model.entity.player.Skills;

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

	public void doSpecialAction(Player player, Entity target) {
		switch (this) {
		case SAPPHIRE_BOLTS:
			if (target instanceof Player) {
				Player targetP = (Player)target;
				int prayerToDecrease = (int)Math.round(player.skills().level(Skills.RANGED) * (1.0/20.0));
				player.messageDebug("Prayer to decrease: %d", prayerToDecrease);
				targetP.skills().alterSkill(Skills.PRAYER, -prayerToDecrease, true);
				player.skills().alterSkillUnder99(Skills.PRAYER, (int)Math.round(prayerToDecrease / 2.0), true);
			}
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
