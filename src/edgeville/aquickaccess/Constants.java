package edgeville.aquickaccess;

import edgeville.model.Tile;
import edgeville.model.uid.UIDProvider;
import edgeville.model.uid.providers.RedisUIDProvider;
import edgeville.model.uid.providers.SimpleUIDProvider;

/**
 * Created by Sky on 28-6-2016.
 */
public class Constants {

	public static final String SERVER_NAME = "Edgeville";
	public static final int REVISION = 86;
	public static final boolean FORCE_REVISION = false;
	public static final String CACHE_DIR = "./data/filestore";
	public static final String MAP_KEYS_DIR = "./data/map/keys.bin";

	// Netty config
	public static final int PORT = 43594;
	public static final String IP_ADDRESS = "localhost";
	public static final int ACCEPT_THREADS = 1;
	public static final int IO_THREADS = 2;
	
	// UID Provider
	public static final Class UID_PROVIDER = SimpleUIDProvider.class;
	
	// Lazy load definitions
	public static final boolean LAZY_DEFINITIONS = true;
	public static final Tile SPAWN_TILE = Locations.EDGEVILLE.getTile();
	
	public static final int WORLD_ID = 1; 
	public static final boolean WORLD_EMULATION = true; 
	public static final int COMBAT_XP_RATE_MULTIPLIER = 1; 
	public static final int SKILLING_XP_RATE_MULTIPLIER = 1;
	
	// PgSQL
	public static final String PGSQL_USER ="postgres";
	public static final String PGSQL_PASSWORD = "guthixos";
	public static final String PGSQL_HOST = "localhost";
	public static final int PGSQL_PORT = 5433;
	public static final String PGSQL_DATABASE = "osrs";
	
	// Redis
	public static final String REDIS_HOST = "localhost";
	public static final String REDIS_PASSWORD = "??????????";
	public static final int REDIS_PORT = 6379;
	public static final int REDIS_TIMEOUT = 2000;
	

	public static final boolean MIGRATIONS_ENABLED = false;
}
