package nl.bartpelle.veteres.net.message.game;

import nl.bartpelle.veteres.io.RSBuffer;
import nl.bartpelle.veteres.model.entity.Player;
import nl.bartpelle.veteres.util.SettingsBuilder;

/**
 * Created by Bart on 8/11/2015.
 */
public class InterfaceSettings implements Command {

	private int hash;
	private int from;
	private int to;
	private int setting;

	private String settingStr = "";

	public InterfaceSettings(int target, int targetChild, int from, int to, SettingsBuilder setting) {
		this(target, targetChild, from, to, setting.build());
	}

	public InterfaceSettings(int target, int targetChild, int from, int to, int setting) {
		settingStr = "Interfacesettings sent: target:"+target+" targetchild:"+targetChild+" from:"+from+" to:"+to+" setting:"+setting;
		hash = (target << 16) | targetChild;
		this.from = from;
		this.to = to;
		this.setting = setting;
	}

	@Override
	public RSBuffer encode(Player player) {
		player.message(settingStr);

		RSBuffer buffer = new RSBuffer(player.channel().alloc().buffer(13));
		buffer.packet(159);

		buffer.writeLEShort(to);
		buffer.writeIntV1(hash);
		buffer.writeLEInt(setting);
		buffer.writeShort(from);

		return buffer;
	}

}
