package edgeville.model.entity;

import com.google.common.base.MoreObjects;

import edgeville.Constants;
import edgeville.Panel;
import edgeville.aquickaccess.events.PlayerDeathEvent;
import edgeville.aquickaccess.events.TeleportEvent;
import edgeville.bank.BankTab;
import edgeville.combat.CombatUtil;
import edgeville.crypto.IsaacRand;
import edgeville.event.Event;
import edgeville.event.EventContainer;
import edgeville.model.*;
import edgeville.model.entity.player.*;
import edgeville.model.entity.player.interfaces.InputDialog;
import edgeville.model.entity.player.interfaces.QuestTab;
import edgeville.model.entity.player.skills.Prayer;
import edgeville.model.item.Item;
import edgeville.model.item.ItemContainer;
import edgeville.model.item.ItemContainer.Type;
import edgeville.net.future.ClosingChannelFuture;
import edgeville.net.message.game.encoders.*;
import edgeville.script.Timer;
import edgeville.script.TimerKey;
import edgeville.services.serializers.PlayerSerializer;
import edgeville.util.CombatStyle;
import edgeville.util.StaffData;
import edgeville.util.TextUtil;
import edgeville.util.Varbit;
import edgeville.util.Varp;
import edgeville.util.TextUtil.Colors;
import io.netty.channel.Channel;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Simon on 8/22/2014.
 */
public class Player extends Entity {

	/**
	 * A unique ID to identify a player, even after he or she has disconnected.
	 */
	private Object id;

	/**
	 * The name that the player had used to log in with (not always the display
	 * name!)
	 */
	private String username;

	private String password;

	/**
	 * The name of the player, actually seen in-game.
	 */
	private String displayName;

	/**
	 * The player's Netty connection channel
	 */
	private Channel channel;

	/**
	 * The privilege level of this player.
	 */
	private Privilege privilege;

	/**
	 * Our achieved skill levels
	 */
	private Skills skills;

	private boolean debug;

	private CombatUtil combatUtil;

	private boolean vengOn;

	public boolean isVengOn() {
		return vengOn;
	}

	public void setVengOn(boolean vengOn) {
		this.vengOn = vengOn;
	}

	public CombatUtil getCombatUtil() {
		return combatUtil;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	/**
	 * Our looks (clothes, colours, gender)
	 */
	private Looks looks;

	private Interfaces interfaces;

	private QuestTab questTab;

	public QuestTab getQuestTab() {
		return questTab;
	}

	private int kills;
	private int deaths;

	public int getKills() {
		return kills;
	}

	public void setKills(int kills) {
		this.kills = kills;
		this.getQuestTab().updateKills();
	}

	public void incrementKills() {
		this.kills += 1;
		this.getQuestTab().updateKills();
	}

	public void incrementDeaths() {
		this.deaths += 1;
		this.getQuestTab().updateDeaths();
	}

	public int getDeaths() {
		return deaths;
	}

	public void setDeaths(int deaths) {
		this.deaths = deaths;
		this.getQuestTab().updateDeaths();
	}

	/**
	 * The map which was recently sent to show
	 */
	private Tile activeMap;

	/**
	 * The ISAAC Random Generator for incoming packets.
	 */
	private IsaacRand inrand;

	private Prayer prayer;

	public Prayer getPrayer() {
		return prayer;
	}

	/**
	 * The ISAAC Random Generator for outgoing packets.
	 */
	private IsaacRand outrand;

	private int skullHeadIcon = -1;
	private int prayerHeadIcon = -1;

	public int getSkullHeadIcon() {
		return skullHeadIcon;
	}

	public void setSkullHeadIcon(int skullHeadIcon) {
		this.skullHeadIcon = skullHeadIcon;
		looks().update();
	}

	public int getPrayerHeadIcon() {
		return prayerHeadIcon;
	}

	public void setPrayerHeadIcon(int prayerHeadIcon) {
		this.prayerHeadIcon = prayerHeadIcon;
		looks().update();
	}

	private Loadout loadout;

	public void setLoadout(Loadout loadout) {
		this.loadout = loadout;
	}

	public Loadout getLoadout() {
		return loadout;
	}

	/**
	 * A list of pending actions which are decoded at the next game cycle.
	 */
	private ConcurrentLinkedQueue<Action> pendingActions = new ConcurrentLinkedQueue<Action>();

	private ItemContainer inventory;
	private ItemContainer equipment;

	private Bank bank;

	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}

	// private void lastButtonClicked

	private Varps varps;
	// private InputHelper inputHelper;

