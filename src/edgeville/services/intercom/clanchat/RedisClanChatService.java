package edgeville.services.intercom.clanchat;

import com.google.gson.Gson;
import com.typesafe.config.Config;

import edgeville.GameServer;
import edgeville.model.entity.Player;
import edgeville.net.message.game.AddClanChatMessage;
import edgeville.services.intercom.ClanChatService;
import edgeville.services.intercom.PmService;
import edgeville.services.redis.RedisService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.JedisPubSub;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class RedisClanChatService extends JedisPubSub implements ClanChatService {

	private static final Logger logger = LogManager.getLogger();

	private static final String CHANNEL_CLANCHAT = "friends_chat"; // Comes from RS3

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
			case CHANNEL_CLANCHAT:
				Optional<FriendsChatMessage> pm = safeFromJson(message, FriendsChatMessage.class);
				if (pm.isPresent()) {
					FriendsChatMessage msg = pm.get();
					server.world().players().forEach(p -> p.write(new AddClanChatMessage(msg.username, "Help", msg.icon, 301, msg.message)));
				}
				break;
		}
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
			logger.error("Cannot use Redis Clan Chat service if the Redis service is disabled");
			return false;
		}

		redisService = service.get();

		if (redisService.isAlive()) {
			ExecutorService executor = Executors.newSingleThreadExecutor();
			executor.submit(() -> redisService.doOnPool(j -> j.subscribe(this, CHANNEL_CLANCHAT)));

			logger.info("Redis Clan Chat provider ready to dispatch and receive messages.");
		} else {
			logger.error("Cannot use Redis Clan Chat service if the Redis service failed to start");
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


	public static class FriendsChatMessage {
		public String uid;
		public String chatUID;
		public String message;
		public int icon;
		public String username;
	}

}
