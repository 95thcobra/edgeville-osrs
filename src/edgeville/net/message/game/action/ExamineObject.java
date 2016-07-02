package edgeville.net.message.game.action;

import io.netty.channel.ChannelHandlerContext;

import java.util.Arrays;

import edgeville.fs.ObjectDefinition;
import edgeville.io.RSBuffer;
import edgeville.model.AttributeKey;
import edgeville.model.entity.Player;
import edgeville.net.message.game.Action;
import edgeville.net.message.game.PacketInfo;

/**
 * @author Simon Pelle on 8/23/2014.
 */
@PacketInfo(size = 2)
public class ExamineObject implements Action {

	private int id;

	@Override
	public void process(Player player) {
		if (player.isDebug())
			player.message("%s, (%d) %s", player.world().examineRepository().object(id), id, Arrays.toString(player.world().definitions().get(ObjectDefinition.class, id).models));
		else
			player.message(player.world().examineRepository().object(id));
	}

	@Override
	public void decode(RSBuffer buf, ChannelHandlerContext ctx, int opcode, int size) {
		id = buf.readULEShort();
	}

}