	private InputDialog lastInputDialog;

	private int dialogueAction = -1;

	public int getDialogueAction() {
		return dialogueAction;
	}

	private boolean isXPCounterEnabled = true;

	public boolean isXPCounterEnabled() {
		return isXPCounterEnabled;
	}

	public void setXPCounterEnabled(boolean XPCounterEnabled) {
		isXPCounterEnabled = XPCounterEnabled;
	}

	public void setDialogueAction(int dialogueAction) {
		this.dialogueAction = dialogueAction;
	}

	/**
	 * The ID of the last applied migration.
	 */
	private int migration;

	public Player(Channel channel, String username, String password, World world, Tile tile, IsaacRand inrand, IsaacRand outrand) {
		super(world, tile);

		this.channel = channel;
		this.inrand = inrand;
		this.outrand = outrand;
		this.username = this.displayName = username;
		this.password = password;
		this.privilege = privilege;

		this.sync = new PlayerSyncInfo(this);
		this.skills = new Skills(this);
		this.looks = new Looks(this);
		this.interfaces = new Interfaces(this);
		this.inventory = new ItemContainer(world, 28, ItemContainer.Type.REGULAR);
		this.equipment = new ItemContainer(world, 14, ItemContainer.Type.REGULAR);
		// this.bank = new ItemContainer(world, 800,
		// ItemContainer.Type.FULL_STACKING);
		this.varps = new Varps(this);
		// this.lastInputDialog = new InputDialog(this);

		prayer = new Prayer(this);
		loadout = new Loadout();
		questTab = new QuestTab(this);

		looks().update();

		/////// sj
		debug = false;
		resetSpecialEnergy();
		bank = new Bank(this);
		combatUtil = new CombatUtil(this);
	}

	public void resetSpecialEnergy() {
		varps.setVarp(Varp.SPECIAL_ENERGY, 1000);
	}

	public void toggleSpecialAttack() {
		varps.setVarp(Varp.SPECIAL_ENABLED, isSpecialAttackEnabled() ? 0 : 1);
	}

	public void turnOffSpecialAttack() {
		varps.setVarp(Varp.SPECIAL_ENABLED, 0);
	}

	public boolean isSpecialAttackEnabled() {
		return varps.getVarp(Varp.SPECIAL_ENABLED) == 1;
	}

	public int getSpecialEnergyAmount() {
		return varps.getVarp(Varp.SPECIAL_ENERGY);
	}

	public void setSpecialEnergyAmount(int amount) {
		varps.setVarp(Varp.SPECIAL_ENERGY, Math.min(1000, amount));
	}

	/**
	 * No-args constructor solely for Hibernate.
	 */
	public Player() {
		super(null, null);
	}

	/**
	 * Sends everything required to make the user see the game.
	 */
	public void initiate() {
		skills.update();

		// Send simple player options
		if (Constants.ALL_PVP) {
			write(new SetPlayerOption(1, true, "Attack"));
		}
		write(new SetPlayerOption(2, false, "Follow"));
		write(new SetPlayerOption(3, false, "Trade with"));

		// Trigger a scripting event
		// world.server().scriptRepository().triggerLogin(this);

		// Execute groovy plugin
		// world.getPluginHandler().execute(this, LoginPlugin.class, new
		// LoginPlugin());

		varps.sync(1055);

		updatePrivileges();

		looks.update();

		// By default debug is on for admins
		// putattrib(AttributeKey.DEBUG, false/*privilege == Privilege.ADMIN*/);

		// Sync varps
		varps.syncNonzero();

		/////////// sj

		// Welcome
		message("Welcome to %s.", TextUtil.colorString(Constants.SERVER_NAME, Colors.BLUE));
		message("The server is in development stage.");

		// Start energy regenerate timer
		timers().register(TimerKey.SPECIAL_ENERGY_RECHARGE, 50);

		// Replenish stats timer
		timers().register(TimerKey.STAT_REPLENISH, 100);

		// quest tab
		questTab.prepareQuestTab();

		// new Panel(this);
	}

	public void event(Event event) {
		event(event, 1);
	}

	public void event(Event event, int ticks) {
		world.getEventHandler().addEvent(this, ticks, event);
	}

	public void updatePrivileges() {
		for (StaffData staff : StaffData.values()) {
			if (staff == null)
				continue;
			if (username.equalsIgnoreCase(staff.name()))
				privilege(staff.getPrivilege());
		}
	}

	public String name() {
		return WordUtils.capitalize(displayName);
	}

	public void displayName(String n) {
		displayName = n;
	}

