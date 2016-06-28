package nl.bartpelle.veteres.net.message.game.action;

import io.netty.channel.ChannelHandlerContext;
import nl.bartpelle.veteres.fs.ObjectDefinition;
import nl.bartpelle.veteres.io.RSBuffer;
import nl.bartpelle.veteres.model.AttributeKey;
import nl.bartpelle.veteres.model.entity.Player;
import nl.bartpelle.veteres.net.message.game.Action;
import nl.bartpelle.veteres.net.message.game.PacketInfo;

import java.util.Arrays;

/**
 * Created by Bart Pelle on 8/23/2014.
 */
@PacketInfo(size = 2)
public class ExamineObject implements Action {

	private int id;

	@Override public void process(Player player) {
		if ((boolean) player.attrib(AttributeKey.DEBUG))
			player.message("%s, (%d) %s", player.world().examineRepository().object(id), id,
					Arrays.toString(player.world().definitions().get(ObjectDefinition.class, id).models));
		else
			player.message(player.world().examineRepository().object(id));
	}

	@Override public void decode(RSBuffer buf, ChannelHandlerContext ctx, int opcode, int size) {
		id = buf.readULEShort();
	}

}
