package nl.bartpelle.veteres.model.uid.providers;

import nl.bartpelle.veteres.GameServer;
import nl.bartpelle.veteres.model.entity.Player;
import nl.bartpelle.veteres.model.uid.UIDProvider;
import nl.bartpelle.veteres.services.redis.RedisService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Bart on 4-3-2015.
 *
 * Unique ID provider through an atomic Redis key, which gets incremented as requested to have one unique ID
 * generator across multiple servers to avoid handing out a UID which is already used.
 */
public class RedisUIDProvider extends UIDProvider {

	private static final Logger logger = LogManager.getLogger();

	/**
	 * The Redis service used to make calls for the UID incrementing calls.
	 */
	private RedisService redisService;

	public RedisUIDProvider(GameServer server) {
		super(server);

		redisService = server.service(RedisService.class, false).orElseThrow(
				() -> new RuntimeException("cannot use Redis UID provider without the Redis service"));

		if (redisService.isAlive()) {
			/* Make an incr command by 0, which will either leave the key as-is or create if missing. */
			Object incr = redisService.doOnPoolReturning(j -> j.incrBy("uid_incr", 0));
			logger.info("Redis UID provider ready to provide UIDs, will provide UIDs from {} onwards.", incr);
		} else
			throw new RuntimeException("cannot use Redis UID provider if the Redis service failed to start");
	}

	@Override
	public Object acquire(Player player) {
		return redisService.doOnPoolReturning(j -> j.incr("uid_incr"));
	}

}
