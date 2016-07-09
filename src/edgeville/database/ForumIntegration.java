package edgeville.database;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import edgeville.model.entity.Player;

public class ForumIntegration {

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
			BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),  "iso-8859-1"));

			String line = br.readLine();
			System.out.println(line);
			/*
			 * while(true) { line = in.readLine(); if (line == null) { break; }
			 * System.out.println(line); }
			 */
			
		 

		} catch (Exception e) {
			e.printStackTrace();
		}
		return 1;
	}
}