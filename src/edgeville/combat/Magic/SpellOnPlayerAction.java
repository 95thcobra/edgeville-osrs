package edgeville.combat.Magic;

import org.apache.commons.lang3.StringUtils;

import edgeville.combat.Graphic;
import edgeville.event.Event;
import edgeville.event.EventContainer;
import edgeville.model.Entity;
import edgeville.model.Tile;
import edgeville.model.entity.Player;
import edgeville.model.entity.player.Skills;
import edgeville.model.item.Item;
import edgeville.script.TimerKey;
import edgeville.util.AccuracyFormula;
import edgeville.util.CombatStyle;
import edgeville.util.TextUtil;

public class SpellOnPlayerAction {

	private Player player;
	private Entity target;
	private int interfaceId;
	private int child;

	public SpellOnPlayerAction(Player player, Entity target, int interfaceId, int child) {
		this.player = player;
		this.target = target;
		this.interfaceId = interfaceId;
		this.child = child;
	}

	public void start() {
		switch (interfaceId) {
		case 218:
			handleAncients();
			break;
		}
	}

	private void handleAncients() {
		player.messageDebug("ancientspell:" + child);
		switch (child) {

		case 74:
			cycleDoMagicSpell(DamageSpell.SMOKE_RUSH);
			break;
		case 78:
			cycleDoMagicSpell(DamageSpell.SHADOW_RUSH);
			break;
		case 70:
			cycleDoMagicSpell(DamageSpell.BLOOD_RUSH);
			break;
		case 66:
			cycleDoMagicSpell(DamageSpell.ICE_RUSH);
			break;

		case 76:
			cycleDoMagicSpell(DamageSpell.SMOKE_BURST);
			break;
		case 80:
			cycleDoMagicSpell(DamageSpell.SHADOW_BURST);
			break;
		case 72:
			cycleDoMagicSpell(DamageSpell.BLOOD_BURST);
			break;
		case 68:
			cycleDoMagicSpell(DamageSpell.ICE_BURST);
			break;

		case 75:
			cycleDoMagicSpell(DamageSpell.SMOKE_BLITZ);
			break;
		case 79:
			cycleDoMagicSpell(DamageSpell.SHADOW_BLITZ);
			break;
		case 71:
			cycleDoMagicSpell(DamageSpell.BLOOD_BLITZ);
			break;
		case 67:
			cycleDoMagicSpell(DamageSpell.ICE_BLITZ);
			break;

		case 77:
			cycleDoMagicSpell(DamageSpell.SMOKE_BARRAGE);
			break;
		case 81:
			cycleDoMagicSpell(DamageSpell.SHADOW_BARRAGE);
			break;
		case 73:
			cycleDoMagicSpell(DamageSpell.BLOOD_BARRAGE);
			break;
		case 69:
			cycleDoMagicSpell(DamageSpell.ICE_BARRAGE);
			break;
		}
	}

	public Tile moveCloser() {
		player.pathQueue().clear();

		int steps = player.pathQueue().running() ? 2 : 1;
		int otherSteps = target.pathQueue().running() ? 2 : 1;

		Tile otherTile = target.pathQueue().peekAfter(otherSteps) == null ? target.getTile() : target.pathQueue().peekAfter(otherSteps).toTile();
		player.stepTowards(target, otherTile, 25);
		return player.pathQueue().peekAfter(steps - 1) == null ? player.getTile() : player.pathQueue().peekAfter(steps - 1).toTile();
	}

	private void cycleDoMagicSpell(DamageSpell spell) {
		int levelReq = spell.getLevelReq();
		if (levelReq > player.skills().level(Skills.MAGIC)) {
			player.message("You need a magic level of %d to cast %s.", levelReq,spell.toString());
			return;
		}
		
		if (!spell.hasRunes(player)) {
			player.message("You do not have the required runes to cast %s.",spell.toString());
			return;
		}
		
		player.world().getEventHandler().addEvent(player, new Event() {

			@Override
			public void execute(EventContainer container) {
				if (target.locked() || target.dead()) {
					container.stop();
					return;
				}
				doMagicSpell(spell, container);
			}
		});
	}

	private boolean doMagicSpell(DamageSpell spell, EventContainer container) {
		if (player.getTile().distance(target.getTile()) > 7 && !player.frozen() && !player.stunned()) {
			player.stepTowards(target, 2);
			return false;
		}
		if (player.timers().has(TimerKey.COMBAT_ATTACK)) {
			return false;
		}

		int tileDist = player.getTile().distance(target.getTile());
		player.animate(spell.getAnimation());

		if (spell.getProjectileId() > 0/* && !target.pathQueue().empty() */) {
			player.world().spawnProjectile(player.getTile(), target, spell.getProjectileId(), 0, 0, 36, spell.getSpeed() * tileDist, 0, 0);
		}

		int delay = (int) (1 + Math.floor(tileDist) / 2.0);
		player.timers().register(TimerKey.COMBAT_ATTACK, spell.getCombatDelayTicks());
		boolean success = AccuracyFormula.doesHit(player, target, CombatStyle.MAGIC);

		int hit = player.world().random(spell.getMaxHit());

		if (success) {
			target.hit(player, hit, delay, CombatStyle.MAGIC).graphic(spell.getGfx());
			if (target instanceof Player) {
				if (spell.getSoundId() > 0)
					((Player) target).sound(spell.getSoundId());
			}
			player.sound(spell.getSoundId());

			// TODO smoke poisons
			
			if (spell == DamageSpell.SHADOW_RUSH || spell == DamageSpell.SHADOW_BURST) {
				if (target instanceof Player)
					((Player) target).skills().alterSkill(Skills.ATTACK, 0.9);
			}
			if (spell == DamageSpell.SHADOW_BLITZ || spell == DamageSpell.SHADOW_BARRAGE) {
				if (target instanceof Player)
					((Player) target).skills().alterSkill(Skills.ATTACK, 0.85);
			}

			if (spell == DamageSpell.BLOOD_RUSH || spell == DamageSpell.BLOOD_BURST || spell == DamageSpell.BLOOD_BLITZ || spell == DamageSpell.BLOOD_BARRAGE) {
				player.heal(hit / 4);
			}
			
			if (spell == DamageSpell.ICE_RUSH) {
				target.freeze(8); // 5 second freeze timer
			}
			
			if (spell == DamageSpell.ICE_BARRAGE) {
				target.freeze(17); // 10 second freeze timer
			}

			if (spell == DamageSpell.ICE_BLITZ) {
				target.freeze(25); // 15 second freeze timer
			}

			if (spell == DamageSpell.ICE_BARRAGE) {
				target.freeze(33); // 20 second freeze timer
			}

		} else {
			target.hit(player, 0, delay).graphic(new Graphic(85, 92, 0));
		}
		container.stop();
		return true;
	}
}
