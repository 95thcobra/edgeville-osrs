package nl.bartpelle.veteres.net.message.game.action;

import io.netty.channel.ChannelHandlerContext;
import nl.bartpelle.skript.WaitReason;
import nl.bartpelle.veteres.aquickaccess.dialogue.DialogueAction;
import nl.bartpelle.veteres.io.RSBuffer;
import nl.bartpelle.veteres.model.AttributeKey;
import nl.bartpelle.veteres.model.entity.Player;
import nl.bartpelle.veteres.model.entity.player.Privilege;
import nl.bartpelle.veteres.net.message.game.Action;
import nl.bartpelle.veteres.net.message.game.PacketInfo;

/**
 * Created by Bart on 5-2-2015.
 */
@PacketInfo(size = 6)
public class DialogueContinue implements Action {

	private int hash;
	private int slot;

	@Override
	public void decode(RSBuffer buf, ChannelHandlerContext ctx, int opcode, int size) {
		hash = buf.readLEInt();
		slot = buf.readULEShortA();

		if (slot == 0xFFFF)
			slot = -1;
	}

	@Override
	public void process(Player player) {
		if (player.privilege().eligibleTo(Privilege.ADMIN) && player.<Boolean>attrib(AttributeKey.DEBUG, false))
			player.message("Dialogue [%d:%d], slot: %d", hash>>16, hash&0xFFFF, slot);

		int id = hash >>16;
		int child = hash & 0xFFFF;

		//Object returnval = null;
		int returnval = -1;
		if (id == 219) {
			returnval = slot;
		}


	player.message("Dialogue action: id:"+id+" child:"+child + " returnval:"+returnval);
		new DialogueAction(player, returnval).handleDialog();
		//player.world().server().scriptExecutor().continueFor(player, WaitReason.DIALOGUE, returnval);
	}
}
