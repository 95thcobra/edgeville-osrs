package edgeville.model.entity;

import com.google.common.base.MoreObjects;

import edgeville.aquickaccess.events.PlayerDeathEvent;
import edgeville.aquickaccess.events.TeleportEvent;
import edgeville.crypto.IsaacRand;
import edgeville.event.Event;
import edgeville.event.EventContainer;
import edgeville.handlers.InputHelper;
import edgeville.model.*;
import edgeville.model.entity.player.*;
import edgeville.model.item.Item;
import edgeville.model.item.ItemContainer;
import edgeville.net.future.ClosingChannelFuture;
import edgeville.net.message.game.*;
import edgeville.script.Timer;
import edgeville.script.TimerKey;
import edgeville.services.serializers.PlayerSerializer;
import edgeville.util.StaffData;
import edgeville.util.Varbit;
import edgeville.util.Varp;
import io.netty.channel.Channel;

import org.apache.commons.lang3.text.WordUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Bart Pelle on 8/22/2014.
 */
public class Player extends Entity {

    /**
     * A unique ID to identify a player, even after he or she has disconnected.
     */
    private Object id;

    /**
     * The name that the player had used to log in with (not always the display name!)
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

    /**
     * Our looks (clothes, colours, gender)
     */
    private Looks looks;

    private Interfaces interfaces;

    /**
     * The map which was recently sent to show
     */
    private Tile activeMap;

    /**
     * The ISAAC Random Generator for incoming packets.
     */
    private IsaacRand inrand;

    /**
     * The ISAAC Random Generator for outgoing packets.
     */
    private IsaacRand outrand;

    /**
     * A list of pending actions which are decoded at the next game cycle.
     */
    private ConcurrentLinkedQueue<Action> pendingActions = new ConcurrentLinkedQueue<Action>();

    private ItemContainer inventory;
    private ItemContainer equipment;
    private ItemContainer bank;

    private Varps varps;
    private InputHelper inputHelper;

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
        this.bank = new ItemContainer(world, 800, ItemContainer.Type.FULL_STACKING);
        this.varps = new Varps(this);
        this.inputHelper = new InputHelper(this);

        looks().update();

        ///////sj
        resetSpecialEnergy();
    }

    public void resetSpecialEnergy() {
        varps.setVarp(Varp.SPECIAL_ENERGY, 1000);
    }

    public void toggleSpecialAttack() {
        varps.setVarp(Varp.SPECIAL_ENABLED, isSpecialAttackEnabled() ? 0 : 1);
    }

    public boolean isSpecialAttackEnabled() {
        return varps.varp(Varp.SPECIAL_ENABLED) == 1;
    }

    public int getSpecialEnergyAmount() {
        return varps.varp(Varp.SPECIAL_ENERGY);
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
        //write(new SetPlayerOption(1, true, "Attack"));
        write(new SetPlayerOption(2, false, "Follow"));
        write(new SetPlayerOption(3, false, "Trade with"));

        // Trigger a scripting event
        //world.server().scriptRepository().triggerLogin(this);

        // Execute groovy plugin
        //world.getPluginHandler().execute(this, LoginPlugin.class, new LoginPlugin());

        varps.sync(1055);

        updatePrivileges();

        looks.update();

        // By default debug is on for admins
        putattrib(AttributeKey.DEBUG, privilege == Privilege.ADMIN);

        // Sync varps
        varps.syncNonzero();

        ///////////sj

        // Start energy regenerate timer
        timers().register(TimerKey.SPECIAL_ENERGY_RECHARGE, 50);

        // quest tab
        interfaces().clearQuestInterface();
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
        //  return privilege;
        return Privilege.ADMIN;
    }

    public void privilege(Privilege p) {
        privilege = p;
    }

    public Interfaces interfaces() {
        return interfaces;
    }

    public ItemContainer inventory() {
        return inventory;
    }

    public ItemContainer equipment() {
        return equipment;
    }

    public ItemContainer bank() {
        return bank;
    }

    public Varps varps() {
        return varps;
    }

    public void migration(int m) {
        migration = m;
    }

    public int migration() {
        return migration;
    }

    public InputHelper inputHelper() {
        return inputHelper;
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
    public void hp(int hp, int exceed) {
        skills.levels()[Skills.HITPOINTS] = Math.max(0, Math.min(maxHp() + exceed, hp));
        skills.update(Skills.HITPOINTS);
    }

    @Override
    public PlayerSyncInfo sync() {
        return (PlayerSyncInfo) sync;
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
     * Dispatches a logout message, and hooks a closing future to that. Once it's flushed, the channel is closed.
     * The player is also immediately removed from the player list.
     */
    public void logout() {
        // If we're logged in and the channel is active, begin with sending a logout message and closing the channel.
        // We use writeAndFlush here because otherwise the message won't be flushed cos of the next unregister() call.
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
        if ((boolean) attrib(AttributeKey.LOGOUT, false)) {
            putattrib(AttributeKey.LOGOUT, false);

            // Attempt to log us out. In the future, we'd want to do combat checking and such here.
            logout();
            return;
        }

        // Fire timers
        for (Iterator<Map.Entry<TimerKey, Timer>> it = timers.timers().entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<TimerKey, Timer> entry = it.next();
            if (entry.getValue().ticks() < 1) {
                TimerKey key = entry.getKey();
                it.remove();

                //world.server().scriptRepository().triggerTimer(this, key);
            }
        }

        // Regenerate special energy
        if (!timers().has(TimerKey.SPECIAL_ENERGY_RECHARGE)) {
            int currentEnergy = varps().varp(Varp.SPECIAL_ENERGY);
            varps().setVarp(Varp.SPECIAL_ENERGY, Math.min(1000, currentEnergy + 100));
            timers.register(TimerKey.SPECIAL_ENERGY_RECHARGE, 50);
        }

        // Region enter and leave triggers
        int lastregion = attrib(AttributeKey.LAST_REGION, -1);
        
        //if (lastregion != tile.region()) {
            //world.server().scriptRepository().triggerRegionEnter(this, tile.region());
        	//TODO OPTIONAL: Trigger region enter
        //}
        putattrib(AttributeKey.LAST_REGION, tile.region());

        // Show attack option when player is in wilderness.
        handlePlayerOptions();
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
        if (bank.dirty()) {
            write(new SetItems(95, bank));
            bank.clean();
        }
    }

    public void updateWeaponInterface() {
        Item wep = equipment.get(EquipSlot.WEAPON);
        write(new InterfaceText(593, 1, wep == null ? "Unarmed" : wep.definition(world).name));
        write(new InterfaceText(593, 2, "Combat Lvl: " + skills.combatLevel()));

        // Set the varp that holds our weapon interface panel type
        int panel = wep == null ? 0 : world.equipmentInfo().weaponType(wep.id());
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
        world.getEventHandler().addEvent(this, new PlayerDeathEvent(this));
        //world.server().scriptExecutor().executeScript(this, Death.script);
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
        return MoreObjects.toStringHelper(this).add("id", id).add("username", username)
                .add("displayName", displayName).add("tile", tile).add("privilege", privilege).toString();
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
}
