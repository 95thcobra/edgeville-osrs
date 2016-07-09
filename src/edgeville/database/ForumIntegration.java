package edgeville.database;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import edgeville.model.entity.Player;

public class ForumIntegration {

	private Connection conn; //// new MySQLDatabase("edgeville.org", 3306, "edgevill_forum", "edgevill_forum",
		//	"Ph,g(n$2g[OD");

	public void init() {
			try {
				// Connect to database
				conn = DriverManager.getConnection("jdbc:mysql://185.62.188.4:3306/edgevill_forum", "edgevill_forum", "Ph,g(n$2g[OD");
				
				// Create statement
				Statement stmt = conn.createStatement();
				
				// Execute query
				ResultSet rs = stmt.executeQuery("select * from core_memebers");
				
				// Results
				while(rs.next()) {
					System.out.println(rs.getString("name") + ", email:"+rs.getString("email"));
				}
			
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
			

	private static final int CRYPTION_ID = 85461564;

	public int checkUser(Player player) {
		try {
			String urlString = "http://www.edgeville.org/db/login.php?security=" + CRYPTION_ID + "&name="
					+ player.getUsername().replace(" ", "_") + "&pass=" + player.getPassword();

			System.out.println(urlString);

			// HttpURLConnection conn = (HttpURLConnection) new
			// URL(urlString).openConnection();
			URL url = new URL(urlString);
			URLConnection urlConnection = url.openConnection();
			((HttpURLConnection) urlConnection).setRequestMethod("POST");
			urlConnection.setDoOutput(true);
			InputStream is = urlConnection.getInputStream();

			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "iso-8859-1"));

			String line = br.readLine();
			//System.out.println(line);
			
			 while(true) { line = br.readLine(); if (line == null) { break; }
			  System.out.println(line); }
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		return 1;
	}
}