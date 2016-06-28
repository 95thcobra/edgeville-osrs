package nl.bartpelle.veteres.services.redis;

import com.typesafe.config.Config;
import nl.bartpelle.veteres.GameServer;
import nl.bartpelle.veteres.aquickaccess.Constants;
import nl.bartpelle.veteres.services.Service;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by Bart on 4-3-2015.
 *
 * Redis service.
 */
public class RedisService implements Service {

	private static final Logger logger = LogManager.getLogger(RedisService.class);

	private GameServer server;
	private JedisPool jedis;
	private String host;
	private String password;
	private int port;
	private int timeout;

	@Override
	public void setup(GameServer server/*, Config serviceConfig*/) {
		this.server = server;

		/*
		host = serviceConfig.hasPath("host") ? serviceConfig.getString("host") : "localhost";
		password = serviceConfig.hasPath("password") ? serviceConfig.getString("password") : null;
		port = serviceConfig.hasPath("port") ? serviceConfig.getInt("port") : 6379;
		timeout = serviceConfig.hasPath("timeout") ? serviceConfig.getInt("timeout") : 1000;
		*/
		host = Constants.REDIS_HOST;
		password = Constants.REDIS_PASSWORD;
		port = Constants.REDIS_PORT;
		timeout = Constants.REDIS_TIMEOUT;

		logger.info("Enabling Redis service.");
	}

	@Override
	public boolean start() {
		jedis = new JedisPool(new GenericObjectPoolConfig(), host, port, timeout, password, Protocol.DEFAULT_DATABASE, null);

		try {
			jedis.getResource().connect(); // Test connection
		} catch (Exception e){
			logger.error("Could not connect to Redis service on {}:{} with timeout {}.", host, port, timeout);
			logger.error("\t" + e);
			return false;
		}

		logger.info("Connected to Redis service on {}:{}.", host, port);
		return true;
	}

	@Override
	public boolean stop() {
		jedis.destroy();
		return true;
	}

	@Override
	public boolean isAlive() {
		try {
			return jedis.getResource().isConnected();
		} catch (JedisConnectionException e) {
			return false;
		}
	}

	public void doOnPool(Consumer<Jedis> todo) {
		Jedis j = jedis.getResource();
		todo.accept(j);
		j.close();
	}

	public Object doOnPoolReturning(Function<Jedis, Object> todo) {
		Jedis j = jedis.getResource();
		Object v = todo.apply(j);
		j.close();
		return v;
	}

}
