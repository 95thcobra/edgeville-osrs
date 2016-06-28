package nl.bartpelle.veteres.task;

import nl.bartpelle.veteres.model.Tile;
import nl.bartpelle.veteres.model.World;
import nl.bartpelle.veteres.model.entity.Npc;
import nl.bartpelle.veteres.model.entity.PathQueue;
import nl.bartpelle.veteres.model.entity.Player;
import nl.bartpelle.veteres.net.message.game.DisplayMap;

import java.util.Collection;

/**
 * Created by Bart Pelle on 8/10/2015.
 */
public class NpcPreSyncTask implements Task {

	@Override
	public void execute(World world) {
		world.npcs().forEach(this::preUpdate);
	}

	private void preUpdate(Npc npc) {
		// Process path
		if (!npc.pathQueue().empty()) {
			PathQueue.Step walkStep = npc.pathQueue().next();
			int walkDirection = PathQueue.calculateDirection(npc.tile().x, npc.tile().z, walkStep.x, walkStep.z);
			int runDirection = -1;
			npc.tile(new Tile(walkStep.x, walkStep.z, npc.tile().level));

			if ((walkStep.type == PathQueue.StepType.FORCED_RUN || npc.pathQueue().running()) && !npc.pathQueue().empty() && walkStep.type != PathQueue.StepType.FORCED_WALK) {
				PathQueue.Step runStep = npc.pathQueue().next();
				runDirection = PathQueue.calculateDirection(npc.tile().x, npc.tile().z, runStep.x, runStep.z);
				npc.tile(new Tile(walkStep.x, walkStep.z, npc.tile().level));
			}

			npc.sync().step(walkDirection, runDirection);
		}
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
