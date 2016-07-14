package edgeville.database;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import edgeville.model.entity.Player;

public class ForumIntegration {

	// public static getInstance() {
	// todo poling
	// }

	// private static Connection conn; //// new MySQLDatabase("edgeville.org",
	// 3306, "edgevill_forum", "edgevill_forum",
	// "Ph,g(n$2g[OD");

	public static boolean insertHiscore(Player player) {
		if ((System.currentTimeMillis() - player.getLastHiscoresUpdate()) < (10 * 60 * 1000)) {
			return false;
		}
		player.setLastHiscoresUpdate(System.currentTimeMillis());
		new Thread() {
			@Override
			public void run() {
				insertHiscore2(player);
			}
		}.start();
		return true;
	}

	private static boolean insertHiscore2(Player player) {
		try {
			// Connect to database
			Connection conn = DriverManager.getConnection("jdbc:mysql://edgeville.org:3306/sky_hiscores",
					"sky_hiscores", "3!zbr,vU2S%C");

			// Create statement
			// Statement stmt = conn.createStatement();
			PreparedStatement pstmt = conn.prepareStatement(
					"INSERT INTO user_hiscore (member_id, kills, deaths) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE kills = ?, deaths = ?");

			pstmt.setInt(1, player.getMemberId());
			pstmt.setInt(2, player.getKills());
			pstmt.setInt(3, player.getDeaths());
			pstmt.setInt(4, player.getKills());
			pstmt.setInt(5, player.getDeaths());

			pstmt.executeUpdate();

			// Execute query
			// ResultSet rs = stmt.executeQuery("INSERT INTO user_hiscore
			// (member_id, kills, deaths, elo) VALUES ()");

			// Results
			// while(rs.next()) {
			// System.out.println(rs.getString("name") + ",
			// email:"+rs.getString("email"));
			// }

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}

	private static final String CRYPTION_ID = "28Vqeuyr";

	/**
	 * -1 = mysql down, 1 is wrong pass, 2 = success
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public static int checkUser(String username, String password) {
		int response = -1;
		try {
			String urlString = "http://edgeville.org/game/login.php?security=" + CRYPTION_ID + "&name="
					+ username.replace(" ", "_") + "&pass=" + password;

			System.out.println(urlString);

			URL url = new URL(urlString);
			URLConnection urlConnection = url.openConnection();
			urlConnection.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
			// ((HttpURLConnection) urlConnection).setRequestMethod("POST");
			// urlConnection.setDoOutput(true);
			InputStream is = urlConnection.getInputStream();

			// InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			String line = br.readLine();
			System.out.println(line);
			response = Integer.parseInt(line);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}
}