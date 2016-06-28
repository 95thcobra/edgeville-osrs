package nl.bartpelle.veteres.task;

import nl.bartpelle.veteres.model.World;
import nl.bartpelle.veteres.model.entity.Npc;

import java.util.Collection;

/**
 * Created by Bart on 8/26/2015.
 */
public class NpcProcessingTask implements Task {

	@Override
	public void execute(World world) {
		world.npcs().forEach(Npc::cycle);
	}

	@Override
	public boolean isAsyncSafe() {
		return false;
	}

	@Override
	public Collection<SubTask> createJobs(World world) {
		return null;
	}

}
