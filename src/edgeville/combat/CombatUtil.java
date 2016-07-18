package edgeville.combat;

import org.apache.commons.lang3.StringUtils;

import edgeville.Constants;
import edgeville.model.Entity;
import edgeville.model.entity.Player;
import edgeville.model.entity.player.EquipSlot;
import edgeville.model.item.Item;
import edgeville.script.TimerKey;
import edgeville.util.Varp;

public class CombatUtil {
	private Player player;

	public static boolean canAttack(Player player, Entity target) {
		if (player.dead() || player.locked()) {
			return false;
		}
		if (!(target instanceof Player)) {
			return true;
		}
		Player targetP = (Player) target;
		
		if (player.inCombat() && targetP != player.getTarget() && player.getLastAttackedBy() != targetP) {
			player.message("You are already fighting someone else!");
			return false;
		}
		
		//player.message("bool1=%b, bool2=%b",  player != target.getLastAttackedBy(), target.getTarget() != player);
		
		player.setLastAttackedBy(null);
		/*
		if (target.timers().has(TimerKey.IN_COMBAT)) {
			if (target.getTarget() != player && target.getLastAttackedBy() != player) {
				player.message("This player is in combat!");
				return false;
			}
		}
		*/
		if (targetP.timers().has(TimerKey.IN_COMBAT) && player != target.getLastAttackedBy() && target.getTarget() != player) {	
			player.message("This player is in combat!");	
			return false;
		}
		
		targetP.timers().register(TimerKey.IN_COMBAT, 5);
		player.setTarget(target);
		target.setLastAttackedBy(player);
		
		if (!Constants.ALL_PVP) {
			if (!player.inWilderness() || !((Player) target).inWilderness()) {
				player.message("You or your target are not in wild.");
				return false;
			}
		} else {
			if (player.inSafeArea() && !targetP.canBeAttackInSafeArea()) {
				player.message("You or your target are in a safe area!");
				return false;
			}
			if (!player.inSafeArea() && targetP.inSafeArea() && !targetP.canBeAttackInSafeArea()) {
				player.message("You or your target are in a safe area!");
				return false;
			}
			if (targetP.inSafeArea() && !targetP.canBeAttackInSafeArea()) {
				player.message("You or your target are in a safe area!");
				return false;
			}

			return true;
		}
		return true;
	}

	public CombatUtil(Player player) {
		this.player = player;
	}

	public enum AttackStyle {
		ACCURATE, AGGRESSIVE, CONTROLLED, DEFENSIVE
	}

	public void setAttackStyle(AttackStyle attackStyle) {
		switch (attackStyle) {
		case ACCURATE:
			player.getVarps().setVarp(Varp.ATTACK_STYLE, 0);
			break;
		case AGGRESSIVE:
			player.getVarps().setVarp(Varp.ATTACK_STYLE, 1);
			break;
		case CONTROLLED:
			player.getVarps().setVarp(Varp.ATTACK_STYLE, 2);
			break;
		case DEFENSIVE:
			player.getVarps().setVarp(Varp.ATTACK_STYLE, 3);
			break;
		}
	}

	public AttackStyle getAttackStyle() {
		AttackStyle attackStyle = AttackStyle.ACCURATE;

		Item weapon = player.getEquipment().get(EquipSlot.WEAPON);
		if (weapon != null) {
			String wepName = weapon.definition(player.world()).name;

			if (StringUtils.containsIgnoreCase(wepName, "abyssal tentacle")
					|| StringUtils.containsIgnoreCase(wepName, "abyssal whip")) {
				player.messageDebug("Hitting with whip");
				switch (player.getVarps().getVarp(Varp.ATTACK_STYLE)) {
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

		switch (player.getVarps().getVarp(Varp.ATTACK_STYLE)) {
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

	public enum SlashStabCrunch {
		SLASH, STAB, CRUNCH
	}

	public static SlashStabCrunch getSlashStabCrunch(Player player) {
		SlashStabCrunch slashStabCrunch = SlashStabCrunch.SLASH;
		// AttackStyle attackStyle = AttackStyle.ACCURATE;

		Item weapon = player.getEquipment().get(EquipSlot.WEAPON);
		if (weapon != null) {
			String wepName = weapon.definition(player.world()).name;

			if (StringUtils.containsIgnoreCase(wepName, "abyssal tentacle")
					|| StringUtils.containsIgnoreCase(wepName, "abyssal whip")) {
				player.messageDebug("Hitting with whip");
				return SlashStabCrunch.SLASH;
			}
		}

		switch (player.getVarps().getVarp(Varp.ATTACK_STYLE)) {
		case 2:
			slashStabCrunch = SlashStabCrunch.STAB;
			break;
		}
		return slashStabCrunch;
	}
}
