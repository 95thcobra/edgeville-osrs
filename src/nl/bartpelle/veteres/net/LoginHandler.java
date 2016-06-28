package nl.bartpelle.veteres.net;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import nl.bartpelle.veteres.GameServer;
import nl.bartpelle.veteres.aquickaccess.Constants;
import nl.bartpelle.veteres.crypto.IsaacRand;
import nl.bartpelle.veteres.model.Tile;
import nl.bartpelle.veteres.model.entity.Player;
import nl.bartpelle.veteres.net.message.*;
import nl.bartpelle.veteres.services.login.LoginService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by Bart on 8/4/2014.
 */
@ChannelHandler.Sharable
public class LoginHandler extends ChannelInboundHandlerAdapter {

	/**
	 * The logger instance for this class.
	 */
	private static final Logger logger = LogManager.getLogger(LoginHandler.class);

	/**
	 * A reference to the server instance.
	 */
	private GameServer server;

	/**
	 * The spawn tile of new players, as defined in the configuration file.
	 */
	private Tile spawnTile;

	/**
	 * The login service we are going to use when our friends and family log in. If there's none, all requests are
	 * blindly accepted. Easy for development, but gives you no saving.
	 */
	private LoginService loginService;

	public LoginHandler(GameServer server) {
		this.server = server;

		//spawnTile = new Tile(server.config().getInt("game.spawnpos.x"), server.config().getInt("game.spawnpos.z"));
		spawnTile = new Tile(Constants.SPAWN_TILE);

		// Load our service, if any. Null is a service too :-)
		Optional<LoginService> service = server.service(LoginService.class, false);
		loginService = service.orElse(null);

		if (service.isPresent())
			logger.info("Using {} to process logins.", loginService.getClass().getSimpleName());
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		super.channelRead(ctx, msg);

		if (msg instanceof PreloginResponseMessage) {
			PreloginResponseMessage pre = (PreloginResponseMessage) msg;
			ctx.writeAndFlush(HandshakeResponse.ALL_OK);
		} else if (msg instanceof LoginRequestMessage) {
			LoginRequestMessage message = ((LoginRequestMessage) msg);

			if (loginService != null) {
				loginService.enqueue(message);
				return;
			}

			String username = message.username();
			String password = message.password();

			logger.trace("Player logged in: {} (password {}).", username, password);

			// Prepare random gens
			int[] seed = message.isaacSeed();
			IsaacRand inrand = new IsaacRand(seed);
			IsaacRand outrand = new IsaacRand(Arrays.stream(seed).map(i -> i + 50).toArray());

			Player player = new Player(message.channel(), message.username(), server.world(), new Tile(3088, 3505), inrand, outrand);
			player.id(player.username());
			LoginService.complete(player, server, message);
		}
	}

}
