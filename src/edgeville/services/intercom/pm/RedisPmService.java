package edgeville.services.intercom.pm;

import com.google.gson.Gson;
import com.typesafe.config.Config;

import edgeville.GameServer;
import edgeville.model.entity.Player;
import edgeville.services.intercom.PmService;
import edgeville.services.redis.RedisService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.JedisPubSub;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Simon on 12-3-2015.
 *
 * Provides inter-world private messaging support.
 */
public class RedisPmService extends JedisPubSub implements PmService {

	private static final Logger logger = LogManager.getLogger();

	private static final String CHANNEL_LOGIN = "v_userlogin";
	private static final String CHANNEL_LOGOUT = "v_userlogout";
	private static final String CHANNEL_PM = "v_pm";

	private GameServer server;
	private RedisService redisService;
	private Gson gson;

	@Override
	public void setup(GameServer server/*, Config serviceConfig*/) {
		this.gson = new Gson();
		this.server = server;
	}

	@Override
	public void onMessage(String channel, String message) {
		switch (channel) {
			case CHANNEL_PM:
				Optional<PrivateMessage> pm = safeFromJson(message, PrivateMessage.class);
				if (pm.isPresent()) {
					logger.info("Private message event: {}.", message);
				}
				break;
		}
	}

	@Override
	public void onUserOnline(Player player) {
		// TODO
	}

	@Override
	public void onUserOffline(Player player) {
		// TODO
	}

	@Override
	public void privateMessageDispatched(Player from, String target, String message) {
		redisService.doOnPool(j -> j.publish(CHANNEL_PM, gson.toJson(new PrivateMessage(from.name(), target, message))));
	}

	private <T> Optional<T> safeFromJson(String json, Class<T> typeOf) {
		try {
			return Optional.of(gson.fromJson(json, typeOf));
		} catch (Throwable t) {
			return Optional.empty();
		}
	}

	@Override
	public boolean start() {
		Optional<RedisService> service = server.service(RedisService.class, false);
		if (!service.isPresent()) {
			logger.error("Cannot use Redis PM service if the Redis service is disabled");
			return false;
		}

		redisService = service.get();

		if (redisService.isAlive()) {
			ExecutorService executor = Executors.newSingleThreadExecutor();
			executor.submit(() -> redisService.doOnPool(j -> j.subscribe(this, CHANNEL_LOGIN, CHANNEL_LOGOUT, CHANNEL_PM)));

			logger.info("Redis PM provider ready to dispatch and receive messages.");
		} else {
			logger.error("Cannot use Redis PM service if the Redis service failed to start");
			return false;
		}

		return true;
	}

	@Override
	public boolean stop() {
		return true;
	}

	@Override
	public boolean isAlive() {
		return true;
	}

	// Gson classes
	private static class PrivateMessage {
		public String from;
		public String to;
		public String message;
		public PrivateMessage(String from, String to, String message) {
			this.from = from;
			this.to = to;
			this.message = message;
		}
	}

}
