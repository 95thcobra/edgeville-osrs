package nl.bartpelle.veteres.services.serializers.pg.part;

import nl.bartpelle.veteres.model.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Bart on 8/10/2015.
 */
public interface PgJsonPart {

	public void decode(Player player, ResultSet resultSet) throws SQLException;

	public void encode(Player player, PreparedStatement characterUpdateStatement) throws SQLException;

}
