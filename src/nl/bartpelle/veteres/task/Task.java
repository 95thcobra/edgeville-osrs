package nl.bartpelle.veteres.task;

import nl.bartpelle.veteres.model.World;

import java.util.Collection;
import java.util.concurrent.Callable;

/**
 * Created by Bart Pelle on 8/23/2014.
 */
public interface Task {

	public void execute(World world);

	public boolean isAsyncSafe();

	public Collection<SubTask> createJobs(World world);

}
