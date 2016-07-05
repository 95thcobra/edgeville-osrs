package edgeville.aquickaccess.actions;

import edgeville.combat.Food;
import edgeville.combat.Potions;
import edgeville.event.Event;
import edgeville.event.EventContainer;
import edgeville.model.AttributeKey;
import edgeville.model.entity.Player;
import edgeville.model.entity.Sounds;
import edgeville.model.entity.player.Skills;
import edgeville.model.item.Item;
import edgeville.script.TimerKey;
import edgeville.util.Varbit;

/**
 * Created by Sky on 28-6-2016.
 */
public class ItemOption1 {
	private Player player;
	private int itemId;
	private int slot;

	public ItemOption1(Player player, int itemId, int slot) {
		this.player = player;
		this.itemId = itemId;
		this.slot = slot;
	}

	public void start() {
		if (handleFood()) {
			return;
		}

		if (handlePotions()) {
			return;
		}
	}

	private boolean handlePotions() {
		for (Potions potion : Potions.values()) {
			for (int id : potion.getIds()) {
				if (id == itemId) {
					consumePotion(potion);
					return true;
				}
			}
		}
		return false;
	}

	private void consumePotion(Potions potion) {
		if (player.timers().has(TimerKey.POTION)) {
			return;
		}

		player.timers().register(TimerKey.POTION, 3);
		player.animate(829);
		player.sound(Sounds.DRINKING);
		player.message("You drink the " + potion.getName());
		deductPotionDose(potion);
	}

	private void deductPotionDose(Potions potion) {
		if (potion.isLastDose(itemId)) {
			player.getInventory().remove(new Item(itemId));
			player.getInventory().add(new Item(229));
			player.message("You have finished your potion.");
		} else {

			player.getInventory().remove(itemId);
			player.getInventory().add(potion.getNextDose(itemId));
			player.message("You have " + potion.dosesLeft(itemId) + " doses left.");
		}

		// Special cases
		if (handleSpecialPotion(potion)) {
			return;
		}

		// Default
		double change = potion.getBaseValue() + (player.skills().xpLevel(potion.getSkill()) * potion.getPercentage() / 100.0);
		player.skills().alterSkill(potion.getSkill(), (int) Math.round(change), false);
	}

	private boolean handleSpecialPotion(Potions potion) {
		player.messageDebug("Handling special potion");
		if (potion == Potions.PRAYER_POTION) {

			int prayerRestore = (int) Math.round(Math.floor(7 + (player.skills().level(Skills.PRAYER) / 4)));
			player.skills().alterSkillUnder99(Skills.PRAYER, prayerRestore, true);

		} else if (potion == Potions.SUPER_RESTORE) {

			int prayerRestore = (int) Math.round(Math.floor(7 + (player.skills().level(Skills.PRAYER) / 4)));
			player.skills().alterSkillUnder99(Skills.PRAYER, prayerRestore + 1, true);

			for (int i = 0; i < 2; i++) {
				int restore = 8 + Math.round(player.skills().xpLevel(i) / 4);
				player.skills().alterSkillUnder99(i, restore, true);
			}

		} else {
			return false;
		}
		return true;
	}

	private boolean handleFood() {
		for (Food food : Food.values()) {
			if (food.getId() == itemId) {
				eat(food);
				return true;
			}
		}
		return false;
	}

	private void eat(Food food) {
		if (player.timers().has(TimerKey.FOOD)) {
			return;
		}

		player.getInventory().remove(new Item(food.getId()), true, player.attribute(AttributeKey.ITEM_SLOT, 0));
		if (food.getNextId() != -1) {
			player.getInventory().add(new Item(food.getId()), true);
		}

		player.sound(Sounds.EATING);

		player.timers().register(TimerKey.FOOD, 3);
		player.timers().extendOrRegister(TimerKey.COMBAT_ATTACK, 3);

		player.heal(food.getHeal());
		player.animate(829);

		if (food == Food.BEER) {
			player.message("You drink the beer. You feel dizzy...");
			return;
		}

		player.message("You eat the " + food.toString());

		if (food.getHeal() > 0) {
			player.world().getEventHandler().addEvent(player, 2, false, new Event() {
				@Override
				public void execute(EventContainer container) {
					player.message("It heals some health.");
					container.stop();
				}
			});
		}
	}
}
