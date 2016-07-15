package edgeville.model.entity.player;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edgeville.Constants;
import edgeville.model.entity.Player;

public class Punishments {

	private List<String> bannedPlayers;
	
	public List<String> getBannedPlayers() {
		return bannedPlayers;
	}

	public void setBannedPlayers(List<String> bannedPlayers) {
		this.bannedPlayers = bannedPlayers;
	}

	public List<String> getMutedPlayers() {
		return mutedPlayers;
	}

	public void setMutedPlayers(List<String> mutedPlayers) {
		this.mutedPlayers = mutedPlayers;
	}

	public List<String> getBannedIps() {
		return bannedIps;
	}

	public void setBannedIps(List<String> bannedIps) {
		this.bannedIps = bannedIps;
	}

	public List<String> getMutedIps() {
		return mutedIps;
	}

	public void setMutedIps(List<String> mutedIps) {
		this.mutedIps = mutedIps;
	}

	private List<String> mutedPlayers;

	private List<String> bannedIps;
	private List<String> mutedIps;

	public Punishments() {
		loadBannedPlayers();
		loadMutedPlayers();
		loadBannedIps();
		loadMutedIps();
	}

	private void loadMutedIps() {
		// TODO Auto-generated method stub
	}

	private void loadBannedIps() {
		// TODO Auto-generated method stub
	}

	private void loadMutedPlayers() {
		mutedPlayers = new ArrayList<>();
		
		try (BufferedReader br = new BufferedReader(new FileReader(Constants.MUTED_PLAYERS))) {
			String line = br.readLine();

			while (line != null) {
				mutedPlayers.add(line);
				line = br.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadBannedPlayers() {
		bannedPlayers = new ArrayList<>();
		
		try (BufferedReader br = new BufferedReader(new FileReader(Constants.BANNED_PLAYERS))) {
			String line = br.readLine();

			while (line != null) {
				bannedPlayers.add(line);
				line = br.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void removePlayerBan(String username) {
		for (int i = 0; i < bannedPlayers.size(); i++) {
			if (bannedPlayers.get(i).equalsIgnoreCase(username)) {
				bannedPlayers.remove(i);
			}
		}
		updateBannedPlayers();
	}
	
	public void removePlayerMute(String username) {
		for (int i = 0; i < mutedPlayers.size(); i++) {
			if (mutedPlayers.get(i).equalsIgnoreCase(username)) {
				mutedPlayers.remove(i);
			}
		}
		updateMutedPlayers();
	}
	
	public void addPlayerMute(String username) {
		mutedPlayers.add(username);
		updateMutedPlayers();
	}

	public void addPlayerBan(String username) {
		bannedPlayers.add(username);
		updateBannedPlayers();
	}
	
	private void updateMutedPlayers() {
		updateFile(mutedPlayers, Constants.MUTED_PLAYERS);
	}
	
	private void updateBannedPlayers() {
		updateFile(bannedPlayers, Constants.BANNED_PLAYERS);
	}
	
	private void updateFile(List<String> lines, String dir) {
		try {
			File file = new File(dir);

			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);

			for (String username : lines) {
				bw.write(username);
			}
			bw.close();
			
		} catch (Exception e) {
			System.out.println("Couldn't update banned players.");
		}
	}
}
