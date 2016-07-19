package edgeville.net.message.game.decoders;

import io.netty.channel.ChannelHandlerContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edgeville.aquickaccess.actions.ItemOnItemAction;
import edgeville.io.RSBuffer;
import edgeville.model.entity.Player;
import edgeville.net.message.game.encoders.Action;
import edgeville.net.message.game.encoders.PacketInfo;

/**
 * @author Simon
 * @date November 01, 2015
 */
@PacketInfo(size = 16)
public class ItemOnItem implements Action {

    private static final Logger logger = LogManager.getLogger(ItemOnItem.class);

    private int hash1;
    private int fromSlot;
    private int toSlot;
    private int itemUsedWithId;
    private int itemUsedId;
    private int hash2;

    @Override
    public void decode(RSBuffer buf, ChannelHandlerContext ctx, int opcode, int size) {
        hash1 = buf.readLEInt(); // 4
        fromSlot = buf.readULEShortA(); // 2
        toSlot = buf.readUShortA(); // 2
        itemUsedWithId = buf.readULEShortA(); // 2
        itemUsedId = buf.readUShortA(); // 2
        hash2 = buf.readLEInt(); // 4

        int interfaceId = hash1 >> 16;
        int interfaceId2 = hash2 >> 16;
        int componentId = hash2 & 0xFFFF;
    }

    @Override
    public void process(Player player) {
        logger.info("[ItemOnItem]: interfaceId:{} itemUsedId:{} itemUsedWithId{}", (hash1 >> 16), itemUsedId, itemUsedWithId);
        int interfaceId = (hash1 >> 16);
        if (player.getInventory().has(itemUsedId) && player.getInventory().has(itemUsedWithId)) {
            //player.world().server().scriptRepository().triggerItemOnItem(player, itemUsedId, itemUsedWithId);
        	//TODO item on item
        	
        	new ItemOnItemAction(player, interfaceId, fromSlot, itemUsedId, toSlot, itemUsedWithId).start();
        }
    }
}