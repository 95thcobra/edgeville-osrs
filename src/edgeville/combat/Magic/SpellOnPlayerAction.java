package edgeville.combat.Magic;

import edgeville.combat.Graphic;
import edgeville.event.Event;
import edgeville.event.EventContainer;
import edgeville.model.Entity;
import edgeville.model.Tile;
import edgeville.model.entity.Player;
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
		switch (child) {

		// ice blitz
		case 67:
			cycleDoMagicSpell(Spell.ICE_BLITZ);
			break;

		// ice barrage
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
		// if (player.timers().has(TimerKey.COMBAT_ATTACK)) {
		player.world().getEventHandler().addEvent(player, new Event() {
			// private int tick = 0;
			
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
			// moveCloser();
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

			if (spell.getGfx() == 369)
				target.freeze(33); // 20 second freeze timer
			else if (spell.getGfx() == 367)
				target.freeze(25); // 15 second
			/*
			 * else if (gfx == 377 || gfx == 373 || gfx == 376 || gfx == 375)
			 * player.heal(hit / 4) // Heal for 25% with blood barrage else if
			 * ((gfx == 379 || gfx == 382) && target.isPlayer())
			 * target.skills().alterSkill(Skills.ATTACK,
			 * -(target.skills().level(Skills.ATTACK) * 0.1).toInt(), false)
			 * else if (gfx == 361) target.freeze(8) else if (gfx == 363)
			 * target.freeze(16) else if ((gfx == 381 || gfx == 383) &&
			 * target.isPlayer()) target.skills().alterSkill(Skills.ATTACK,
			 * -(target.skills().level(Skills.ATTACK) * 0.15).toInt(), false)
			 * else if (gfx == 367) { target.freeze(25) //15 second freeze timer
			 * player.graphic(366) }
			 */
		} else {
			target.hit(player, 0, delay).graphic(new Graphic(85, 92, 0));
		}
		container.stop();
		return true;
	}
}
