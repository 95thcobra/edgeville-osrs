package edgeville.services.serializers;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;

import edgeville.model.Tile;
import edgeville.model.entity.Player;
import edgeville.model.entity.player.Privilege;
import edgeville.model.entity.player.Skills;
import edgeville.model.item.Item;
import edgeville.model.uid.UIDProvider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.function.Consumer;

/**
 * Created by Bart on 5-3-2015.
 * <p>
 * Simple default serializer to <b>only</b> use in single-server setups because
 * it uses a local file to serialize the player data to by means of GSON.
 */
public class JSONFileSerializer extends PlayerSerializer {

	private static final Logger logger = LogManager.getLogger(JSONFileSerializer.class);

	private Gson gson;

	/**
	 * The folder containing the character files.
	 */
	private File characterFolder = new File("data/characters");

	public JSONFileSerializer(UIDProvider provider) {
		super(provider);

		gson = new GsonBuilder().setPrettyPrinting().create(); // TODO
																// configuration

		/* Create folder if missing */
		characterFolder.mkdirs();
	}

	@Override
	public boolean loadPlayer(Player player, Object uid, String password, Consumer<PlayerLoadResult> fn) {
		File characterFile = new File(characterFolder, player.name() + ".json");

		// If the file does not exist, let the caller know.
		if (!characterFile.exists()) {
			fn.accept(PlayerLoadResult.OK);
			return true;
		}

		try {
			fn.accept(loadPlayer(player, new FileInputStream(characterFile), password));
		} catch (FileNotFoundException e) {
			logger.error("Could not decode JSON player data for {} because the file was missing!", player.name(), e);
			fn.accept(PlayerLoadResult.INVALID_DETAILS);
		}

		return true;
	}

	public PlayerLoadResult loadPlayer(Player player, InputStream inputStream, String password) {
		JsonElement element = new JsonParser().parse(new InputStreamReader(inputStream));

		if (element.isJsonObject()) {
			JsonObject rootObject = element.getAsJsonObject();

			// Check password
			if (!rootObject.get("password").getAsString().equals(password))
				return PlayerLoadResult.INVALID_DETAILS;

			/* Basic information */
			String displayName = rootObject.get("displayName").getAsString();
			Privilege privilege = Privilege.valueOf(rootObject.get("privilege").getAsString());
			Tile tile = gson.fromJson(rootObject.get("tile"), Tile.class);
			int migration = rootObject.get("migration").getAsInt();

			/* Construct the player */
			player.displayName(displayName);
			player.privilege(privilege);
			player.migration(migration);
			player.move(tile);

			player.setTile(tile);

			/* Skill information */
			JsonArray skills = rootObject.get("skills").getAsJsonArray();
			for (int i = 0 ; i < skills.size(); i++) {
				JsonObject skill = skills.get(i).getAsJsonObject();
				player.skills().levels()[i] = skill.get("level").getAsInt();
				player.skills().xp()[i] = skill.get("xp").getAsInt();
			}
			player.skills().recalculateCombat();

			/* inventory */
			JsonArray inventory = rootObject.get("inventory").getAsJsonArray();
			for (int i = 0; i < player.inventory().size(); i++) {
				Item item = gson.fromJson(inventory.get(i), Item.class);
				player.inventory().set(i, item);
			}

			/* equipment */
			JsonArray equipment = rootObject.get("equipment").getAsJsonArray();
			for (int i = 0; i < player.equipment().size(); i++) {
				Item item = gson.fromJson(equipment.get(i), Item.class);
				player.equipment().set(i, item);
			}
			
			/* varps */
			JsonArray varps = rootObject.get("varps").getAsJsonArray();
			for (int i = 0 ; i < varps.size(); i++) {
				JsonObject varp = varps.get(i).getAsJsonObject();
				player.varps().setVarp(varp.get("id").getAsInt(), varp.get("value").getAsInt());
			}

			return PlayerLoadResult.OK;
		}

		return PlayerLoadResult.INVALID_DETAILS;
	}

	@Override
	public void savePlayer(Player player) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("username", player.getUsername());
		jsonObject.addProperty("displayName", player.getDisplayName());
		jsonObject.addProperty("password", player.getPassword());
		jsonObject.add("tile", gson.toJsonTree(player.getTile()));
		jsonObject.add("privilege", gson.toJsonTree(player.getPrivilege()));
		jsonObject.addProperty("migration", player.migration());

		/* Inventory */
		JsonArray inventory = new JsonArray();
		for (int i = 0; i < player.inventory().size(); i++) {
			inventory.add(gson.toJsonTree(player.inventory().get(i)));
		}
		jsonObject.add("inventory", inventory);

		/* equipment */
		JsonArray equipment = new JsonArray();
		for (int i = 0; i < player.equipment().size(); i++) {
			equipment.add(gson.toJsonTree(player.equipment().get(i)));
		}
		jsonObject.add("equipment", equipment);
		
		/* varps */
		JsonArray varps = new JsonArray();
		for (int i = 0; i < player.varps().getVarps().length; i++) {
			int varppp = player.varps().getVarps()[i];
			if (varppp == 0) {
				continue;
			}
			JsonObject varp = new JsonObject();
			varp.addProperty("id", i);
			varp.addProperty("value", varppp);
			varps.add(varp);
		}
		jsonObject.add("varps", varps);

		/* Skills */
		JsonArray skills = new JsonArray();
		for (int i = 0; i < Skills.SKILL_COUNT; i++) {
			JsonObject object = new JsonObject();
			object.addProperty("level", player.skills().level(i));
			object.addProperty("xp", player.skills().xp()[i]);
			skills.add(object);
		}
		jsonObject.add("skills", skills);
		// end

		File characterFile = new File(characterFolder, player.getUsername() + ".json");
		try (FileWriter out = new FileWriter(characterFile)) {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			out.write(gson.toJson(jsonObject));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
