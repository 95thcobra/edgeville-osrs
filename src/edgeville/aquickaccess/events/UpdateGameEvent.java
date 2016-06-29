package edgeville.aquickaccess.events;

import java.io.File;
import java.io.IOException;

import edgeville.event.Event;
import edgeville.event.EventContainer;
import edgeville.model.entity.Player;
import edgeville.net.message.game.AddGroundItem;
import edgeville.net.message.game.SetMapBase;

public class UpdateGameEvent extends Event {

	private Player player;
	private int tick;
	private int ticksForRestart;

	public UpdateGameEvent(Player player, int ticksForRestart) {
		this.player = player;
		this.ticksForRestart = ticksForRestart;
		tick = 0;
	}

	@Override
	public void execute(EventContainer container) {
		if (tick == ticksForRestart) {
			player.world().players().forEach(p -> {
				p.logout();
			});

			try {
				ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", "Run Server.bat");
				processBuilder.directory(new File("./"));
				processBuilder.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			System.out.println("Server has shut down.");
			System.exit(0);
			container.stop();
		}
		
		tick++;
	}
}
