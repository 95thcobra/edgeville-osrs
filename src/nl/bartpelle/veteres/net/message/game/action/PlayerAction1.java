package nl.bartpelle.veteres.net.message.game.action;

import io.netty.channel.ChannelHandlerContext;
import nl.bartpelle.veteres.aquickaccess.combat.PvPCombat;
import nl.bartpelle.veteres.io.RSBuffer;
import nl.bartpelle.veteres.model.AttributeKey;
import nl.bartpelle.veteres.model.entity.Player;
import nl.bartpelle.veteres.net.message.game.Action;
import nl.bartpelle.veteres.net.message.game.PacketInfo;

/**
 * Created by Bart on 8/12/2015.
 */
@PacketInfo(size = 3)
public class PlayerAction1 implements Action {

    private boolean run;
    private int index;

    @Override
    public void decode(RSBuffer buf, ChannelHandlerContext ctx, int opcode, int size) {
        run = buf.readByteN() == 1;
        index = buf.readULEShort();
    }

    @Override
    public void process(Player player) {
        player.stopActions(true);

        Player other = player.world().players().get(index);
        if (other == null) {
            player.message("Unable to find player.");
        } else {
            if (!player.locked() && !player.dead() && !other.dead()) {
                player.face(other);

                if (player.inWilderness() && !other.inWilderness()) {
                    player.message("Your target is not in the wilderness.");
                    return;
                } else if (other.inWilderness() && !player.inWilderness()) {
                    player.message("You are not in the wilderness.");
                    return;
                }

                player.putattrib(AttributeKey.TARGET_TYPE, 0);
                player.putattrib(AttributeKey.TARGET, /*index*/other);

              // player.world().server().scriptExecutor().executeScript(player, PlayerCombat.script);
                new PvPCombat(player, other).start();
               // new CombatBuilder().start();
            }
        }
    }
}
