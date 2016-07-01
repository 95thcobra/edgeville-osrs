package edgeville.combat.Magic;

import edgeville.combat.Graphic;
import edgeville.event.Event;
import edgeville.event.EventContainer;
import edgeville.model.Entity;
import edgeville.model.Tile;
import edgeville.model.entity.Player;
import edgeville.model.entity.player.Skills;
import edgeville.script.TimerKey;
import edgeville.util.AccuracyFormula;
import edgeville.util.CombatStyle;

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
			cycleDoMagicSpell(Spell.SMOKE_RUSH);
			break;
		case 78:
			cycleDoMagicSpell(Spell.SHADOW_RUSH);
			break;
		case 70:
			cycleDoMagicSpell(Spell.BLOOD_RUSH);
			break;
		case 66:
			cycleDoMagicSpell(Spell.ICE_RUSH);
			break;

		case 76:
			cycleDoMagicSpell(Spell.SMOKE_BURST);
			break;
		case 80:
			cycleDoMagicSpell(Spell.SHADOW_BURST);
			break;
		case 72:
			cycleDoMagicSpell(Spell.BLOOD_BURST);
			break;
		case 68:
			cycleDoMagicSpell(Spell.ICE_BURST);
			break;

		case 75:
			cycleDoMagicSpell(Spell.SMOKE_BLITZ);
			break;
		case 79:
			cycleDoMagicSpell(Spell.SHADOW_BLITZ);
			break;
		case 71:
			cycleDoMagicSpell(Spell.BLOOD_BLITZ);
			break;
		case 67:
			cycleDoMagicSpell(Spell.ICE_BLITZ);
			break;

		case 77:
			cycleDoMagicSpell(Spell.SMOKE_BARRAGE);
			break;
		case 81:
			cycleDoMagicSpell(Spell.SHADOW_BARRAGE);
			break;
		case 73:
			cycleDoMagicSpell(Spell.BLOOD_BARRAGE);
			break;
		case 69:
			cycleDoMagicSpell(Spell.ICE_BARRAGE);
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

	private void cycleDoMagicSpell(Spell spell) {
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

	private boolean doMagicSpell(Spell spell, EventContainer container) {
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
			
			if (spell == Spell.SHADOW_RUSH || spell == Spell.SHADOW_BURST) {
				if (target instanceof Player)
					((Player) target).skills().alterSkill(Skills.ATTACK, 0.9);
			}
			if (spell == Spell.SHADOW_BLITZ || spell == Spell.SHADOW_BARRAGE) {
				if (target instanceof Player)
					((Player) target).skills().alterSkill(Skills.ATTACK, 0.85);
			}

			if (spell == Spell.BLOOD_RUSH || spell == Spell.BLOOD_BURST || spell == Spell.BLOOD_BLITZ || spell == Spell.BLOOD_BARRAGE) {
				player.heal(hit / 4);
			}
			
			if (spell == Spell.ICE_RUSH) {
				target.freeze(8); // 5 second freeze timer
			}
			
			if (spell == Spell.ICE_BARRAGE) {
				target.freeze(17); // 10 second freeze timer
			}

			if (spell == Spell.ICE_BLITZ) {
				target.freeze(25); // 15 second freeze timer
			}

			if (spell == Spell.ICE_BARRAGE) {
				target.freeze(33); // 20 second freeze timer
			}

		} else {
			target.hit(player, 0, delay).graphic(new Graphic(85, 92, 0));
		}
		container.stop();
		return true;
	}
}
