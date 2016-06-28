package nl.bartpelle.veteres.task;

import nl.bartpelle.veteres.model.World;
import nl.bartpelle.veteres.model.entity.Npc;
import nl.bartpelle.veteres.model.entity.PathQueue;

import java.util.Collection;

/**
 * Created by Bart Pelle on 8/10/2015.
 */
public class NpcPostSyncTask implements Task {

	@Override
	public void execute(World world) {
		world.npcs().forEach(this::postUpdate);
	}

	private void postUpdate(Npc npc) {
		npc.sync().clear();
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
