package nl.bartpelle.veteres.task;

import nl.bartpelle.veteres.model.World;
import nl.bartpelle.veteres.model.entity.Player;

import java.util.Collection;

/**
 * Created by Bart on 5-3-2015.
 */
public class PlayerProcessingTask implements Task {

	@Override
	public void execute(World world) {
		world.players().forEachShuffled(Player::cycle);
	}

	@Override
	public Collection<SubTask> createJobs(World world) {
		return null;
	}

	@Override
	public boolean isAsyncSafe() {
		return false;
	}

}
