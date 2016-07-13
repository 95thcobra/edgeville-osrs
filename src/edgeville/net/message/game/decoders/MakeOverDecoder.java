package edgeville.net.message.game.decoders;

import edgeville.aquickaccess.actions.ButtonClickAction;
import edgeville.io.RSBuffer;
import edgeville.model.AttributeKey;
import edgeville.model.entity.Player;
import edgeville.net.message.game.encoders.Action;
import edgeville.net.message.game.encoders.PacketInfo;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author Simon on 5-2-2015.
 */
@PacketInfo(size = 18)
public class MakeOverDecoder implements Action {

	private int gender;
	private int[] clothes;
	private int[] colors;

	@Override
	public void decode(RSBuffer buf, ChannelHandlerContext ctx, int opcode, int size) {
		gender = buf.readByte();

		clothes = new int[7];
		colors = new int[5];

		for (int i = 0; i < 7; i++) {
			clothes[i] = buf.readByte();
		}

		for (int i = 0; i < 5; i++) {
			colors[i] = buf.readByte();
		}
	}

	@Override
	public void process(Player player) {
		player.looks().setGender(gender);
		player.looks().setClothes(clothes);
		player.looks().setColors(colors);
		player.looks().update();
		player.interfaces().closeMain();
	}
}