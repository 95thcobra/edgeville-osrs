package nl.bartpelle.veteres.task;

import nl.bartpelle.veteres.ServerProcessor;
import nl.bartpelle.veteres.model.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;

/**
 * Created by Bart on 3-2-2015.
 */
public class ScriptProcessingTask implements Task {

	private static final Logger logger = LogManager.getLogger(ServerProcessor.class);

	@Override
	public void execute(World world) {
		world.cycle();
		//world.server().scriptExecutor().cycle();
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
