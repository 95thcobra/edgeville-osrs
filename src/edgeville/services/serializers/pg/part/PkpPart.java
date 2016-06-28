package edgeville.services.serializers.pg.part;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import edgeville.model.AttributeKey;
import edgeville.model.Tile;
import edgeville.model.entity.Player;

/**
 * Created by Bart on 8/10/2015.
 */
public class PkpPart implements PgJsonPart {

	@Override
	public void decode(Player player, ResultSet resultSet) throws SQLException {
		player.attrib(AttributeKey.PK_POINTS, resultSet.getInt("pkp"));
	}

	@Override
	public void encode(Player player, PreparedStatement characterUpdateStatement) throws SQLException {
		characterUpdateStatement.setInt(8, player.attrib(AttributeKey.PK_POINTS, 0));
	}

}
