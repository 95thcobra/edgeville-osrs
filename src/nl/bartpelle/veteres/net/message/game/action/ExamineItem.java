package nl.bartpelle.veteres.net.message.game.action;

import io.netty.channel.ChannelHandlerContext;
import nl.bartpelle.veteres.fs.ItemDefinition;
import nl.bartpelle.veteres.io.RSBuffer;
import nl.bartpelle.veteres.model.entity.Player;
import nl.bartpelle.veteres.net.message.game.Action;
import nl.bartpelle.veteres.net.message.game.PacketInfo;
import nl.bartpelle.veteres.util.L10n;

/**
 * Created by Bart Pelle on 8/9/2015.
 */
@PacketInfo(size = 2)
public class ExamineItem implements Action {

	private int id;

	@Override public void process(Player player) {
		player.message(player.world().examineRepository().item(id));
	}

	@Override public void decode(RSBuffer buf, ChannelHandlerContext ctx, int opcode, int size) {
		id = buf.readUShortA();
	}

}
