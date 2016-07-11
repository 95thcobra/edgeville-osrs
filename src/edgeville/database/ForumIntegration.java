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
	
//	public static getInstance() {
		//todo poling
	//}

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
			

	private static final String CRYPTION_ID = "28Vqeuyr";
	
	/**
	 * -1 = mysql down, 1 is wrong pass, 2 = success 
	 * @param username
	 * @param password
	 * @return
	 */
	public static int checkUser(String username, String password) {
		int response = -1;
		try {
			String urlString = "http://scripts.edgeville.org/login.php?security=" + CRYPTION_ID + "&name="
					+ username.replace(" ", "_") + "&pass=" + password;

			System.out.println(urlString);

			URL url = new URL(urlString);
			URLConnection urlConnection = url.openConnection();
			//((HttpURLConnection) urlConnection).setRequestMethod("POST");
			//urlConnection.setDoOutput(true);
			InputStream is = urlConnection.getInputStream();

			//InputStreamReader isr = new InputStreamReader(is);
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