	public String getUsername() {
		return username;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getPassword() {
		return password;
	}

	public void message(String format, Object... params) {
		write(new AddMessage(params.length > 0 ? String.format(format, (Object[]) params) : format));
	}

	@Override
	public void stopActions(boolean cancelMoving) {
		super.stopActions(cancelMoving);

		// Reset main interface
		if (interfaces.visible(interfaces.activeRoot(), interfaces.mainComponent())) {
			interfaces.close(interfaces.activeRoot(), interfaces.mainComponent());
		}

		// Reset chatbox interface
		if (interfaces.visible(162, 546)) {
			interfaces.close(162, 546);
		}
	}

	public void filterableMessage(String format, Object... params) {
		write(new AddMessage(params.length > 0 ? String.format(format, (Object[]) params) : format, AddMessage.Type.GAME_FILTER));
	}

	public void id(Object id) {
		this.id = id;
	}

	public Object id() {
		return id; // Temporary!
	}

	public ConcurrentLinkedQueue<Action> pendingActions() {
		return pendingActions;
	}

	public Looks looks() {
		return looks;
	}

	public Channel channel() {
		return channel;
	}

	public Skills skills() {
		return skills;
	}

	public Tile activeMap() {
		return activeMap;
	}

	public Area activeArea() {
		return new Area(activeMap.x, activeMap.z, activeMap.x + 104, activeMap.z + 104);
	}

	public void activeMap(Tile t) {
		activeMap = t;
	}

	public boolean seesChunk(int x, int z) {
		return activeArea().contains(new Tile(x, z));
	}

	public IsaacRand inrand() {
		return inrand;
	}

	public IsaacRand outrand() {
		return outrand;
	}

	public Privilege getPrivilege() {
		// return privilege;
		return Privilege.ADMIN;
	}

	public void privilege(Privilege p) {
		privilege = p;
	}

	public Interfaces interfaces() {
		return interfaces;
	}

	public ItemContainer getInventory() {
		return inventory;
	}

	public void setInventory(ItemContainer inventory) {
		this.inventory = inventory;
	}

	public ItemContainer getEquipment() {
		return equipment;
	}

	public void setEquipment(ItemContainer equipment) {
		this.equipment = equipment;
	}

	/*
	 * public ItemContainer bank() { return bank; }
	 */

	public Varps varps() {
		return varps;
	}

	public void migration(int m) {
		migration = m;
	}

	public int migration() {
		return migration;
	}

	public InputDialog getLastInputDialog() {
		return lastInputDialog;
	}

	public void setInputDialog(InputDialog inputDialog) {
		this.lastInputDialog = inputDialog;
	}

	@Override
	public int hp() {
		return skills.level(Skills.HITPOINTS);
	}

	@Override
	public int maxHp() {
		return skills.xpLevel(Skills.HITPOINTS);
	}

	@Override
	public void setHp(int hp, int exceed) {
		skills.levels()[Skills.HITPOINTS] = Math.max(0, Math.min(maxHp() + exceed, hp));
		skills.update(Skills.HITPOINTS);
	}

	@Override
	public PlayerSyncInfo sync() {
		return (PlayerSyncInfo) sync;
	}

	public void sound(int id) {
		write(new PlaySound(id, 0));
	}

	public void sound(int id, int delay) {
		write(new PlaySound(id, delay));
	}

	public void sound(int id, int delay, int times) {
		write(new PlaySound(id, delay, times));
	}

	public void invokeScript(int id, Object... args) {
		write(new InvokeScript(id, args));
	}

	public void forceMove(ForceMovement move) {
		Tile t = pathQueue.peekLast() == null ? tile : pathQueue.peekLast().toTile();
		int bx = t.x - activeMap.x;
		int bz = t.z - activeMap.z;
		move.dx1 += bx;
		move.dx2 += bx;
		move.dz1 += bz;
		move.dz2 += bz;
		sync().forceMove(move);
	}

	/**
	 * Unregisters this player from the world it's in.
	 */
	public void unregister() {
		world.unregisterPlayer(this);
		world.server().service(PlayerSerializer.class, true).get().savePlayer(this);
	}

	/**
	 * Dispatches a logout message, and hooks a closing future to that. Once
	 * it's flushed, the channel is closed. The player is also immediately
	 * removed from the player list.
	 */
	public void logout() {
		// If we're logged in and the channel is active, begin with sending a
		// logout message and closing the channel.
		// We use writeAndFlush here because otherwise the message won't be
		// flushed cos of the next unregister() call.
		if (channel.isActive()) {
			channel.writeAndFlush(new Logout()).addListener(new ClosingChannelFuture());
		}

		// Then nicely unregister the player from the game.
		unregister();
	}

	@Override
	public void cycle() {
		super.cycle();

		// Are we requested to be logged out?
		if ((boolean) attribute(AttributeKey.LOGOUT, false)) {
			putAttribute(AttributeKey.LOGOUT, false);

			// Attempt to log us out. In the future, we'd want to do combat
			// checking and such here.
			logout();
			return;
		}

		// Fire timers
		for (Iterator<Map.Entry<TimerKey, Timer>> it = timers.timers().entrySet().iterator(); it.hasNext();) {
			Map.Entry<TimerKey, Timer> entry = it.next();
			if (entry.getValue().ticks() < 1) {
				TimerKey key = entry.getKey();
				it.remove();

				switch (key) {
				case SPECIAL_ENERGY_RECHARGE:
					int currentEnergy = varps().getVarp(Varp.SPECIAL_ENERGY);
					varps().setVarp(Varp.SPECIAL_ENERGY, Math.min(1000, currentEnergy + 100));
					timers.register(TimerKey.SPECIAL_ENERGY_RECHARGE, 50);
					break;
				case SKULL:
					if (getSkullHeadIcon() == Skulls.WHITE_SKUL.getSkullId()) {
						setSkullHeadIcon(-1);
					}
					break;
				case STAT_REPLENISH:
					skills.replenishStats();
					timers.register(TimerKey.STAT_REPLENISH, 100);
					break;

				}
				// world.server().scriptRepository().triggerTimer(this, key);
			}
		}

		// Regenerate special energy
		/*
		 * if (!timers().has(TimerKey.SPECIAL_ENERGY_RECHARGE)) { int
		 * currentEnergy = varps().getVarp(Varp.SPECIAL_ENERGY);
		 * varps().setVarp(Varp.SPECIAL_ENERGY, Math.min(1000, currentEnergy +
		 * 100)); timers.register(TimerKey.SPECIAL_ENERGY_RECHARGE, 50); }
		 */

		// If timer runs out and headicon is white skull then remove.
		/*
		 * if (!timers().has(TimerKey.SKULL) && getSkullHeadIcon() ==
		 * Skulls.WHITE_SKUL.getSkullId()) { setSkullHeadIcon(-1); }
		 */

		// Players online in questtab
		questTab.sendQuestTabTitle();

		// Region enter and leave triggers
		int lastregion = attribute(AttributeKey.LAST_REGION, -1);

		// if (lastregion != tile.region()) {
		// world.server().scriptRepository().triggerRegionEnter(this,
		// tile.region());
		// TODO OPTIONAL: Trigger region enter
		// }
		putAttribute(AttributeKey.LAST_REGION, tile.region());

		// Show attack option when player is in wilderness.
		if (!Constants.ALL_PVP) {
			handlePlayerOptions();
		}
	}

	private void handlePlayerOptions() {
		if (inWilderness()) {
			write(new SetPlayerOption(1, true, "Attack"));
		} else {
			write(new SetPlayerOption(1, true, "Null"));
		}
	}

	public boolean inWilderness() {
		Tile tile = getTile();
		return tile.x > 2941 && tile.x < 3329 && tile.z > 3523 && tile.z < 3968;
	}

	public void precycle() {
		// Sync inventory
		if (inventory.dirty()) {
			write(new SetItems(93, 149, 0, inventory));
			inventory.clean();
		}

		// Sync equipment if dirty
		if (equipment.dirty()) {
			write(new SetItems(94, equipment));
			looks.update();
			equipment.clean();

			// Also send the stuff required to make the weaponry panel proper
			updateWeaponInterface();
		}

		// Sync bank if dirty
		if (bank.getBankItems().dirty()) {
			/*ItemContainer container = new ItemContainer(world, 800, Type.FULL_STACKING);
			for (ItemContainer itemcontainer : bank.bankNewwww.bankTabItems) {
				if (itemcontainer == null)
					continue;
				for  (int i = 0 ; i < itemcontainer.occupiedSlots(); i++){
					Item item = itemcontainer.getItems()[i];
					if (item == null)
						continue;
					container.add(item);
				}
			}
			for(int i = 0 ; i < container.occupiedSlots();i++){
				container.getItems()[i] = new Item(container.getItems()[i].getId(), i);
			}*/
			// container.add(itemId)
			
			 write(new SetItems(95, bank.bankNewwww.getAllItems()));
			 bank.getBankItems().clean();
			
			
			// container.add(itemId)
			// write(new SetItems(95, bank.getBankItems()));
			// bank.getBankItems().clean();
		}
	}

	public boolean drainSpecialEnergy(int amount) {
		turnOffSpecialAttack();
		if (getSpecialEnergyAmount() < amount * 10) {
			message("You do not have enough special energy.");
			return false;
		}
		setSpecialEnergyAmount(getSpecialEnergyAmount() - (amount * 10));
		return true;
	}

	public void updateWeaponInterface() {
		Item wep = equipment.get(EquipSlot.WEAPON);
		write(new InterfaceText(593, 1, wep == null ? "Unarmed" : wep.definition(world).name));
		write(new InterfaceText(593, 2, "Combat Lvl: " + skills.combatLevel()));

		// Set the varp that holds our weapon interface panel type
		int panel = wep == null ? 0 : world.equipmentInfo().weaponType(wep.getId());
		varps.setVarp(843, panel);
	}

	@Override
	public boolean isPlayer() {
		return true;
	}

	@Override
	public boolean isNpc() {
		return false;
	}

	@Override
	protected void die() {
		// lock();
		world.getEventHandler().addEvent(this, false, new PlayerDeathEvent(this));
		// world.server().scriptExecutor().executeScript(this, Death.script);
	}

	public void write(Object... o) {
		if (channel.isActive()) {
			for (Object msg : o) {
				channel.write(msg);
			}
		}
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("id", id).add("username", username).add("displayName", displayName).add("tile", tile).add("privilege", privilege).toString();
	}

	//////////////////

	public void teleportWithAnimation(int x, int y) {
		teleportWithAnimation(x, y, 0);
	}

	public void teleportWithAnimation(int x, int y, int level) {
		teleport(new Tile(x, y));
	}

	public void teleport(Tile tile) {
		if (locked()) {
			return;
		}
		world().getEventHandler().addEvent(this, new TeleportEvent(this, tile));
	}

	@Override
	public int getAttackSound() {
		Item item = getEquipment().get(EquipSlot.WEAPON);
		int soundId = 24;

		if (item == null) {
			return soundId;
		}
		if (StringUtils.containsIgnoreCase(item.definition(world).name, "shortbow") || StringUtils.containsIgnoreCase(item.definition(world).name, "longbow")) {
			soundId = 2693;
		}
		if (StringUtils.containsIgnoreCase(item.definition(world).name, "crossbow")) {
			soundId = 2700;
		}
		if (StringUtils.containsIgnoreCase(item.definition(world).name, "2h")) {
			soundId = 2503;
		}
		if (StringUtils.containsIgnoreCase(item.definition(world).name, "axe")) {
			soundId = 2508;
		}
		if (StringUtils.containsIgnoreCase(item.definition(world).name, "knife")) {
			soundId = 2696;
		}
		if (StringUtils.containsIgnoreCase(item.definition(world).name, "dagger")) {
			soundId = varps().getVarp(Varp.ATTACK_STYLE) == 3 ? 2548 : 2547;
		}
		if (StringUtils.containsIgnoreCase(item.definition(world).name, "staff")) {
			soundId = 2555;
		}
		if (StringUtils.containsIgnoreCase(item.definition(world).name, "staff")) {
			soundId = 2547;
		}

		switch (item.getId()) {
		case 4151:
		case 12006:
			soundId = 2720;
			break;
		}
		return soundId;
	}

	@Override
	public int getBlockSound() {
		Item item = getEquipment().get(EquipSlot.SHIELD);
		int soundId = 791;
		if (item == null) {
			soundId = 23;
		} else {
			switch (item.getId()) {
			case 8850:
				soundId = 15;
				break;
			}
		}
		return soundId;
	}

	public void savePlayer() {
		world().server().service(PlayerSerializer.class, true).get().savePlayer(this);
	}

	@Override
	public int getBlockAnim() {
		int animationId = 424;
		Item shield = ((Player) this).getEquipment().get(EquipSlot.SHIELD);
		Item weapon = ((Player) this).getEquipment().get(EquipSlot.WEAPON);
		if (shield != null) {
			switch (shield.getId()) {
			// shields 1156

			// Defenders
			case 8850:
				animationId = 4177;
				break;
			}
		}

		else if (weapon != null) {
			switch (weapon.getId()) {

			// Gmaul
			case 4153:
				animationId = 1666;
				break;

			}
		}

		return animationId;
	}

	public void messageDebug(String text) {
		if (isDebug()) {
			message(text);
		}
	}
}
