package info.jupiter;

import info.RS2Server;
import info.jupiter.util.WaitOnCompletionExecutor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Handles the updating of an individual world. <p> 
 * (NOTE, this will be fused with the world class at a later date).
 * @author Advocatus <davidcntt@hotmail.com>
 *
 */
public class WorldUpdater implements Runnable {

	/**
	 * The WorldUpdater instance.
	 */
	private static WorldUpdater updater = null;

	/**
	 * Lazily initialized singleton.
	 * @return the WorldUpdater instance.
	 */
	public static WorldUpdater getWorldUpdater() {
		if (updater == null)
			updater = new WorldUpdater();
		return updater;
	}

	/**
	 * List of all the 'process()' tasks. Aka, TickTasks in Hyperion.
	 */
	private List<Runnable> preUpdate = new ArrayList<Runnable>();
	
	/**
	 * List of all the Player/NPC 'UpdateTask' tasks.
	 */
	private List<Runnable> clientUpdate = new ArrayList<Runnable>();
	
	/**
	 * List of all the reset tasks to be run after updating.
	 */
	private List<Runnable> postUpdate = new ArrayList<Runnable>();
	
	/**
	 * A reference to the WaitOnCompletionExecutor instance for parallel logic.
	 */
	private WaitOnCompletionExecutor executor = RS2Server.getInstance().getThreadPool();

	/**
	 * Run every cycle.
	 */
	@Override
	public void run() {
		preUpdate.clear();
		clientUpdate.clear();
		postUpdate.clear();
		for (Iterator<Npc> it$ = World.getWorld().getNpcs().iterator(); it$.hasNext();) {
			Npc npc = it$.next();
			preUpdate.add(new RunnableNpc(npc, Stage.PRE_UPDATE));
			postUpdate.add(new RunnableNpc(npc, Stage.POST_UPDATE));
		}
		for (Iterator<Player> it$ = World.getWorld().getPlayers().iterator(); it$.hasNext();) {
			Player player = it$.next();
			if (player.getSession().getSocketChannel().isOpen()) {
				preUpdate.add(new RunnablePlayer(player, Stage.PRE_UPDATE));
				clientUpdate.add(new RunnablePlayer(player, Stage.UPDATE));
				postUpdate.add(new RunnablePlayer(player, Stage.POST_UPDATE));
			} else
				it$.remove();
		}
		executor.submit(preUpdate);
		executor.await();
		executor.submit(clientUpdate);
		executor.await();
		executor.submit(postUpdate);
		executor.await();
	}
}
