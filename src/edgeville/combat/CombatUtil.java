package edgeville.combat;

import com.intellij.openapi.util.text.StringUtil;

import edgeville.model.entity.Player;
import edgeville.model.entity.player.EquipSlot;
import edgeville.model.item.Item;
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

		Item weapon = player.getEquipment().get(EquipSlot.WEAPON);
		if (weapon != null) {
			String wepName = weapon.definition(player.world()).name;

			if (StringUtil.containsIgnoreCase(wepName, "abyssal tentacle") || StringUtil.containsIgnoreCase(wepName, "abyssal whip")) {
				player.messageDebug("Hitting with whip");
				switch (player.varps().getVarp(Varp.ATTACK_STYLE)) {
				case 0:
					attackStyle = AttackStyle.ACCURATE;
					break;
				case 1:
					attackStyle = AttackStyle.CONTROLLED;
					break;
				case 3:
					attackStyle = AttackStyle.DEFENSIVE;
					break;
				}
				return attackStyle;
			}
		}

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
