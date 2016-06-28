package nl.bartpelle.veteres.net.message.game.action;

import io.netty.channel.ChannelHandlerContext;
import nl.bartpelle.veteres.event.Event;
import nl.bartpelle.veteres.event.EventContainer;
import nl.bartpelle.veteres.io.RSBuffer;
import nl.bartpelle.veteres.model.GroundItem;
import nl.bartpelle.veteres.model.entity.PathQueue;
import nl.bartpelle.veteres.model.entity.Player;
import nl.bartpelle.veteres.net.message.game.Action;
import nl.bartpelle.veteres.net.message.game.PacketInfo;

/**
 * Created by Bart on 8/23/2015.
 */
@PacketInfo(size = 7)
public class GroundItemAction3 implements Action {

	private int x;
	private int z;
	private int id;
	private boolean run;

	@Override
	public void decode(RSBuffer buf, ChannelHandlerContext ctx, int opcode, int size) {
		z = buf.readUShort();
		run = buf.readByteS() == 1;
		id = buf.readUShort();
		x = buf.readUShort();
	}

	@Override
	public void process(Player player) {
		GroundItem item = player.world().getGroundItem(x, z, player.tile().level, id);

		if (!player.locked() && !player.dead()) {
			//player.putattrib(AttributeKey.GROUNDITEM_TARGET, item);
			//player.world().server().scriptExecutor().executeScript(player, GroundItemTaking.script);
			takeGroundItem(player, item);
		}
	}

	private void takeGroundItem(Player player, GroundItem item) {
		if (item == null) {
			return;
		}

		player.walkTo(item.tile().x, item.tile().z, PathQueue.StepType.REGULAR);
		player.world().getEventHandler().addEvent(player, new Event() {
			@Override
			public void execute(EventContainer container) {
				if (item.tile().equals(player.tile())) {
					if (player.world().groundItemValid(item) && (player.inventory().add(item.item(), false).success())) {
						player.world().removeGroundItem(item);
						player.sound(2582, 0);
					}
				}
			}
		});
	}

}
