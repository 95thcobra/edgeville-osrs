package edgeville.combat;

import edgeville.model.entity.Player;
import edgeville.util.Varp;

public class CombatUtil {
	private Player player;

	public CombatUtil(Player player) {
		this.player = player;
	}

	public enum AttackStyle {
		ACCURATE, AGGRESSIVE, CONTROLLED, DEFENSIVE
	}

	public void setAttackStyle(AttackStyle attackStyle) {
		switch (attackStyle) {
		case ACCURATE:
			player.varps().setVarp(Varp.ATTACK_STYLE, 0);
			break;
		case AGGRESSIVE:
			player.varps().setVarp(Varp.ATTACK_STYLE, 1);
			break;
		case CONTROLLED:
			player.varps().setVarp(Varp.ATTACK_STYLE, 2);
			break;
		case DEFENSIVE:
			player.varps().setVarp(Varp.ATTACK_STYLE, 3);
			break;
		}
	}

	public AttackStyle getAttackStyle() {
		AttackStyle attackStyle = AttackStyle.ACCURATE;
		switch (player.varps().getVarp(Varp.ATTACK_STYLE)) {
		case 0:
			attackStyle = AttackStyle.ACCURATE;
			break;
		case 1:
			attackStyle = AttackStyle.AGGRESSIVE;
			break;
		case 2:
			attackStyle = AttackStyle.CONTROLLED;
			break;
		case 3:
			attackStyle = AttackStyle.DEFENSIVE;
			break;
		}
		return attackStyle;
	}
}
