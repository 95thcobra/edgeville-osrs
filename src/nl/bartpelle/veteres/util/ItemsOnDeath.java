package nl.bartpelle.veteres.util;

import nl.bartpelle.veteres.fs.ItemDefinition;
import nl.bartpelle.veteres.model.GroundItem;
import nl.bartpelle.veteres.model.entity.Player;
import nl.bartpelle.veteres.model.item.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by Carl on 2015-08-23.
 */
public class ItemsOnDeath {

	public static void dropItems(Player killer, Player target) {

		ArrayList<Item> items = new ArrayList<>();
		ArrayList<Item> keptItems = new ArrayList<>();

		Collections.addAll(items, target.inventory().copy());
		Collections.addAll(items, target.equipment().copy());

		Collections.sort(items, (o1, o2) -> {
			if (o1 == null || o2 == null)
				return 0;

			ItemDefinition def = o1.definition(killer.world());
			ItemDefinition def2 = o2.definition(killer.world());
			if (def == null || def2 == null)
				return -1;
			if (def.cost * o1.amount() < def2.cost * o2.amount())
				return 1;
			return -1;
		});

		target.inventory().empty();
		target.equipment().empty();

		for (int i = 0; i < (items.size() < 3 ? items.size() : 3); i++) {
			if (items.get(i) != null) {
				keptItems.add(items.get(i));
				target.inventory().add(items.get(i), true);
			}
		}

		killer.message("SIZE:"+items.size());

		items.stream().filter(i -> i != null && !keptItems.contains(i)).forEach(i -> {
			killer.world().spawnGroundItem(new GroundItem(i, target.tile(), killer));
			killer.message("ITEM"+i.id());
		});

		killer.world().spawnGroundItem(new GroundItem(new Item(526), target.tile(), killer));
	}

}
