package edgeville.util;

import static edgeville.handlers.InputHelper.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import edgeville.aquickaccess.dialogue.DialogueHandler;
import edgeville.aquickaccess.events.UpdateGameEvent;
import edgeville.event.Event;
import edgeville.event.EventContainer;
import edgeville.fs.ItemDefinition;
import edgeville.model.*;
import edgeville.model.entity.Npc;
import edgeville.model.entity.Player;
import edgeville.model.entity.player.Privilege;
import edgeville.model.entity.player.Skills;
import edgeville.model.item.Item;
import edgeville.net.message.game.*;

/**
 * @author Simon Pelle on 8/23/2014.
 */
public final class GameCommands {

	/**
	 * Map containing the registered commands.
	 */
	private static Map<String, Command> commands = setup();

	private GameCommands() {

	}

	private static Map<String, Command> setup() {
		commands = new HashMap<>();

		put(Privilege.PLAYER, "invokescript", (p, args) -> p.write(new InvokeScript(Integer.parseInt(args[0]), (Object[]) Arrays.copyOfRange(args, 1, args.length))));
		/*
		 * put(Privilege.PLAYER, "invoke", (p, args) -> { p.write(new
		 * InvokeScript(108, new Object[] { "Enter Amount:" })); });
		 * put(Privilege.PLAYER, "invoketest", (p, args) -> { p.write(new
		 * InvokeScript(532, new Object[] { "toggleroof" })); });
		 */
		put(Privilege.ADMIN, "debugon", (p, args) -> p.setDebug(true));
		put(Privilege.ADMIN, "debugoff", (p, args) -> p.setDebug(false));

		put(Privilege.ADMIN, "update", (p, args) -> {
			int ticks = Integer.parseInt(args[0]);
			p.write(new UpdateGame(ticks));
			p.world().getEventHandler().addEvent(p, false, new UpdateGameEvent(p, ticks));
		});

		put(Privilege.ADMIN, "getvarp", (p, args) -> {
			p.message("" + p.varps().getVarp(Integer.parseInt(args[0])));
		});

		put(Privilege.ADMIN, "skull", (p, args) -> {
			p.setSkullHeadIcon(Integer.parseInt(args[0]));
		});
		put(Privilege.ADMIN, "prayer", (p, args) -> {
			p.setPrayerHeadIcon(Integer.parseInt(args[0]));
		});

		put(Privilege.ADMIN, "loopskulls", (p, args) -> {
			new Thread(() -> {
				for (int i = 0; i < 8; i++) {
					p.setSkullHeadIcon(i);
					try {
						Thread.sleep(300);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
		});

		put(Privilege.ADMIN, "loopvarbit", (p, args) -> {
			new Thread(() -> {
				for (int i = 0; i < 10000; i++) {
					p.varps().setVarbit(i, Integer.parseInt(args[0]));
				}
			}).start();
		});

		put(Privilege.ADMIN, "loopvarp", (p, args) -> {
			new Thread(() -> {
				for (int i = 0; i < 2001; i++) {
					p.varps().setVarp(i, Integer.parseInt(args[0]));
				}
			}).start();
		});

		put(Privilege.ADMIN, "lv", (p, args) -> {
			new Thread(() -> {
				for (int i = Integer.parseInt(args[0]); i < Integer.parseInt(args[1]); i++) {
					p.varps().setVarp(i, Integer.parseInt(args[2]));
				}
			}).start();
		});

		put(Privilege.ADMIN, "lvb", (p, args) -> {
			new Thread(() -> {
				for (int i = Integer.parseInt(args[0]); i < Integer.parseInt(args[1]); i++) {
					p.varps().setVarbit(i, Integer.parseInt(args[2]));
				}
			}).start();
		});

		put(Privilege.ADMIN, "lvv", (p, args) -> {
			new Thread(() -> {
				for (int i = 0; i < 5000; i++) {
					p.varps().setVarp(Integer.parseInt(args[0]), i);
				}
			}).start();
		});

		put(Privilege.ADMIN, "lvbv", (p, args) -> {
			new Thread(() -> {
				for (int i = 0; i < 5000; i++) {
					p.varps().setVarbit(Integer.parseInt(args[0]), i);
				}
			}).start();
		});

		put(Privilege.ADMIN, "loopvp", (p, args) -> {
			new Thread(() -> {
				int varbit = 0;
				while (varbit++ < 5000) {
					// p.varps().varbit(varbit, 1);
					p.varps().setVarp(varbit, 1);
					// p.interfaces().setting(271, varbit, 1, 3, new
					// SettingsBuilder().option(0));

					p.message("varb:" + varbit);
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}).start();
		});

		put(Privilege.PLAYER, "title", (p, args) -> {

			new Thread(() -> {

				// for (int i = 10; i < 16; i++) {
				p.interfaces().send(84, 161, 11, false);
				try {
					Thread.sleep(300);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// }

			}).start();
		});

		/* Player commands */
		put(Privilege.PLAYER, "players", (p, args) -> {
			int size = p.world().players().size();
			p.message("There %s %d player%s online.", size == 1 ? "is" : "are", size, size == 1 ? "" : "s");
		});

		put(Privilege.PLAYER, "reload", (p, args) -> commands = setup());
		put(Privilege.PLAYER, "refreshlooks", (p, args) -> p.looks().update());
		put(Privilege.ADMIN, "logout", (p, args) -> p.logout());
		put(Privilege.PLAYER, "coords", (p, args) -> p.message("Your coordinates are [%d, %d]. Region %d.", p.getTile().x, p.getTile().z, p.getTile().region()));
		put(Privilege.PLAYER, "tele", (p, args) -> {
			if (args[0].contains(",")) { // Ctrl-shift click
				String[] params = args[0].split(",");
				int level = Integer.parseInt(params[0]);
				int rx = Integer.parseInt(params[1]);
				int rz = Integer.parseInt(params[2]);
				int lx = Integer.parseInt(params[3]);
				int lz = Integer.parseInt(params[4]);
				p.move(rx * 64 + lx, rz * 64 + lz, level);
			} else {
				p.move(Integer.parseInt(args[0]), Integer.parseInt(args[1]), args.length > 2 ? Integer.parseInt(args[2]) : 0);
			}
		});
		put(Privilege.PLAYER, "anim", (p, args) -> p.animate(Integer.parseInt(args[0])));
		put(Privilege.PLAYER, "gfx", (p, args) -> p.graphic(Integer.parseInt(args[0])));
		put(Privilege.PLAYER, "yell", (p, args) -> p.world().players().forEach(p2 -> p2.message("[%s] %s", p.name(), glue(args))));
		put(Privilege.PLAYER, "up", (p, args) -> p.move(p.getTile().x, p.getTile().z, Math.min(3, p.getTile().level + 1)));
		put(Privilege.PLAYER, "down", (p, args) -> p.move(p.getTile().x, p.getTile().z, Math.max(0, p.getTile().level - 1)));
		/*
		 * put(Privilege.PLAYER, "scripts", (p, args) -> { new Thread(() -> {
		 * long l = System.currentTimeMillis();
		 * p.world().server().scriptRepository().load(); p.message(
		 * "Took %d to reload scripts.", System.currentTimeMillis() - l);
		 * }).start(); });
		 */
		put(Privilege.ADMIN, "clipinfo", (p, args) -> p.message("Current clip: %s", Arrays.deepToString(p.world().clipSquare(p.getTile(), 5))));
		put(Privilege.ADMIN, "interface", (p, args) -> p.interfaces().sendMain(Integer.parseInt(args[0]), false));

		put(Privilege.ADMIN, "cinterface", (p, args) -> {
			p.interfaces().send(Integer.parseInt(args[0]), 162, 546, false);
		});

		put(Privilege.ADMIN, "loopinter", (p, args) -> {
			new Thread(() -> {
				int interfaceId = 0;
				while (interfaceId++ < 594) {
					p.interfaces().sendMain(interfaceId, false);
					p.message("Interface: " + interfaceId);
					System.out.println("Interface: " + interfaceId);
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}).start();
		});

		put(Privilege.ADMIN, "testdia", (p, args) -> {
			new DialogueHandler().sendOptionDialogue(p, "Where would you like to teleport to?", "Edgeville", "Varrock", "Falador");
		});

		put(Privilege.ADMIN, "xpdrop", (p, args) -> {
			p.varps().setVarbit(Varbit.XP_DROPS_COUNTER, 2);
			p.message("xpdrop off.");
		});

		put(Privilege.ADMIN, "interdia", (p, args) -> {
			int interfaceId = 140;
			int positionId = 1;
			final int parentInterfaceId = 162;
			for (int i = 0; i < 7; i++) {
				p.write(new InterfaceText(interfaceId, i, "Test"));
			}
			p.interfaces().send(interfaceId, 162, 546, false); // chatbox
		});

		put(Privilege.ADMIN, "loopinterpos", (p, args) -> {
			new Thread(() -> {
				int interfaceId = 72;
				int positionId = 76;
				while (positionId++ < 200) {
					p.interfaces().send(interfaceId, 548, positionId, true); // chatbox
					System.out.println("pos id: " + positionId);
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}).start();
		});

		put(Privilege.ADMIN, "stringtest", (p, args) -> {
			int interfaceId = Integer.parseInt(args[0]);
			int strings = Integer.parseInt(args[1]);

			p.interfaces().sendMain(interfaceId, false);

			for (int index = 0; index < strings; index++)
				p.write(new InterfaceText(interfaceId, index, "" + index));
		});

		put(Privilege.ADMIN, "dumpstats", (p, args) -> {
			for (Item equipment : p.getEquipment().copy()) {
				if (equipment == null)
					continue;
				p.message("Name: " + equipment.definition(p.world()).name + ", ID: " + equipment.id());
			}
		});

		/*
		 * put(Privilege.PLAYER, "wtest", (p, args) -> {
		 * p.privilege(Privilege.ADMIN); p.putAttribute(AttributeKey.DEBUG,
		 * true); p.message("Current privileges: " + p.getPrivilege()); });
		 */
		put(Privilege.ADMIN, "rootwindow", (p, args) -> p.interfaces().sendRoot(Integer.parseInt(args[0])));
		put(Privilege.ADMIN, "close", (p, args) -> p.interfaces().close(p.interfaces().activeRoot(), p.interfaces().mainComponent()));
		put(Privilege.ADMIN, "lastchild", (p, args) -> p.message("Last child of %s is %d.", args[0], p.world().server().store().getIndex(3).getDescriptor().getLastFileId(Integer.parseInt(args[0]))));
		put(Privilege.ADMIN, "music", (p, args) -> p.write(new PlayMusic(Integer.parseInt(args[0]))));
		put(Privilege.ADMIN, "itemconfig", (p, args) -> p.message("Item %s has params %d", args[0], p.world().definitions().get(ItemDefinition.class, Integer.parseInt(args[0])).noteModel));
		put(Privilege.PLAYER, "sell", (p, args) -> {
			int itemId = Integer.parseInt(args[0]);

			int value = PkpSystem.getCost(itemId) / 2;

			if (value < 1) {
				p.message("You cannot sell this item as it has no pkp value.");
				return;
			}

			p.getInventory().remove(new Item(itemId), true);
			p.putAttribute(AttributeKey.PK_POINTS, (int) p.attribute(AttributeKey.PK_POINTS, 0) + value);
			p.message("You have sold the " + new Item(itemId).definition(p.world()).name + " for " + value + " points. You now have a total of " + p.attribute(AttributeKey.PK_POINTS, 0) + " points.");

		});
		put(Privilege.PLAYER, "item", (p, args) -> {

			if (p.getPrivilege() != Privilege.ADMIN && p.getTile().z > 3520 && p.getTile().z < 3972) {
				p.message("You cannot spawn items while standing in the wilderness.");
				return;
			}

			int itemId = Integer.parseInt(args[0]);
			int amount = args.length > 1 ? Integer.parseInt(args[1]) : 1;
			Item item = new Item(itemId, amount);

			/*
			 * int pkp = PkpSystem.getCost(itemId);
			 * 
			 * if (item.definition(p.world()).unnotedID > -1) { pkp =
			 * Math.max(PkpSystem.getCost(item.definition(p.world()).unnotedID),
			 * pkp); item = new Item(item.definition(p.world()).unnotedID); }
			 * 
			 * if (pkp > -1) { amount = 1;
			 * 
			 * if (pkp > (int) p.attrib(AttributeKey.PK_POINTS, 0)) { p.message(
			 * "You don't have enough PK points to purchase the " +
			 * item.definition(p.world()).name + ". You have " +
			 * p.attrib(AttributeKey.PK_POINTS, 0) +
			 * " points and the item costs " + pkp + " points."); return; } else
			 * { p.putattrib(AttributeKey.PK_POINTS, (int)
			 * p.attrib(AttributeKey.PK_POINTS, 0) - pkp); p.message(
			 * "You have purchased the " + item.definition(p.world()).name +
			 * " for " + pkp + " points, you now have " +
			 * p.attrib(AttributeKey.PK_POINTS, 0) + " points left."); } }
			 */

			p.getInventory().add(new Item(itemId, amount), true);
		});
		put(Privilege.ADMIN, "varp", (p, args) -> p.varps().setVarp(Integer.parseInt(args[0]), Integer.parseInt(args[1])));
		put(Privilege.ADMIN, "varbit", (p, args) -> p.varps().setVarbit(Integer.parseInt(args[0]), Integer.parseInt(args[1])));
		put(Privilege.ADMIN, "give", (p, args) -> {

			if (p.getPrivilege() != Privilege.ADMIN && p.getTile().z > 3520 && p.getTile().z < 3972) {
				p.message("You cannot spawn items while standing in the wilderness.");
				return;
			}

			String name = args[0];
			for (int i = 0; i < 15000; i++) {
				ItemDefinition def = p.world().definitions().get(ItemDefinition.class, i);
				if (def != null) {
					String n = def.name;
					n = n.replaceAll(" ", "_");
					n = n.replaceAll("\\(", "");
					n = n.replaceAll("\\)", "");
					if (n.equalsIgnoreCase(name)) {
						p.getInventory().add(new Item(i, args.length > 1 ? Integer.parseInt(args[1]) : 1), true);
						break;
					}
				}
			}
		});
		put(Privilege.ADMIN, "gc", (p, args) -> System.gc());
		put(Privilege.ADMIN, "npc", (p, args) -> {
			p.world().registerNpc(new Npc(Integer.parseInt(args[0]), p.world(), p.getTile(), false));
		});
		put(Privilege.ADMIN, "musicbyname", (p, args) -> {
			String name = glue(args).toLowerCase();
			int id = p.world().server().store().getIndex(6).getContainerByName(name).getId();
			p.message("%s resolves to %d.", name, id);
			p.write(new PlayMusic(id));
		});

		put(Privilege.PLAYER, "master", (p, args) -> {
			if (inWilderness(p)) {
				p.message("You cannot do this while in the wilderness.");
				return;
			}
			for (int i = 0; i < Skills.SKILL_COUNT; i++) {
				p.skills().addXp(i, 13034431);
			}
		});

		put(Privilege.ADMIN, "teleregion", (p, args) -> {
			int rid = Integer.parseInt(args[0]);
			p.move((rid >> 8) * 64 + 32, (rid & 0xFF) * 64 + 32);
		});
		put(Privilege.ADMIN, "addxp", (p, args) -> p.skills().addXp(Integer.valueOf(args[0]), Integer.valueOf(args[1])));
		put(Privilege.ADMIN, "hitme", (p, args) -> p.hit(p, Integer.valueOf(args[0]), Hit.Type.REGULAR));
		put(Privilege.PLAYER, "empty", (p, args) -> p.getInventory().empty());
		put(Privilege.MODERATOR, "teleto", (p, args) -> p.move(p.world().playerByName(glue(args)).get().getTile()));
		put(Privilege.MODERATOR, "teletome", (p, args) -> p.world().playerByName(glue(args)).get().move(p.getTile()));
		put(Privilege.ADMIN, "maxspec", (p, args) -> p.varps().setVarp(Varp.SPECIAL_ENERGY, 1000));
		put(Privilege.ADMIN, "finditem", (p, args) -> {
			String s = glue(args);
			new Thread(() -> {
				int found = 0;

				for (int i = 0; i < 14_000; i++) {
					if (found > 249) {
						p.message("Too many results (> 250). Please narrow down.");
						break;
					}
					ItemDefinition def = p.world().definitions().get(ItemDefinition.class, i);
					if (def != null && def.name.toLowerCase().contains(s)) {
						p.message("Result: " + i + " - " + def.name + " (price: " + def.cost + ")");
						found++;
					}
				}
				p.message("Done searching. Found " + found + " results.");
			}).start();
		});
		put(Privilege.ADMIN, "stress", (p, args) -> {
			p.graphic(123);
			p.animate(123);
			p.hit(p, 1);
			p.hit(p, 1);
			p.face(p);
			p.looks().update();
			p.sync().publicChatMessage(new ChatMessage("Hi", 0, 0));
		});
		put(Privilege.ADMIN, "input", (p, args) -> {
			p.inputHelper().provideAlphaNumerical("Is William Gay?", new AlphaNumericalInput() {
				@Override
				public void execute(Player player, String value) {
					System.out.println("The value is: " + value);
				}
			});
		});
		put(Privilege.PLAYER, "ancients", (p, args) -> p.varps().setVarbit(4070, 1));
		put(Privilege.PLAYER, "modern", (p, args) -> p.varps().setVarbit(4070, 0));
		put(Privilege.PLAYER, "lunar", (p, args) -> p.varps().setVarbit(4070, 2));
		put(Privilege.PLAYER, "gdz", (p, args) -> {
			if (inWilderness(p)) {
				p.message("You cannot do this while in the wilderness.");
				return;
			}
			p.move(3288, 3886);
		});
		put(Privilege.PLAYER, "chins", (p, args) -> {
			if (inWilderness(p)) {
				p.message("You cannot do this while in the wilderness.");
				return;
			}
			p.move(3138, 3784);
		});
		put(Privilege.PLAYER, "44s", (p, args) -> {
			if (inWilderness(p)) {
				p.message("You cannot do this while in the wilderness.");
				return;
			}
			p.move(2978, 3871);
		});
		put(Privilege.PLAYER, "mb", (p, args) -> {
			if (inWilderness(p)) {
				p.message("You cannot do this while in the wilderness.");
				return;
			}
			p.move(2539, 4716);
		});

		put(Privilege.PLAYER, "edge", (p, args) -> {
			if (inWilderness(p)) {
				p.message("You cannot do this while in the wilderness.");
				return;
			}
			p.move(3086, 3491);
		});

		put(Privilege.PLAYER, "commands", (p, args) -> {
			p.message("--------------------Commands--------------------");
			p.message("::ancients - changes to the ancient spellbook.");
			p.message("::lunar - changes to the lunar spellbook.");
			p.message("::modern - changes to the modern spellbook.");
			p.message("::master - sets all your levels to 99.");
			p.message("::empty - clears your inventory.");
			p.message("::item id - spawns an item with the specified id.");
			p.message("::lvl skillid level - sets the specified skill to a level between 1 and 99.");
			p.message("::pkp - see your pk points.");
		});

		put(Privilege.PLAYER, "lvl", (p, args) -> {
			if (inWilderness(p)) {
				p.message("You cannot do this while in the wilderness.");
				return;
			}
			int skill = Integer.parseInt(args[0]);
			int lv = Integer.parseInt(args[1]);
			p.skills().xp()[skill] = Skills.levelToXp(lv);
			p.skills().levels()[skill] = p.skills().xpLevel(skill);
			p.skills().update();
		});

		put(Privilege.ADMIN, "kickall", (p, args) -> {
			p.world().players().forEach(Player::logout);
		});

		put(Privilege.PLAYER, "pkp", (p, args) -> p.message("You currently have " + p.attribute(AttributeKey.PK_POINTS, 0) + " PK points."));

		/*put(Privilege.DEVELOPER, "openbank", (p, args) -> {
			p.getBank().open();

		});*/

		put(Privilege.ADMIN, "sound", (p, args) -> p.write(new PlaySound(Integer.parseInt(args[0]), 0)));
		put(Privilege.ADMIN, "removenpcs", (p, args) -> p.world().npcs().forEach(n -> p.world().npcs().remove(n)));
		put(Privilege.ADMIN, "reloadnpcs", (p, args) -> {
			p.world().npcs().forEach(n -> p.world().npcs().remove(n));
			p.world().loadNpcSpawns();
		});
		put(Privilege.ADMIN, "transmog", (p, args) -> p.looks().transmog(Integer.parseInt(args[0])));
		return commands;
	}

	private static boolean inWilderness(Player player) {
		if (player.getPrivilege().eligibleTo(Privilege.ADMIN))
			return false;
		Tile t = player.getTile();
		return t.x > 2941 && t.x < 3329 && t.z > 3524 && t.z < 3968;
	}

	private static void put(Privilege privilege, String name, BiConsumer<Player, String[]> handler) {
		Command command = new Command();
		command.privilege = privilege;
		command.handler = handler;
		commands.put(name, command);
	}

	private static String glue(String[] args) {
		return Arrays.stream(args).collect(Collectors.joining(" "));
	}

	public static void process(Player player, String command) {
		String[] parameters = new String[0];
		String[] parts = command.split(" ");

		if (parts.length > 1) {
			parameters = new String[parts.length - 1];
			System.arraycopy(parts, 1, parameters, 0, parameters.length);
			command = parts[0];
		}

		int level = player.getPrivilege().ordinal();
		while (level-- >= 0) {
			if (!commands.containsKey(command.toLowerCase())) {
				continue;
			}

			Command c = commands.get(command.toLowerCase());

			/* Verify privilege */
			if (player.getPrivilege().eligibleTo(c.privilege)) {
				c.handler.accept(player, parameters);
				return;
			}
		}

		player.message("Command '%s' does not exist.", command);
	}

	static class Command {
		Privilege privilege;
		BiConsumer<Player, String[]> handler;
	}
}